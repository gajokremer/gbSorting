package responseReceiver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.zeroc.Ice.Current;

public class ResponseReceiverI implements Services.ResponseReceiver {

    // @Override
    // public void receiveCallback(String s, Current current) {
    // System.out.println("\n" + s);
    // }

    @Override
    public void receiveResponse(String response, Current current) {
        System.out.println("\n" + response);
        // writeToFile(response);
    }

    private void writeToFile(String content) {
        // Specify the path to the output.txt file
        String filePath = "data/output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("\nResult written to '" + filePath + "'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
