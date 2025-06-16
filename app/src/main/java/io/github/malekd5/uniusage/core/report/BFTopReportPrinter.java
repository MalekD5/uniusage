package io.github.malekd5.uniusage.core.report;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.github.malekd5.uniusage.utils.Utils;

public class BFTopReportPrinter {
    public static void printReport(List<Map.Entry<String, Set<String>>> topKOperations, long totalUsers, int topK) {
        for (int i = 0; i < Math.min(topK, topKOperations.size()); i++) {
            Entry<String, Set<String>> entry = topKOperations.get(i);
            String operation = entry.getKey();
            int userCount = entry.getValue().size();

            Utils.printOperationUsage(operation, userCount, totalUsers);
        }
    }
}
