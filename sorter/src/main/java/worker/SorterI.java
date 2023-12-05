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

    // public boolean isRunning() {
    // return running;
    // }

    public void updateRunning(boolean running) {
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
    public void receiveTask(String dataPath, int start, int end, long sorterId, Current current) {
        if (!running) {
            System.out.println("\n-> Task cannot be processed. Application is not running.");
            return;
        }

        String outputFileLocation = "/opt/share/gb/outputs/";
        String outputFileName = "output" + sorterId + ".txt";
        fileAccessor.createOutputFile(outputFileLocation, outputFileName);

        // String content = fileAccessor.readContent(dataPath, start, end);
        // System.out.println("\nRead content from " + start + " to " + end + ": "
        // + content.split("\n").length + " lines");

        String[] content = fileAccessor.readContent1(dataPath, start, end);
        System.out.println("\nRead content from " + start + " to " + end + ": "
                + content.length + " lines");

        // System.out.println("\nSorter Content: \n{" + content+"}");

        long startTime = System.currentTimeMillis();

        // String[] sortedContent = sort1(content);
        // String sortedContent = sort(content);
        String[] sortedContent = forkJoinSort(content);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        long timeInSeconds = executionTime / 1000;
        System.out.println("\nSorting execution time: " + executionTime + "ms" + " (" + timeInSeconds + "s)\n");

        // fileAccessor.writeToFile(outputFileLocation.concat(outputFileName),
        // sortedContent);
        fileAccessor.writeToFile(outputFileLocation.concat(outputFileName), sortedContent);
    }

    // private String sort(String s) {
    // // private String sort(String s, String filePath) {
    // String result = "";

    // if (running) {
    // // System.out.println("\nFile content received from Server");
    // System.out.println("\nSorting file content...\n");

    // String[] lines = s.split("\n");

    // // Sort the array of lines alphabetically
    // Arrays.sort(lines);

    // // Join the sorted lines into a single string with newline separation
    // result = String.join("\n", lines);
    // }

    // return result;
    // }

    private String[] sort1(String[] lines) {
        // Sort the array of lines alphabetically
        Arrays.sort(lines);

        return lines;
    }

    private class SortTask extends RecursiveTask<String[]> {
        private String[] array;
        private static final int THRESHOLD = 2000000; // You can adjust this value

        SortTask(String[] array) {
            this.array = array;
        }

        @Override
        protected String[] compute() {
            if (array.length <= THRESHOLD) {
                Arrays.sort(array);
                return array;
            } else {
                int mid = array.length / 2;
                SortTask left = new SortTask(Arrays.copyOfRange(array, 0, mid));
                SortTask right = new SortTask(Arrays.copyOfRange(array, mid, array.length));

                left.fork();
                String[] rightResult = right.compute();
                String[] leftResult = left.join();

                return merge(leftResult, rightResult);
            }
        }

        private String[] merge(String[] left, String[] right) {
            String[] result = new String[left.length + right.length];
            int i = 0, j = 0, k = 0;

            while (i < left.length && j < right.length) {
                if (left[i].compareTo(right[j]) <= 0) {
                    result[k++] = left[i++];
                } else {
                    result[k++] = right[j++];
                }
            }

            while (i < left.length) {
                result[k++] = left[i++];
            }
            while (j < right.length) {
                result[k++] = right[j++];
            }

            return result;
        }
    }

    public String[] forkJoinSort(String[] lines) {
        ForkJoinPool pool = new ForkJoinPool();
        SortTask task = new SortTask(lines);
        return pool.invoke(task);
    }
}
