public record PageRequest(int limit, String cursor, String sortBy, boolean isAscending) {
    public PageRequest {
        if (limit <= 0 || limit > 100) limit = 20; // Enforce max limits
    }
}

public record PageResult<T>(java.util.List<T> data, String nextCursor, boolean hasNext) {}

public record CursorData(Object sortValue, Long id) {}