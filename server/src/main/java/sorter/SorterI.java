package sorter;
import com.zeroc.Ice.Current;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SorterI implements Services.Sorter {

    @Override
    public String sort(String s, Current current) {
        String[] lines = s.split("\n");

        // Sort the array of lines alphabetically
        Arrays.sort(lines);

        // Join the sorted lines into a single string with newline separation
        String result = String.join("\n", lines);

        // Write the sorted result to the output.txt file
        writeToFile(result);

        return "Result processed successfully!";
    }

    private void writeToFile(String content) {
        // Specify the path to the output.txt file
        String filePath = "data/output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("Result written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
