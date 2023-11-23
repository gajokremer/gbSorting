package sorterManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.zeroc.Ice.Current;

import connections.ConnectionManagerI;

public class DistSorterI implements Services.DistSorter {

    private CallbackManagerI callbackManager;
    private ConnectionManagerI connectionManager;

    public DistSorterI(CallbackManagerI callbackManager) {
        this.callbackManager = callbackManager;
        this.connectionManager = callbackManager.getConnectionManager();
    }

    @Override
    // public String distSort(String path, SubjectPrx subject, Current current) {
    public String distSort(String path, Current current) {
        System.out.println("\nFile read request received from Client -> " + path);
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));

            // System.out.println("\n- Total Workers: " + Server.getSorterCount());
            System.out.println("\n- Total Workers: " + connectionManager.getSorterCount());

            // String[] dividedParts = divide(content, Server.getSorterReceivers().size());
            String[] dividedParts = divide(content, connectionManager.getSorterReceivers().size());
            System.out.println();
            for (String p : dividedParts) {
                System.out.println("Part: " + p);
            }

            // send each part to a sorter
            // // if (Server.getSorterReceivers().size() > 0) {
            // if (connectionManager.getSorterReceivers().size() > 0) {
            // int i = 0;
            // String result = "";
            // // for (SorterPrx sorter : Server.getSorters().values()) {
            // for (SorterPrx sorter : connectionManager.getSorters().values()) {
            // result += "\n" + sorter.sort(dividedParts[i]);
            // // subject._notifyAll(result);
            // i++;
            // }

            // return sort(result, current);

            // }

            return sort(content, current);

            // callbackManager.initiateCallback(1, content, current);

            // return "Result processed successfully!";

        } catch (IOException e) {
            return "Error reading or sorting the file: " + e.getMessage();
        }
    }

    @Override
    public String sort(String s, Current current) {
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

    private String[] divide(String content, int parts) {
        // Check for invalid input
        if (content == null || content.isEmpty() || parts <= 0) {
            // Handle invalid input
            return new String[] { content };
        }

        // Calculate the length of each part
        int length = content.length();
        int partLength = length / parts;
        int remainder = length % parts;

        // Initialize an array to store the parts
        String[] dividedParts = new String[parts];

        // Iterate through each part
        int startIndex = 0;
        for (int i = 0; i < parts; i++) {
            // Calculate the endIndex for each part
            int endIndex = startIndex + partLength + (i < remainder ? 1 : 0);

            // Find the nearest newline character to the calculated endIndex
            int newlineIndex = content.lastIndexOf('\n', endIndex - 1);

            // If a newline is found, adjust the endIndex to include the newline character
            if (newlineIndex != -1 && newlineIndex >= startIndex) {
                endIndex = newlineIndex + 1;
            }

            // Extract the substring for the current part
            dividedParts[i] = content.substring(startIndex, endIndex);

            // Update the startIndex for the next part
            startIndex = endIndex;
        }

        return dividedParts;
    }
}
