package sorterMaster;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.IOException;
import com.zeroc.Ice.Current;

import Services.SorterPrx;
import clientManager.ResponseManagerI;
import sorterPool.SorterManagerI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    // private SorterManagerI sorterManager;

    private ContentManager contentManager;
    private SubjectI subjectI;

    private Queue<String> tasks = new LinkedList<>();
    // private List<String> globalResults = new ArrayList<>();

    private ExecutorService masterThreadPool = Executors.newFixedThreadPool(10);

    public DistSorterI(ResponseManagerI responseManager, SorterManagerI sorterManager, SubjectI subjectI,
            ContentManager contentManager) {
        this.responseManager = responseManager;
        // this.sorterManager = sorterManager;

        this.subjectI = subjectI;
        this.contentManager = contentManager;
    }

    @Override
    public String distSort(long id, String dataPath, Current current) {
        System.out.println("\nFile read request received from Client '" + id + "' -> " + "'" + dataPath + "'");

        // contentManager.createOutputFile("/opt/share/gb/", "output.txt");

        int totalLines = contentManager.countFileLines(dataPath);
        int workerCount = subjectI.getWorkerCount();
        // int sorterCount = 3;

        if (workerCount > 1) {
            int[][] ranges = contentManager.calculateRanges(totalLines, workerCount);

            for (int[] range : ranges) {
                tasks.add(range[0] + ";" + range[1]);
            }

            // for (String task : tasks) {
            // System.out.println("\nTask: " + task);
            // }

            System.out.println("\nInitial tasks: " + tasks.size());
            launchWorkers();

            // long counter = 0;
            // for (Services.SorterPrx sorterProxy : subjectI.getSorterProxies().values()) {
            // String task = tasks.remove();
            // int start = Integer.parseInt(task.split(";")[0]);
            // int end = Integer.parseInt(task.split(";")[1]);
            // counter++;
            // sorterProxy.receiveTaskRange(dataPath, start, end, counter);
            // }

            List<Callable<Void>> sortingTasks = new ArrayList<>();

            for (Long sorterId : subjectI.getSorterProxies().keySet()) {
                String task = tasks.remove();
                // Runnable sortingTask = () -> {
                Callable<Void> sortingTask = () -> {
                    int start = Integer.parseInt(task.split(";")[0]);
                    int end = Integer.parseInt(task.split(";")[1]);
                    SorterPrx sorterProxy = subjectI.getSorterProxies().get(sorterId);
                    sorterProxy.receiveTaskRange(dataPath, start, end, sorterId);
                    return null;
                };

                // masterThreadPool.execute(sortingTask);
                sortingTasks.add(sortingTask);
            }

            try {
                List<Future<Void>> futures = masterThreadPool.invokeAll(sortingTasks);

                for (Future<Void> future : futures) {
                    future.get();
                }
                System.out.println("-> All tasks executed successfully.");
            } catch (Exception e) {
                System.out.println("Error executing sorting tasks: " + e.getMessage());
            }

            shutDownWorkers();
            System.out.println("\nRemaining tasks: " + tasks.size());

            String targetPath = "/opt/share/gb/";
            String fileName = "finalOutput.txt";
            // contentManager.createOutputFile(targetPath, fileName);
            // contentManager.combineFiles(fileName, targetPath, "/opt/share/gb/outputs/");

            try {
                contentManager.mergeSortedChunks("/opt/share/gb/outputs/", targetPath.concat(fileName));
            } catch (IOException e) {
                System.out.println("Error merging sorted chunks: " + e.getMessage());
            }

        } else {
            // String content = contentManager.readAllLines(dataPath);
            // String sortedContent = sort(content);
            // contentManager.overWriteFile(dataPath, sortedContent);
        }

        return "SERVER -> Result processed successfully!";
    }

    private void launchWorkers() {
        System.out.println("\nLaunching workers...");
        // notifyObservers();
        subjectI.setRunning(true);
        subjectI._notifyAll();
        System.out.println("-> Workers launched!");
    }

    private void shutDownWorkers() {
        System.out.println("\nShutting down workers...");
        // notifyObservers();
        subjectI.setRunning(false);
        subjectI._notifyAll();
        System.out.println("-> Workers shut down!");
    }

    //

    // private String sort(String s) {
    // System.out.println("\nSorting file content from Server...");

    // String[] lines = s.split("\n");

    // // Sort the array of lines alphabetically
    // Arrays.sort(lines);

    // // Join the sorted lines into a single string with newline separation
    // String result = String.join("\n", lines);

    // // Write the sorted result to the output.txt file
    // // writeToFile(result);

    // // return "Result processed successfully!";
    // return result;
    // }

    // private String[] divide(String[] lines, int parts) {

    // // if (lines == null || lines.length == 0 || parts <= 0) {
    // // return new String[] { "" };
    // // }

    // int partLength = lines.length / parts;
    // int remainder = lines.length % parts;

    // String[] result = new String[parts];
    // int startIdx = 0;

    // for (int i = 0; i < parts; i++) {
    // int endIdx = startIdx + partLength + (i < remainder ? 1 : 0);
    // StringBuilder sb = new StringBuilder();

    // for (int j = startIdx; j < endIdx; j++) {
    // sb.append(lines[j]);
    // if (j < endIdx - 1) {
    // sb.append("\n");
    // }
    // }

    // result[i] = sb.toString();
    // startIdx = endIdx;
    // }

    // return result;
    // }

    // private int partLength(String part) {
    // String[] lines = part.split("\n");
    // return lines.length;
    // }
}