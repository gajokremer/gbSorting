package sorterMaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.zeroc.Ice.Current;

import Services.SorterPrx;
import clientManager.ResponseManagerI;
import sorterPool.SorterManagerI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    // private SorterManagerI sorterManager;

    private SubjectI subjectI;

    // private ForkJoinMasterI forkJoinMaster;

    private Queue<String> tasks = new LinkedList<>();
    private List<String> globalResults = new ArrayList<>();

    public DistSorterI(ResponseManagerI responseManager, SorterManagerI sorterManager, SubjectI subjectI) {
        this.responseManager = responseManager;
        // this.sorterManager = sorterManager;

        this.subjectI = subjectI;

        // this.forkJoinMaster = forkJoinMaster;
    }

    @Override
    public String distSort(long id, String path, Current current) {
        System.out.println("\nFile read request received from Client '" + id + "' -> " + "'" + path + "'");
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));

            // forkJoinMaster.invoke(task, current);

            // int sorterCount = sorterManager.getSorterCount();
            // int sorterCount = sorterPool.size();
            int workerCount = subjectI.getWorkerCount();
            System.out.println("\n- Total Workers: " + workerCount);

            String[] lines = content.split("\n");
            String result = "";

            if (workerCount > 1) {
                String[] parts = divide(lines, workerCount);

                // for (String r : parts) {
                // System.out.println("\n- Length: " + partLength(r));
                // System.out.println(r);
                // }

                // Iterative, should be parallel
                // int i = 0;
                // for (SorterPrx sorter : sorterManager.getSorters().values()) {
                // result += sorter.sort(parts[i]) + "\n";
                // i++;
                // }

                for (int i = 0; i < parts.length; i++) {
                    tasks.add(parts[i]);
                }

                launchWorkers();

                // distrubute the task queue to the workers
                for (SorterPrx sorter : subjectI.getSorterProxies().values()) {
                    globalResults.add(sorter.receiveTask(tasks.poll()));
                }

                result = sort(globalResults.toString());

                shutDownWorkers();

            } else {
                result = sort(content);
            }

            // result = sort(result);

            responseManager.respondToClient(id, result, current);

            return "SERVER -> Result processed successfully!";

        } catch (IOException e) {
            return "Error reading or sorting the file: " + e.getMessage();
        }
    }

    private void launchWorkers() {
        System.out.println("\nLaunching workers...");
        // notifyObservers();
        subjectI.setRunning(true);
        subjectI._notifyAll();
        System.out.println("\nWorkers launched!");
    }

    private void shutDownWorkers() {
        System.out.println("\nShutting down workers...");
        // notifyObservers();
        subjectI.setRunning(false);
        subjectI._notifyAll();
        System.out.println("\nWorkers shut down!");
    }

    private String sort(String s) {
        System.out.println("\nSorting file content from Server...\n");

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

    private int partLength(String part) {
        String[] lines = part.split("\n");
        return lines.length;
    }
}