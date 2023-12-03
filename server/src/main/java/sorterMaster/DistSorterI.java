package sorterMaster;

import java.util.LinkedList;
import java.util.Queue;
import java.io.RandomAccessFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.zeroc.Ice.Current;

import clientManager.ResponseManagerI;
import sorterPool.SorterManagerI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    // private SorterManagerI sorterManager;

    private SubjectI subjectI;
    private ContentManager contentManager;

    private Queue<String> tasks = new LinkedList<>();
    // private List<String> globalResults = new ArrayList<>();

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

            long counter = 0;
            for (Services.SorterPrx sorterProxy : subjectI.getSorterProxies().values()) {
                String task = tasks.remove();
                int start = Integer.parseInt(task.split(";")[0]);
                int end = Integer.parseInt(task.split(";")[1]);
                counter++;
                sorterProxy.receiveTaskRange(dataPath, start, end, counter);
            }

            shutDownWorkers();
            System.out.println("\nRemaining tasks: " + tasks.size());

            String targetPath = "/opt/share/gb/";
            String fileName = "output.txt";
            contentManager.createOutputFile(targetPath, fileName);
            contentManager.combineFiles(fileName, targetPath, "/opt/share/gb/outputs/");

            String combinedContent = contentManager.readAllLines(targetPath.concat(fileName));
            contentManager.writeToFile(targetPath.concat(fileName), combinedContent);

            // try {
            // mergeSortedChunks(targetPath.concat(fileName), ranges,
            // targetPath.concat("finalOutput.txt"));
            // } catch (IOException e) {
            // System.out.println("\nError merging sorted chunks: " + e.getMessage());
            // }
            // String sortedContent = sort(combinedContent);
            // contentManager.overWriteFile(targetPath.concat(fileName), sortedContent);

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

    public void mergeSortedChunks(String inputFile, int[][] ranges, String finalOutputFile) throws IOException {
        PriorityQueue<String[]> queue = new PriorityQueue<>(Comparator.comparing(arr -> arr[0]));
        RandomAccessFile file = new RandomAccessFile(inputFile, "r");
        long[] startOffsets = new long[ranges.length];

        for (int i = 0; i < ranges.length; i++) {
            startOffsets[i] = findByteOffsetForLine(file, ranges[i][0]);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(finalOutputFile));

        try {
            for (int i = 0; i < ranges.length; i++) {
                file.seek(startOffsets[i]);
                if (file.getFilePointer() < file.length()) {
                    String line = file.readLine();
                    if (line != null) {
                        queue.add(new String[] { line, String.valueOf(i) });
                    }
                }
            }

            while (!queue.isEmpty()) {
                String[] chunkLine = queue.poll();
                writer.write(chunkLine[0]);
                writer.newLine();

                int chunkId = Integer.parseInt(chunkLine[1]);
                if (withinRange(file, ranges[chunkId])) {
                    String nextLine = file.readLine();
                    if (nextLine != null) {
                        queue.add(new String[] { nextLine, String.valueOf(chunkId) });
                    }
                }
            }
        } finally {
            writer.close();
            file.close();
        }
    }

    private long findByteOffsetForLine(RandomAccessFile file, int lineNumber) throws IOException {
        file.seek(0);
        for (int i = 1; i < lineNumber; i++) {
            if (file.getFilePointer() < file.length()) {
                file.readLine();
            } else {
                break;
            }
        }
        return file.getFilePointer();
    }

    private boolean withinRange(RandomAccessFile file, int[] range) throws IOException {
        long currentOffset = file.getFilePointer();
        long endOffset = findByteOffsetForLine(file, range[1] + 1);
        return currentOffset < endOffset;
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