package io.github.malekd5.uniticker.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.malekd5.uniusage.utils.ThreadSafeHLL;
import io.github.malekd5.uniusage.utils.Utils;

public class UtilsTest {

    @Test
    void testCalculateStandardError() {
        int precision = 18;
        double standardError = Utils.calculateStandardError(precision);

        Assertions.assertEquals(standardError, 0.2, 0.1);
    }

    @Test
    void testCalculateStart() {
        int currrentIndex = 0;
        int chunkSize = 10;

        long start = Utils.calculateStart(currrentIndex, chunkSize);
        Assertions.assertEquals(start, 0);

        currrentIndex = 1;
        start = Utils.calculateStart(currrentIndex, chunkSize);
        Assertions.assertEquals(start, 10);

        currrentIndex = 5;
        start = Utils.calculateStart(currrentIndex, chunkSize);
        Assertions.assertEquals(start, 50);
    }

    @Test
    void testCalculateEnd() {
        int currrentIndex = 0;
        int chunkSize = 10;

        long end = Utils.calculateEnd(currrentIndex, chunkSize, 10);
        Assertions.assertEquals(end, 10);

        currrentIndex = 1;
        end = Utils.calculateEnd(currrentIndex, chunkSize, 10);
        Assertions.assertEquals(end, 20);

        currrentIndex = 5;
        end = Utils.calculateEnd(currrentIndex, chunkSize, 10);
        Assertions.assertEquals(end, 60);
    }

    @Test
    void testGetTopK() {
        int precision = 18;
        Map<String, ThreadSafeHLL> map = new HashMap<>();
        map.put("a", new ThreadSafeHLL(precision));
        map.put("b", new ThreadSafeHLL(precision));
        map.put("c", new ThreadSafeHLL(precision));
        map.put("d", new ThreadSafeHLL(precision));
        map.put("e", new ThreadSafeHLL(precision));

        map.get("a").offer("a");
        map.get("a").offer("b");
        map.get("a").offer("c");
        map.get("c").offer("c");
        map.get("d").offer("d");
        map.get("e").offer("e");
        map.get("e").offer("c");

        int k = 2;
        List<Map.Entry<String, ThreadSafeHLL>> topK = Utils.getTopK(map, k);

        Assertions.assertEquals(topK.size(), 2);
        Assertions.assertEquals(topK.get(0).getKey(), "a");
        Assertions.assertEquals(topK.get(1).getKey(), "e");
    }

}
