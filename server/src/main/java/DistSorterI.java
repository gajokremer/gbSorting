import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.zeroc.Ice.Current;

import Services.SorterPrx;
// import Services.CallbackManagerPrx;

public class DistSorterI implements Services.DistSorter {

    // @Override
    // public String readFile(String path, long id, CallbackManagerPrx
    // callbackManager, Current current) {
    // System.out.println("\nFile read request received from Client " + id);
    // try {
    // // Use Java NIO to read the content of the file into a string
    // String content = new String(Files.readAllBytes(Paths.get(path)));

    // callbackManager.initiateCallback(id, content);
    // return "File content read successfully!";

    // // return content;

    // } catch (IOException e) {
    // e.printStackTrace();
    // return "Error reading or sorting the file: " + e.getMessage();
    // }
    // }

    @Override
    // public String distSort(String path, SubjectPrx subject, Current current) {
    public String distSort(String path, Current current) {
        System.out.println("\nFile read request received from Client -> " + path);
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));

            String[] dividedParts = divide(content, Server.getWorkerSorters().size());
            System.out.println();
            for (String p : dividedParts) {
                System.out.println("Part: " + p);
            }

            // send each part to a sorter
            if (Server.getWorkerSorters().size() > 0) {
                int i = 0;
                String result = "";
                for (SorterPrx sorter : Server.getWorkerSorters().values()) {
                    result += "\n" + sorter.sort(dividedParts[i]);
                    // subject._notifyAll(result);
                    i++;
                }
            }

            if (Server.getWorkerCount() == 0) {
                return sort(content, current);
            }
            // result = sort(result, current);
            // subject._notifyAll(result);

            return "File content read successfully!";

            // return result;

        } catch (IOException e) {
            return "Error reading or sorting the file: " + e.getMessage();
        }
    }

    @Override
    public String sort(String s, Current current) {
        System.out.println("\nSorting file content...");

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
