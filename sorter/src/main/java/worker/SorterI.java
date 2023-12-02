package worker;

import com.zeroc.Ice.Current;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    // public String receiveTask(String content, Current current) {
    // // System.out.println("\nFile content received from Server -> \n");
    // // System.out.println(content);
    // return sort(content);
    // }

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

    @Override
    public void receiveTaskRange(String path, int start, int end, Current current) {
        if (!running) {
            System.out.println("\nTask cannot be processed. Application is not running.");
            return;
        }

        System.out.println("\nStart: " + start + "\nEnd: " + end);

        String content = readLines(path, start, end);
        System.out.println("\nContent from " + start + " to " + end + ": " + content.length() + " lines");

        sort(content);
    }

    private String readLines(String path, int start, int end) {
        System.out.println("\nReading file content...\n");

        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            int currentLine = 1;
            String line;

            while ((line = reader.readLine()) != null && currentLine <= end) {
                if (currentLine >= start) {
                    contentBuilder.append(line).append("\n");
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        return contentBuilder.toString();
    }
}
