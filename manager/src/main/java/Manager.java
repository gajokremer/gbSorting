import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Services.ObserverPrx;
import Services.ReaderPrx;
import Services.SubjectPrx;
import observer.ObserverI;

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

            // Services.CallbackManagerPrx callbackManager = Services.CallbackManagerPrx
            // .checkedCast(communicator.propertyToProxy("CallbackManager.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            if (reader == null || subject == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter observerAdapter = communicator.createObjectAdapter("Observer");
            observerAdapter.add(new ObserverI(), com.zeroc.Ice.Util.stringToIdentity("Observer"));
            observerAdapter.activate();
            Services.ObserverPrx observer = Services.ObserverPrx.uncheckedCast(observerAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Observer")));

            System.out.println("\nMANAGER STARTED...");

            // rest of the code goes here

            subject.attach(observer);

            run(reader, subject, observer);

            // String ipAddress = getIpAddress();
            // long clientId = callbackManager.register(ipAddress, receiver);
            // System.out.println("-> Client Id: " + clientId + "\n");

            // inputFilePath(clientId, reader, callbackManager);

            // getFilePath(sorter);
        }
    }

    private static void run(ReaderPrx reader, SubjectPrx subject, ObserverPrx observer) {
        while (true) {
            // System.out.println("Enter file path: ");
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains("::")) {
                String[] parts = input.split("::");
                String path = parts[1];

                String sharedPath = "/opt/share/";
                path = sharedPath + path;

                String result = reader.readFile(path, subject);
                System.out.println("\n" + result + "\n");
            }

            if (input.equals("exit")) {
                subject.detach(observer);
                System.out.println("\nDisconnecting from server...\n");
                break;
            }
        }
    }
}
