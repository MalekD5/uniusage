package io.github.malekd5.uniusage.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Ordering;

public final class Utils {

    private Utils() {
    }

    public static final int THREAD_COUNT;

    static {
        final int cores = Runtime.getRuntime().availableProcessors();
        THREAD_COUNT = Math.max(1, cores);
    }

    public final static CharsetDecoder UTF8_DECODER = StandardCharsets.UTF_8.newDecoder();

    public static long calculateStart(int currrentIndex, long chunkSize) {
        return currrentIndex * chunkSize;
    }

    public static long calculateEnd(int currrentIndex, long chunkSize, long fileSize) {
        return (currrentIndex == THREAD_COUNT - 1) ? fileSize : (currrentIndex + 1) * chunkSize;
    }

    public static long findNextNewline(FileChannel fc, long position, long fileSize) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1);
        long pos = position;
        while (pos < fileSize) {
            bb.clear();
            fc.position(pos);
            if (fc.read(bb) != 1)
                break;
            bb.flip();
            if (bb.get() == '\n') {
                return pos + 1;
            }
            pos++;
        }
        return fileSize;
    }

    public static long findPrevNewline(FileChannel fc, long position) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1);
        long pos = position - 1;
        while (pos >= 0) {
            bb.clear();
            fc.position(pos);
            if (fc.read(bb) != 1)
                break;
            bb.flip();
            byte b = bb.get();
            if (b == '\n') {
                return pos + 1;
            }
            pos--;
        }
        return 0;
    }

    public static List<Map.Entry<String, ThreadSafeHLL>> getTopK(
            Map<String, ThreadSafeHLL> map, int k) {

        Ordering<Map.Entry<String, ThreadSafeHLL>> byCardinalityDesc = Ordering.natural()
                .onResultOf((Map.Entry<String, ThreadSafeHLL> entry) -> entry.getValue().cardinality());

        return new ArrayList<>(byCardinalityDesc.greatestOf(map.entrySet(), k));
    }

    public static void printOperationUsage(String operation, long count, long totalUsers) {
        if (totalUsers == 0) {
            System.out.printf("Operation \"%s\" has no measurable usage (0 total users).%n", operation);
            return;
        }

        double percentage = count * 100.0 / totalUsers;

        System.out.printf(
                "Operation \"%s\" is used by %.2f%% of our users (%d users).%n",
                operation, percentage, count);
    }

    public static double calculateStandardError(int precision) {
        int m = 1 << precision;
        return (1.04 / Math.sqrt(m)) * 100;
    }

}
