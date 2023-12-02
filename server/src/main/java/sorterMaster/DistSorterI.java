package sorterMaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.zeroc.Ice.Current;

import clientManager.ResponseManagerI;
import sorterPool.SorterManagerI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    // private SorterManagerI sorterManager;

    private SubjectI subjectI;

    private Queue<String> tasks = new LinkedList<>();
    private List<String> globalResults = new ArrayList<>();

    public DistSorterI(ResponseManagerI responseManager, SorterManagerI sorterManager, SubjectI subjectI) {
        this.responseManager = responseManager;
        // this.sorterManager = sorterManager;

        this.subjectI = subjectI;

    }

    private static String readAndGetString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0; // Counter to track the number of lines

            while ((line = reader.readLine()) != null) {
                // Process each line as needed
                contentBuilder.append(line).append(System.lineSeparator());

                // Print each line locally
                System.out.println(line);

                // Increment the line counter
                lineCount++;
            }

            // Print the total number of lines locally
            System.out.println("Total lines in the file: " + lineCount);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    private static int countFileLines(String filePath) {
        int lineCount = 0; // Counter to track the number of lines

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                // Increment the line counter
                lineCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineCount;
    }

    private static int[][] calculateRanges(int totalLines, int parts) {
        if (totalLines <= 0 || parts <= 0) {
            throw new IllegalArgumentException("Total lines and parts must be positive integers.");
        }

        int partLength = totalLines / parts;
        int remainder = totalLines % parts;

        int[][] result = new int[parts][2];
        int startIdx = 0;

        for (int i = 0; i < parts; i++) {
            int endIdx = startIdx + partLength + (i < remainder ? 1 : 0);
            result[i][0] = startIdx;
            result[i][1] = endIdx - 1;
            startIdx = endIdx;
        }

        return result;
    }

    @Override
    public String distSort(long id, String path, Current current) {
        System.out.println("\nFile read request received from Client '" + id + "' -> " + "'" + path + "'");

        int totalLines = countFileLines(path);
        int workerCount = subjectI.getWorkerCount();
        // int sorterCount = 3;

        if (workerCount > 1) {
            int[][] ranges = calculateRanges(totalLines, workerCount);

            for (int[] range : ranges) {
                tasks.add(range[0] + ";" + range[1]);
            }

            // for (String task : tasks) {
            // System.out.println("\nTask: " + task);
            // }

            System.out.println("\nInitial tasks: " + tasks.size());

            launchWorkers();

            for (Services.SorterPrx sorterProxy : subjectI.getSorterProxies().values()) {
                String task = tasks.remove();
                int start = Integer.parseInt(task.split(";")[0]);
                int end = Integer.parseInt(task.split(";")[1]);
                sorterProxy.receiveTaskRange(path, start, end);
            }

            shutDownWorkers();

            System.out.println("\nRemaining tasks: " + tasks.size());
        }

        return "SERVER -> Result processed successfully!";
    }

    // @Override
    // public String distSort(long id, String path, Current current) {

    // try {
    // String content = new String(Files.readAllBytes(Paths.get(path)));

    // // forkJoinMaster.invoke(task, current);

    // // int sorterCount = sorterManager.getSorterCount();
    // // int sorterCount = sorterPool.size();
    // int sorterCount = subjectI.getWorkerCount();
    // System.out.println("\n- Total Workers: " + sorterCount);

    // String[] lines = content.split("\n");
    // String result = "";

    // if (sorterCount > 1) {
    // String[] parts = divide(lines, sorterCount);

    // // for (String r : parts) {
    // // System.out.println("\n- Length: " + partLength(r));
    // // System.out.println(r);
    // // }

    // // Iterative, should be parallel
    // // int i = 0;
    // // for (SorterPrx sorter : sorterManager.getSorters().values()) {
    // // result += sorter.sort(parts[i]) + "\n";
    // // i++;
    // // }

    // for (int i = 0; i < parts.length; i++) {
    // tasks.add(parts[i]);
    // }

    // launchWorkers();

    // // distrubute the task queue to the observers
    // for (SorterPrx sorterProxy : subjectI.getSorterProxies().values()) {
    // sorterProxy.receiveTask(tasks.remove());
    // }

    // result = sort(result);

    // shutDownWorkers();

    // } else {
    // result = sort(content);
    // }

    // // result = sort(result);

    // responseManager.respondToClient(id, result, current);

    // return "SERVER -> Result processed successfully!";

    // } catch (IOException e) {
    // return "Error reading or sorting the file: " + e.getMessage();
    // }
    // }

    private void readAndPrintFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0; // Counter to track the number of lines

            while ((line = reader.readLine()) != null) {
                // Process each line as needed
                System.out.println(line);

                // Increment the line counter
                lineCount++;
            }

            // Print the total number of lines
            System.out.println("Total lines in the file: " + lineCount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchWorkers() {
        System.out.println("\n-> Launching workers...");
        // notifyObservers();
        subjectI.setRunning(true);
        subjectI._notifyAll();
        System.out.println("\nWorkers launched!");
    }

    private void shutDownWorkers() {
        System.out.println("\n-> Shutting down workers...");
        // notifyObservers();
        subjectI.setRunning(false);
        subjectI._notifyAll();
        System.out.println("\nWorkers shut down!");
    }

    private String sort(String s) {
        System.out.println("\nSorting file content from Server...");

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

    private String[] divide(String[] lines, int parts) {

        // if (lines == null || lines.length == 0 || parts <= 0) {
        // return new String[] { "" };
        // }

        int partLength = lines.length / parts;
        int remainder = lines.length % parts;

        String[] result = new String[parts];
        int startIdx = 0;

        for (int i = 0; i < parts; i++) {
            int endIdx = startIdx + partLength + (i < remainder ? 1 : 0);
            StringBuilder sb = new StringBuilder();

            for (int j = startIdx; j < endIdx; j++) {
                sb.append(lines[j]);
                if (j < endIdx - 1) {
                    sb.append("\n");
                }
            }

            result[i] = sb.toString();
            startIdx = endIdx;
        }

        return result;
    }

    // private int partLength(String part) {
    // String[] lines = part.split("\n");
    // return lines.length;
    // }
}