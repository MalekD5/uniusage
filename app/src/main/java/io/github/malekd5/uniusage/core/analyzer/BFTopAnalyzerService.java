package io.github.malekd5.uniusage.core.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.github.malekd5.uniusage.core.data.LogEntry;

public class BFTopAnalyzerService {

    public BFTopAnalysisResult analyze(File file, int topK) {
        Map<String, Set<String>> operationUsers = new HashMap<>();
        Set<String> allUsers = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();

                var entryOptional = LogEntry.parse(line);

                if (entryOptional.isEmpty()) {
                    System.out.println("Skipping malformed line " + lineCount + ": " + line);
                    continue;
                }

                var entry = entryOptional.get();

                String user = entry.userId();
                String operation = entry.operation();

                allUsers.add(user);
                operationUsers.computeIfAbsent(operation, k -> new HashSet<>()).add(user);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Entry<String, Set<String>>> sortedOps = new ArrayList<>(operationUsers.entrySet());
        sortedOps.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

        int totalUsers = allUsers.size();

        return new BFTopAnalysisResult(sortedOps.subList(0, Math.min(topK, sortedOps.size())), totalUsers);
    }

    public record BFTopAnalysisResult(List<Entry<String, Set<String>>> topKOperations, long totalUsers) {
    }
}
