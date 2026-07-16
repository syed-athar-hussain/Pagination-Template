import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.util.ArrayList;
import java.util.List;

public class PostgresPaginationPlugin {

    public static <T> PageResult<T> paginate(
            JdbcTemplate jdbcTemplate, 
            String tableName, 
            PageRequest request, 
            RowMapper<T> mapper,
            CursorValueExtractor<T> extractor) { // Functional interface to extract values from T

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        List<Object> params = new ArrayList<>();
        CursorData cursor = CursorUtil.decode(request.cursor());

        if (cursor != null) {
            String operator = request.isAscending() ? ">" : "<";
            sql.append(" WHERE (").append(request.sortBy()).append(", id) ")
               .append(operator).append(" (?, ?)");
            params.add(cursor.sortValue());
            params.add(cursor.id());
        }

        String sortOrder = request.isAscending() ? "ASC" : "DESC";
        sql.append(" ORDER BY ")
           .append(request.sortBy()).append(" ").append(sortOrder).append(", ")
           .append("id ").append(sortOrder)
           .append(" LIMIT ?");
        params.add(request.limit() + 1);

        List<T> results = jdbcTemplate.query(sql.toString(), mapper, params.toArray());

        boolean hasNext = results.size() > request.limit();
        if (hasNext) results.remove(results.size() - 1);

        String nextCursor = null;
        if (!results.isEmpty()) {
            T lastItem = results.get(results.size() - 1);
            nextCursor = CursorUtil.encode(extractor.getSortValue(lastItem), extractor.getId(lastItem));
        }

        return new PageResult<>(results, nextCursor, hasNext);
    }
    
    public interface CursorValueExtractor<T> {
        Object getSortValue(T item);
        Long getId(T item);
    }
}