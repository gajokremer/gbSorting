public class Test {
    private final static int MAX_TASK_SIZE = 12000000;

    public static void main(String[] args) {
        int totalLines = 500000000;

        int[][] ranges = calculateRanges(totalLines, MAX_TASK_SIZE);
        System.out.println("Total ranges: " + ranges.length);
        for (int[] range : ranges) {
            System.out.println(range[0] + " - " + range[1]);
        }
    }

    public static int[][] calculateRanges(int totalLines, int x) {
        System.out.println("=> X: " + x);
        System.out.println("=> Total lines: " + totalLines);

        if (totalLines <= 0 || x <= 0) {
            throw new IllegalArgumentException("Total lines and parts must be positive integers.");
        }

        if (totalLines < x) {
            // If totalLines is less than x, return a matrix with a single array
            int[][] singleRange = new int[][] { { 1, totalLines } };
            return singleRange;
        }

        int totalParts = totalLines / x + (totalLines % x == 0 ? 0 : 1);
        int remainder = totalLines % x;

        int[][] ranges = new int[totalParts][2];
        int startIdx = 1; // Start counting from 1

        for (int i = 0; i < totalParts; i++) {
            int endIdx = Math.min(startIdx + x - 1, totalLines); // Adjust the end index
            ranges[i][0] = startIdx;
            ranges[i][1] = endIdx;
            startIdx = endIdx + 1; // Adjust the start index
        }

        return ranges;
    }

}
