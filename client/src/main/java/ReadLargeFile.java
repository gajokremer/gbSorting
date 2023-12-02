import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadLargeFile {
    public static void main(String[] args) {
        String filePath = "data/test.txt";
        String fileContent = readAndGetString(filePath);
        System.out.println(fileContent);
    }

    private static String readAndGetString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0; // Counter to track the number of lines

            while ((line = reader.readLine()) != null) {
                // Process each line as needed
                contentBuilder.append(line).append(System.lineSeparator());

                // Increment the line counter
                lineCount++;
            }

            // Print the total number of lines
            contentBuilder.append("Total lines in the file: ").append(lineCount);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }
}
