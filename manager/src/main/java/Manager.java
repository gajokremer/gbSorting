import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class Manager {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "manager.cfg", extraArgs)) {

            // Services.SorterPrx sorter =
            // Services.SorterPrx.checkedCast(communicator.propertyToProxy("Sorter.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            // if (sorter == null) {
            // throw new Error("Invalid proxy");
            // }

            Services.ReaderPrx reader = Services.ReaderPrx.checkedCast(communicator.propertyToProxy("Reader.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (reader == null) {
                throw new Error("Invalid proxy");
            }

            System.out.println("\nMANAGER STARTED...\n");

            inputFilePath(reader);

            // String path = "data/input.txt";
            // String result = reader.readFile(path);
            // System.out.println("\n" + result);

            // getFilePath(sorter);

        }
    }

    private static void inputFilePath(Services.ReaderPrx reader) {

        while (true) {

            System.out.println("Enter file path: ");
            System.out.print("-> ");
            String path = sc.nextLine();

            if (path.equals("exit")) {
                System.out.println("\nDisconnecting from server...\n");
                break;
            }

            String result = reader.readFile(path);
            System.out.println("\n" + result);
        }
    }

    private static void getFilePath(Services.SorterPrx processor) {
        // Specify the path to your .txt file
        String filePath = "data/input.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringJoiner contentJoiner = new StringJoiner("\n");

            String line;
            while ((line = reader.readLine()) != null) {
                contentJoiner.add(line);
            }

            // Combine all lines into a single string
            String content = contentJoiner.toString();

            // Send the entire content as a single request
            String response = processor.sort(content);

            // System.out.println("Input: " + content);
            // System.out.println("Response: " + response);

            System.out.println("\n" + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
