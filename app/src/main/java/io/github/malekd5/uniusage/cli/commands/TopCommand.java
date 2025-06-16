package io.github.malekd5.uniusage.cli.commands;

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
import java.util.Set;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import io.github.malekd5.uniusage.data.LogEntry;
import io.github.malekd5.uniusage.utils.Utils;

import java.util.Map.Entry;

@Parameters(commandDescription = "Brute force check for top K operations and user percentage, useful for smaller datasets")
public class TopCommand implements Runnable {

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File file;

    @Parameter(names = { "--k",
            "--top-k" }, description = "Top K operations to display", defaultValueDescription = "Defaults to 2")
    private int topK = 2;

    @Override
    public void run() {
        if (!file.exists()) {
            System.out.println("File does not exist: " + file.getAbsolutePath());
            return;
        }

        if (!file.canRead()) {
            System.out.print("File cannot be read: " + file.getAbsolutePath());
            return;
        }

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
        if (totalUsers == 0) {
            System.out.println("No users found.");
            return;
        }

        for (int i = 0; i < Math.min(topK, sortedOps.size()); i++) {
            Entry<String, Set<String>> entry = sortedOps.get(i);
            String operation = entry.getKey();
            int userCount = entry.getValue().size();

            Utils.printOperationUsage(operation, userCount, totalUsers);
        }
    }

}
