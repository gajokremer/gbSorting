package sorterMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.IOException;
import com.zeroc.Ice.Current;

import Services.SorterPrx;
import clientManager.ResponseManagerI;
import sorterPool.SorterManagerI;
import sortingStatus.SubjectI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    // private SorterManagerI sorterManager;

    private ContentManager contentManager;
    private SubjectI subjectI;

    private Queue<String> tasks = new LinkedList<>();
    // private List<String> globalResults = new ArrayList<>();

    // private ExecutorService masterThreadPool = Executors.newFixedThreadPool(10);
    private ExecutorService masterThreadPool;

    private final long LINE_LIMIT = 1200000;

    public DistSorterI(ResponseManagerI responseManager, SorterManagerI sorterManager, SubjectI subjectI,
            ContentManager contentManager) {
        this.responseManager = responseManager;
        // this.sorterManager = sorterManager;

        this.subjectI = subjectI;
        this.contentManager = contentManager;
    }

    @Override
    // public String distSort(long id, String dataPath, Current current) {
    public void distSort(long id, String dataPath, Current current) {
        System.out.println("\nSorting request received from Client '" + id + "' -> " + "'" + dataPath + "'");
        String response = "";

        // contentManager.createOutputFile("/opt/share/gb/", "output.txt");

        int totalLines = contentManager.countFileLines(dataPath);
        int workerCount = subjectI.getWorkerCount();
        final int MAX_TASK_SIZE = 12000000;

        // if (workerCount > 1) {
        if (totalLines > LINE_LIMIT && workerCount > 0) {
            System.out.println("\n-> Sorting file in distributed mode...");
            int[][] ranges = contentManager.calculateRanges(totalLines, MAX_TASK_SIZE);

            for (int[] range : ranges) {
                tasks.add(range[0] + ";" + range[1]);
            }

            // for (String task : tasks) {
            // System.out.println("\nTask: " + task);
            // }

            System.out.println("\nTotal lines: " + totalLines);
            System.out.println("\nInitial tasks: " + tasks.size());
            System.out.println("\nWorker count: " + workerCount);
            // launchWorkers();
            updateWorkers(true);

            distributeTasks(dataPath, workerCount);

            // shutDownWorkers();
            updateWorkers(false);
            System.out.println("\nRemaining tasks: " + tasks.size());

            String fileName = "finalOutput.txt";
            String targetPath = "/opt/share/gb/";
            // contentManager.createOutputFile(targetPath, fileName);
            // contentManager.combineFiles(fileName, targetPath, "/opt/share/gb/outputs/");

            long start = System.currentTimeMillis();

            try {
                contentManager.mergeSortedChunks("/opt/share/gb/outputs/",
                        targetPath.concat(fileName));
            } catch (IOException e) {
                System.out.println("Error merging sorted chunks: " + e.getMessage());
            }

            long end = System.currentTimeMillis();
            long mergingTime = end - start;
            long timeInSeconds = mergingTime / 1000;
            System.out.println("\nMerging execution time: " + mergingTime + "ms" + " (" + timeInSeconds + "s)\n");

            // return "Content sorted successfully!";
            response = "Result processed successfully!";

        } else if (workerCount == 0 || totalLines < LINE_LIMIT) {
            // sort on server
            System.out.println("\n-> Sorting file monolithically...");

            String fileName = "finalOutput.txt";
            String targetPath = "/opt/share/gb/";

            contentManager.createOutputFile(targetPath, fileName);

            long start = System.currentTimeMillis();

            String orderedContent = sort(contentManager.readAllLines(dataPath));

            long end = System.currentTimeMillis();
            long executionTime = end - start;
            long timeInSeconds = executionTime / 1000;
            System.out.println("\nSorting execution time: " + executionTime + "ms" + " (" + timeInSeconds + "s)\n");

            contentManager.writeToFile(targetPath.concat(fileName), orderedContent);

            response = "Content sorted successfully!";
            // return "Content sorted successfully!";
        } else {
            // return "Not enough workers to process the request.";
            response = "Unable to process request.";
        }

        // return "Not enough workers to process the request.";
        System.out.println("\n-> Sorting done!");
        responseManager.respondToClient(id, response, current);
    }

    private void distributeTasks(String dataPath, int workerCount) {
        masterThreadPool = Executors.newFixedThreadPool(workerCount);
        Queue<Runnable> tasksToSend = new LinkedList<>();
        Map<Long, Boolean> sorterStatus = Collections.synchronizedMap(new HashMap<>());

        // Initialize sorterStatus with false
        for (Long sorterId : subjectI.getSorterProxies().keySet()) {
            sorterStatus.put(sorterId, false);
        }

        System.out.println("\nSorters status: " + sorterStatus);

        System.out.println();
        while (!tasks.isEmpty()) {
            for (Long sorterId : sorterStatus.keySet()) {
                if (!sorterStatus.get(sorterId)) {
                    String task = tasks.remove();
                    Runnable sortingTask = () -> {
                        int start = Integer.parseInt(task.split(";")[0]);
                        int end = Integer.parseInt(task.split(";")[1]);
                        SorterPrx sorterProxy = subjectI.getSorterProxies().get(sorterId);
                        sorterProxy.receiveTask(dataPath, start, end, sorterId);
                    };
                    synchronized (sorterStatus) {
                        sorterStatus.put(sorterId, true);
                    }
                    Future<?> result = masterThreadPool.submit(sortingTask);

                    try {
                        result.get();
                        System.out.println(sorterId + " => " + "Task executed successfully!");
                        synchronized (sorterStatus) {
                            sorterStatus.put(sorterId, false);
                        }
                    } catch (Exception e) {
                        System.out.println("Error executing sorting tasks: " + e.getMessage());
                    }
                }
                // System.out.println(sorterId + " => " + tasks.remove());
            }

            System.out.println("---------------------------------");
        }
    }

    private void distributeTasksOriginal(String dataPath, int workerCount) {
        masterThreadPool = Executors.newFixedThreadPool(workerCount);
        List<Callable<Void>> sortingTasks = new ArrayList<>();

        for (Long sorterId : subjectI.getSorterProxies().keySet()) {
            String task = tasks.remove();
            // Runnable sortingTask = () -> {
            Callable<Void> sortingTask = () -> {
                int start = Integer.parseInt(task.split(";")[0]);
                int end = Integer.parseInt(task.split(";")[1]);
                SorterPrx sorterProxy = subjectI.getSorterProxies().get(sorterId);
                sorterProxy.receiveTask(dataPath, start, end, sorterId);
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
    }

    // private void launchWorkers() {
    // System.out.println("\nLaunching workers...");
    // // notifyObservers();
    // subjectI.setRunning(true);
    // subjectI._notifyAll();
    // System.out.println("-> Workers launched!");
    // }

    // private void shutDownWorkers() {
    // System.out.println("\nShutting down workers...");
    // // notifyObservers();
    // subjectI.setRunning(false);
    // subjectI._notifyAll();
    // System.out.println("-> Workers shut down!");
    // }

    private void updateWorkers(boolean newStatus) {
        if (newStatus) {
            System.out.println("\nLaunching workers...");
        }
        if (!newStatus) {
            System.out.println("\nShutting down workers...");
        }

        subjectI.setRunning(newStatus);
        subjectI._notifyAll();

        if (newStatus) {
            System.out.println("-> Workers launched!");
        }
        if (!newStatus) {
            System.out.println("-> Workers shut down!");
        }
    }

    //

    private String sort(String s) {
        // System.out.println("\nSorting file content from Server...");

        String[] lines = s.split("\n");

        // Sort the array of lines alphabetically
        Arrays.sort(lines);

        // Join the sorted lines into a single string with newline separation
        String result = String.join("\n", lines);

        // Write the sorted result to the output.txt file
        // writeToFile(result);

        // return "Result processed successfully!";
        return result;
    }

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