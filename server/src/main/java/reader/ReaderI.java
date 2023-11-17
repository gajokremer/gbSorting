package reader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.zeroc.Ice.Current;

public class ReaderI implements Services.Reader {

    @Override
    public String readFile(String path, Current current) {
        try {
            // Use Java NIO to read the content of the file into a string
            String content = new String(Files.readAllBytes(Paths.get(path)));

            return content;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading or sorting the file: " + e.getMessage();
        }
    }

    // @Override
    // public String readFile(String path, Current current) {
    // try {
    // // Use Java NIO to read the content of the file into a string
    // String content = new String(Files.readAllBytes(Paths.get(path)));

    // // Split the content into an array of strings (assuming each line is a
    // string)
    // String[] lines = content.split("\n");

    // // Sort the array of strings
    // Arrays.sort(lines);

    // // Join the sorted strings back into a single string
    // String sortedContent = String.join("\n", lines);

    // // return sortedContent;
    // return "File content sorted successfully!";

    // } catch (IOException e) {
    // e.printStackTrace();
    // return "Error reading or sorting the file: " + e.getMessage();
    // }
    // }
}
