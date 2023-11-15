import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Reader {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "reader.cfg", extraArgs)) {

            Demo.SorterPrx processor = Demo.SorterPrx.checkedCast(communicator.propertyToProxy("Sorter.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (processor == null) {
                throw new Error("Invalid proxy");
            }

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
}
