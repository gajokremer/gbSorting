package sorterMaster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ContentManager {
    public int countFileLines(String filePath) {
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

    // public String readAllLines(String path) {
    //     System.out.println("\nReading file content...\n");

    //     StringBuilder contentBuilder = new StringBuilder();

    //     try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    //         String line;

    //         while ((line = reader.readLine()) != null) {
    //             contentBuilder.append(line).append("\n");
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace(); // Handle the exception according to your needs
    //     }

    //     System.out.println("-> File content read successfully.");

    //     return contentBuilder.toString();
    // }

    public int[][] calculateRanges(int totalLines, int parts) {
        if (totalLines <= 0 || parts <= 0) {
            throw new IllegalArgumentException("Total lines and parts must be positive integers.");
        }

        int partLength = totalLines / parts;
        int remainder = totalLines % parts;

        int[][] result = new int[parts][2];
        int startIdx = 1; // Start counting from 1

        for (int i = 0; i < parts; i++) {
            int endIdx = startIdx + partLength + (i < remainder ? 1 : 0) - 1; // Adjust the end index
            result[i][0] = startIdx;
            result[i][1] = endIdx;
            startIdx = endIdx + 1; // Adjust the start index
        }

        return result;
    }

    public void mergeSortedChunks(String folderPath, String finalOutputFile) throws IOException {
        // Priority queue to hold the current line from each chunk, along with the chunk
        // ID
        PriorityQueue<String[]> queue = new PriorityQueue<>(Comparator.comparing(arr -> arr[0]));
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        BufferedReader[] readers = new BufferedReader[files.length];

        try {
            // Initialize BufferedReaders for each file and add the first line of each file
            // to the queue
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    readers[i] = new BufferedReader(new FileReader(files[i]));
                    String line = readers[i].readLine();
                    if (line != null) {
                        queue.add(new String[] { line, String.valueOf(i) });
                    }
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(finalOutputFile));

            // Merge process
            while (!queue.isEmpty()) {
                String[] fileLine = queue.poll();
                writer.write(fileLine[0]);
                writer.newLine();

                int fileIndex = Integer.parseInt(fileLine[1]);
                String nextLine = readers[fileIndex].readLine();
                if (nextLine != null) {
                    queue.add(new String[] { nextLine, String.valueOf(fileIndex) });
                }
            }
            writer.close();

            // Delete the input files after successful merging
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }

        } finally {
            // Close all readers
            for (BufferedReader reader : readers) {
                if (reader != null) {
                    reader.close();
                }
            }

            System.out.println("\n-> Sorted chunks merged successfully!");
        }
    }

    // public void createOutputFile(String targetPath, String fileName) {
    // try {
    // // File object representing the desired file
    // File outputFile = new File(targetPath, fileName);

    // // Remove the file if it exists
    // if (outputFile.exists()) {
    // boolean deleted = outputFile.delete();
    // if (!deleted) {
    // System.out.println("Failed to delete existing file.");
    // return;
    // }
    // }

    // // Command to create an empty file in the specified path
    // String command = "touch " + outputFile.getAbsolutePath(); // For Unix-like
    // environments

    // // Create ProcessBuilder
    // ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));

    // // Redirect error stream to output stream
    // processBuilder.redirectErrorStream(true);

    // // Start the process
    // Process process = processBuilder.start();

    // // Wait for the process to finish
    // int exitCode = process.waitFor();

    // // Check if the process exited successfully
    // if (exitCode == 0) {
    // System.out.println("\nOutput file created successfully!");
    // System.out.println("-> '" + outputFile.getAbsolutePath() + "'");
    // } else {
    // System.out.println("\nError creating the output file. Exit code: " +
    // exitCode);
    // }

    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // }

    // void combineFiles(String fileName, String targetPath, String filesPath) {
    // Path targetFilePath = Paths.get(targetPath, fileName);

    // try {
    // try (DirectoryStream<Path> directoryStream =
    // Files.newDirectoryStream(Paths.get(filesPath))) {
    // for (Path filePath : directoryStream) {
    // byte[] fileContent = Files.readAllBytes(filePath);
    // Files.write(targetFilePath, fileContent, StandardOpenOption.APPEND);

    // // Add a newline after writing the file content
    // Files.write(targetFilePath, "\n".getBytes(), StandardOpenOption.APPEND);

    // // Delete the file after its content is appended to the target file
    // Files.delete(filePath);
    // }
    // }

    // System.out.println("Files combined and deleted successfully!");

    // } catch (IOException e) {
    // System.err.println("Error combining files: " + e.getMessage());
    // }
    // }

    // public void writeToFile(String filePath, String content) {
    // // Specify the path to the output.txt file

    // try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    // // writer.write(content);
    // writer.write(content + "\n");
    // System.out.println("\nResult written to '" + filePath + "'.");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // public void overWriteFile(String filePath, String content) {
    // // Specify the path to the output.txt file

    // try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,
    // false))) {
    // // The second parameter (false) specifies that the file should be overwritten
    // writer.write(content);
    // System.out.println("\n-> Content on '" + filePath + "' overwritten
    // successfully.");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
}
