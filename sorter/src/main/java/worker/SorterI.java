package worker;

import com.zeroc.Ice.Current;

import fileAccessor.FileAccessor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SorterI implements Services.Sorter {

    // ForkJoinMasterPrx master;

    // public SorterI(ForkJoinMasterPrx master) {
    // this.master = master;
    // }

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    FileAccessor fileAccessor;
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

        System.out.println("\nSorter Id: " + sorterId);

        // System.out.println("\nStart: " + start + "\nEnd: " + end);

        String content = fileAccessor.readContent(dataPath, start, end);
        System.out.println("\nRead content from " + start + " to " + end + ": "
                + content.split("\n").length + " lines");
        // System.out.println("\nSorter Content: \n{" + content+"}");

        String sortedContent = sort(content);
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
}
