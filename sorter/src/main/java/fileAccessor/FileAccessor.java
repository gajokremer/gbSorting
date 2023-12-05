package fileAccessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileAccessor {

    public String readContent(String filePath, int start, int end) {
        System.out.println("\nReading file content...\n");

        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int currentLine = 1;
            String line;

            while ((line = reader.readLine()) != null && currentLine <= end) {
                if (currentLine >= start) {
                    contentBuilder.append(line).append("\n");
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        System.out.println("-> File content read successfully.");

        return contentBuilder.toString();
    }

    public String[] readContent1(String filePath, int start, int end) {
        System.out.println("\nReading file content...\n");

        // List<String> contentList = new ArrayList<>();
        String[] contentList = new String[end - start + 1];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int currentLine = 1;
            String line;

            while ((line = reader.readLine()) != null && currentLine <= end) {
                if (currentLine >= start) {
                    // contentList.add(line);
                    contentList[currentLine - start] = line;
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        System.out.println("-> File content read successfully.");

        // Convert the list to an array
        // return contentList.toArray(new String[0]);
        return contentList;
    }

    public void createOutputFile(String outputPath, String fileName) {
        try {
            // File object representing the desired file
            File outputFile = new File(outputPath, fileName);

            // Remove the file if it exists
            if (outputFile.exists()) {
                boolean deleted = outputFile.delete();
                if (!deleted) {
                    System.out.println("Failed to delete existing file.");
                    return;
                }
            }

            // Command to create an empty file in the specified path
            String command = "touch " + outputFile.getAbsolutePath(); // For Unix-like environments

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));

            // Redirect error stream to output stream
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();

            // Check if the process exited successfully
            if (exitCode == 0) {
                System.out.println("\nOutput file created successfully!");
                System.out.println("-> '" + outputFile.getAbsolutePath() + "'");
            } else {
                System.out.println("\nError creating the output file. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // public void writeToFile(String filePath, String content) {
    //     // Specify the path to the output.txt file

    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    //         writer.write(content);
    //         // writer.write(content + "\n");
    //         System.out.println("\nResult written to '" + filePath + "'.");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void writeToFile(String filePath, String[] content) {
        // Specify the path to the output.txt file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine(); // Add a newline after each element in the array
            }

            System.out.println("\nResult written to '" + filePath + "'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}