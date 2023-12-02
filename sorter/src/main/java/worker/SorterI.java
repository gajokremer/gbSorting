package worker;

import com.zeroc.Ice.Current;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SorterI implements Services.Sorter {

    // ForkJoinMasterPrx master;

    // public SorterI(ForkJoinMasterPrx master) {
    // this.master = master;
    // }

    private ExecutorService threadPool;

    private boolean running = false;

    public SorterI(int threadPoolSize) {
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        System.out.println("\nSorter running?: " + running);
    }

    // @Override
    // public void update(Current current) {
    // System.out.println("\nSorter updated!");
    // }

    @Override
    public String receiveTask(String content, Current current) {
        // System.out.println("\nFile content received from Server -> \n");
        // System.out.println(content);
        return sort(content);
    }

    private String sort(String s) {

        String result = "";

        if (running) {

            // System.out.println("\nFile content received from Server -> \n");
            // System.out.println(s);

            System.out.println("\nFile content received from Server");
            System.out.println("\nSorting file content...\n");

            String[] lines = s.split("\n");

            // Sort the array of lines alphabetically
            Arrays.sort(lines);

            // Join the sorted lines into a single string with newline separation
            result = String.join("\n", lines);

            // Write the sorted result to the output.txt file
            // writeToFile(result);

            // return "Result processed successfully!";
        }

        return result;
    }
}
