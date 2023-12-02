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

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private boolean running = false;

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
            System.out.println("\nFile content received from Server");
            System.out.println("\nSorting file content...\n");

            String[] lines = s.split("\n");

            // Sort the array of lines alphabetically
            Arrays.sort(lines);

            // Join the sorted lines into a single string with newline separation
            result = String.join("\n", lines);
        }

        return result;
    }

    // private String sort(String s) {
    // String[] resultContainer = { "" }; // Container to hold the result

    // // Use CountDownLatch to wait for the Runnable to finish
    // CountDownLatch latch = new CountDownLatch(1);

    // Runnable task = () -> {
    // if (running) {
    // System.out.println("\nFile content received from Server");
    // System.out.println("\nSorting file content...\n");

    // String[] lines = s.split("\n");

    // // Sort the array of lines alphabetically
    // Arrays.sort(lines);

    // // Join the sorted lines into a single string with newline separation
    // resultContainer[0] = String.join("\n", lines);
    // }

    // // Signal that the Runnable has completed
    // latch.countDown();
    // };

    // // Submit the Runnable to the thread pool and get a Future
    // Future<?> futureResult = threadPool.submit(task);

    // try {
    // // Wait for the Runnable to complete
    // latch.await();
    // // Ensure that the Runnable has completed (handle exceptions if any)
    // futureResult.get();
    // } catch (InterruptedException | ExecutionException e) {
    // e.printStackTrace();
    // }

    // return resultContainer[0];
    // }

}
