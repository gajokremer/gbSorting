import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.zeroc.Ice.Current;

// import Services.CallbackManagerPrx;
import Services.SubjectPrx;

public class ReaderI implements Services.Reader {

    // @Override
    // public String readFile(String path, long id, CallbackManagerPrx callbackManager, Current current) {
    //     System.out.println("\nFile read request received from Client " + id);
    //     try {
    //         // Use Java NIO to read the content of the file into a string
    //         String content = new String(Files.readAllBytes(Paths.get(path)));

    //         callbackManager.initiateCallback(id, content);
    //         return "File content read successfully!";

    //         // return content;

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return "Error reading or sorting the file: " + e.getMessage();
    //     }
    // }

    @Override
    public String readFile(String path, SubjectPrx subject, Current current) {
        System.out.println("\nFile read request received from Manager -> " + path);
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            subject._notifyAll(content);

            return "File content read successfully!";
        } catch (IOException e) {
            return "Error reading or sorting the file: " + e.getMessage();
        }
    }
}
