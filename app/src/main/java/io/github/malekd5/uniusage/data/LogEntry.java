package io.github.malekd5.uniusage.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public record LogEntry(Instant timestamp, String userId, String operation) {

    public static Optional<LogEntry> parse(String line) {
        if (line.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length < 2)
            throw new IllegalArgumentException("Malformed line: " + line);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp;
        try {
            timestamp = LocalDateTime.parse(parts[0] + " " + parts[1], formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Malformed timestamp: " + parts[0] + " " + parts[1], e);
        }

        String[] pathParts = parts[2].split("/");
        if (pathParts.length < 3)
            throw new IllegalArgumentException("Malformed path: " + parts[2]);

        String user = pathParts[1];
        String operation = pathParts[2];

        return Optional.of(new LogEntry(timestamp.atZone(ZoneOffset.UTC).toInstant(), user, operation));
    }

}
