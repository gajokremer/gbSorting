package sorterMaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.zeroc.Ice.Current;

import Services.SorterPrx;
import clientManager.ConnectionManagerI;
import clientManager.ResponseManagerI;

public class DistSorterI implements Services.DistSorter {

    private ResponseManagerI responseManager;
    private ConnectionManagerI connectionManager;

    public DistSorterI(ResponseManagerI callbackManager) {
        this.responseManager = callbackManager;
        this.connectionManager = callbackManager.getConnectionManager();
    }

    @Override
    public String distSort(long id, String path, Current current) {
        System.out.println("\nFile read request received from Client '" + id + "' -> " + "'" + path + "'");
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));

            System.out.println("\n- Total Workers: " + connectionManager.getSorterCount());

            String[] lines = content.split("\n");
            String result = "";

            if (connectionManager.getSorterCount() > 1) {
                String[] parts = divide(lines, connectionManager.getSorterCount());

                // for (String r : parts) {
                //     System.out.println("\n- Length: " + partLength(r));
                //     System.out.println(r);
                // }

                int i = 0;
                for (SorterPrx sorter : connectionManager.getSorters().values()) {
                    result += "\n" + sorter.sort(parts[i]);
                    i++;
                }

                result = sort(result);

            } else {
                result = sort(content);
            }

            // result = sort(result);

            responseManager.respondToClient(id, result, current);

            return "Result processed successfully!";

        } catch (IOException e) {
            return "Error reading or sorting the file: " + e.getMessage();
        }
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

    private static String[] divide(String[] lines, int parts) {

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