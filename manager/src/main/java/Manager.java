import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import Services.CallbackManagerPrx;
import Services.CallbackReceiverPrx;
import Services.ReaderPrx;
import Services.SubjectPrx;
import receiver.CallbackReceiverI;

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

            Services.ReaderPrx reader = Services.ReaderPrx
                    .checkedCast(communicator.propertyToProxy("Reader.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.SubjectPrx subject = Services.SubjectPrx
                    .checkedCast(communicator.propertyToProxy("Subject.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.CallbackManagerPrx callbackManager = Services.CallbackManagerPrx
                    .checkedCast(communicator.propertyToProxy("CallbackManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (reader == null || subject == null || callbackManager == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter callbackReceiverAdapter = communicator.createObjectAdapter("CallbackReceiver");
            callbackReceiverAdapter.add(new CallbackReceiverI(),
                    com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver"));
            callbackReceiverAdapter.activate();
            CallbackReceiverPrx receiver = CallbackReceiverPrx.uncheckedCast(callbackReceiverAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver")));

            com.zeroc.Ice.ObjectAdapter observerAdapter = communicator.createObjectAdapter("Observer");
            observerAdapter.add(new ObserverI(), com.zeroc.Ice.Util.stringToIdentity("Observer"));
            observerAdapter.activate();
            Services.ObserverPrx observer = Services.ObserverPrx.uncheckedCast(observerAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Observer")));

            System.out.println("\nMANAGER STARTED...");

            // rest of the code goes here

            subject.attach(observer);

            run(reader, subject);

            // String ipAddress = getIpAddress();
            // long clientId = callbackManager.register(ipAddress, receiver);
            // System.out.println("-> Client Id: " + clientId + "\n");

            // inputFilePath(clientId, reader, callbackManager);

            // getFilePath(sorter);
        }
    }

    private static void run(ReaderPrx reader, SubjectPrx subject) {
        while (true) {
            // System.out.println("Enter file path: ");
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains("::")) {
                String[] parts = input.split("::");
                String path = parts[1];

                String sharedPath = "/opt/share/";
                path = sharedPath + path;

                String result = reader.readFile1(path, subject);
                System.out.println("\n" + result + "\n");
            }

            if (input.equals("exit")) {
                System.out.println("\nDisconnecting from server...\n");
                break;
            }
        }
    }

    private static void inputFilePath(long clientId, Services.ReaderPrx reader, CallbackManagerPrx callbackManager) {

        while (true) {

            System.out.println("Enter file path: ");
            System.out.print("-> ");
            String path = sc.nextLine();

            String sharedPath = "/opt/share/";
            path = sharedPath + path;

            if (path.equals("exit")) {
                System.out.println("\nDisconnecting from server...\n");
                break;
            }

            String result = reader.readFile(path, clientId, callbackManager);
            System.out.println("\n" + result + "\n");
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

    public static String getIpAddress() {
        String ipAddress = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipAddress;
    }
}
