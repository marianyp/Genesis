package dev.mariany.genesis.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ShapeComparator {
    public static boolean haveSameShape(List<String> firstShape, List<String> secondShape) {
        if (firstShape.size() != secondShape.size()) {
            return false;
        }

        int height = firstShape.size();
        int width = firstShape.getFirst().length();

        for (String row : firstShape) {
            if (row.length() != width) {
                throw new IllegalArgumentException("firstShape has inconsistent row lengths.");
            }
        }

        for (String row : secondShape) {
            if (row.length() != width) {
                return false;
            }
        }

        int[][] normalizedFirstShape = normalizeShape(firstShape);
        int[][] normalizedSecondShape = normalizeShape(secondShape);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (normalizedFirstShape[y][x] != normalizedSecondShape[y][x]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static int[][] normalizeShape(List<String> pattern) {
        Map<Character, Integer> symbolMap = new HashMap<>();
        AtomicInteger nextId = new AtomicInteger();
        int height = pattern.size();
        int width = pattern.getFirst().length();
        int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            String row = pattern.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                if (c == ' ' || c == '\0') {
                    result[y][x] = -1;
                } else {
                    symbolMap.computeIfAbsent(c, k -> nextId.getAndIncrement());
                    result[y][x] = symbolMap.get(c);
                }
            }
        }
        return result;
    }

    public static Optional<Character> getMappedKey(
            List<String> referenceShape,
            List<String> mappedShape,
            char referenceChar
    ) {
        for (int row = 0; row < referenceShape.size(); row++) {
            String firstRow = referenceShape.get(row);
            String secondRow = mappedShape.get(row);

            for (int col = 0; col < firstRow.length(); col++) {
                if (firstRow.charAt(col) == referenceChar) {
                    return Optional.of(secondRow.charAt(col));
                }
            }
        }

        return Optional.empty();
    }
}
