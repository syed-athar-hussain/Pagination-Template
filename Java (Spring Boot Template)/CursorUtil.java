import java.util.Base64;

public class CursorUtil {
    public static String encode(Object sortValue, Long id) {
        if (sortValue == null || id == null) return null;
        String raw = sortValue.toString() + "|" + id;
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public static CursorData decode(String encodedCursor) {
        if (encodedCursor == null || encodedCursor.isBlank()) return null;
        String decoded = new String(Base64.getDecoder().decode(encodedCursor));
        String[] parts = decoded.split("\\|");
        // Parse sortValue appropriately based on your column type (String, Long, Timestamp)
        return new CursorData(parts[0], Long.parseLong(parts[1])); 
    }
}