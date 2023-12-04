package worker;

import com.zeroc.Ice.Current;

import fileAccessor.FileAccessor;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SorterI implements Services.Sorter {

    // ForkJoinMasterPrx master;

    // public SorterI(ForkJoinMasterPrx master) {
    // this.master = master;
    // }

    // private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private FileAccessor fileAccessor;
    private boolean running = false;

    public SorterI(FileAccessor fileAccessor) {
        this.fileAccessor = fileAccessor;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        System.out.println("\nSorter running?: " + running);
    }

    // @Override
    // public String receiveTask(String content, Current current) {
    // // System.out.println("\nFile content received from Server -> \n");
    // // System.out.println(content);
    // return sort(content);
    // }

    @Override
    public void receiveTaskRange(String dataPath, int start, int end, long sorterId, Current current) {
        if (!running) {
            System.out.println("\n-> Task cannot be processed. Application is not running.");
            return;
        }

        String content = fileAccessor.readContent(dataPath, start, end);
        System.out.println("\nRead content from " + start + " to " + end + ": "
                + content.split("\n").length + " lines");
        // System.out.println("\nSorter Content: \n{" + content+"}");

        long startTime = System.currentTimeMillis();

        // String sortedContent = sort(content);
        String sortedContent = forkJoinSort(content);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        long timeInSeconds = executionTime / 1000;
        System.out.println("\nSorting execution time: " + executionTime + "ms" + " (" + timeInSeconds + "s)\n");

        String outputFileLocation = "/opt/share/gb/outputs/";
        String outputFileName = "output" + sorterId + ".txt";
        fileAccessor.createOutputFile(outputFileLocation, outputFileName);
        fileAccessor.writeToFile(outputFileLocation.concat(outputFileName), sortedContent);
    }

    private String sort(String s) {

        String result = "";

        if (running) {
            // System.out.println("\nFile content received from Server");
            System.out.println("\nSorting file content...\n");

            String[] lines = s.split("\n");

            // Sort the array of lines alphabetically
            Arrays.sort(lines);

            // Join the sorted lines into a single string with newline separation
            result = String.join("\n", lines);
        }
        return result;
    }

    private class SortTask extends RecursiveTask<String> {
        private String str;
        private static final int THRESHOLD = 2000000; // You can adjust this value

        SortTask(String str) {
            this.str = str;
        }

        @Override
        protected String compute() {
            if (str.length() <= THRESHOLD) {
                // Directly sort small strings
                String[] lines = str.split("\n");
                Arrays.sort(lines);
                return String.join("\n", lines);
            } else {
                // Split the task
                int mid = str.length() / 2;
                SortTask left = new SortTask(str.substring(0, mid));
                SortTask right = new SortTask(str.substring(mid));

                left.fork(); // asynchronously execute the left task
                String rightResult = right.compute(); // compute right part
                String leftResult = left.join(); // wait and retrieve the result of the left part

                return merge(leftResult, rightResult); // merge the sorted strings
            }
        }

        private String merge(String left, String right) {
            // Simple merging of two sorted strings
            StringBuilder result = new StringBuilder();
            int i = 0, j = 0;
            while (i < left.length() && j < right.length()) {
                if (left.charAt(i) <= right.charAt(j)) {
                    result.append(left.charAt(i++));
                } else {
                    result.append(right.charAt(j++));
                }
            }
            result.append(left.substring(i));
            result.append(right.substring(j));
            return result.toString();
        }
    }

    public String forkJoinSort(String str) {
        ForkJoinPool pool = new ForkJoinPool();
        SortTask task = new SortTask(str);
        return pool.invoke(task);
    }
}
