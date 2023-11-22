import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Services.ObserverPrx;
import Services.CallbackManagerPrx;
import Services.CallbackReceiverPrx;
import Services.DistSorter;
import Services.DistSorterPrx;
import Services.SubjectPrx;
import observer.ObserverI;

public class Client {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg", extraArgs)) {

            // Services.SorterPrx sorter =
            // Services.SorterPrx.checkedCast(communicator.propertyToProxy("Sorter.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            // if (sorter == null) {
            // throw new Error("Invalid proxy");
            // }

            Services.DistSorterPrx distSorter = Services.DistSorterPrx
                    .checkedCast(communicator.propertyToProxy("DistSorter.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            // Services.SubjectPrx subject = Services.SubjectPrx
            // .checkedCast(communicator.propertyToProxy("Subject.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            Services.CallbackManagerPrx callbackManager = Services.CallbackManagerPrx
                    .checkedCast(communicator.propertyToProxy("CallbackManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (distSorter == null || callbackManager == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter receiverAdapter = communicator.createObjectAdapter("CallbackReceiver");
            receiverAdapter.add(new ObserverI(), com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver"));
            receiverAdapter.activate();
            Services.CallbackReceiverPrx receiver = Services.CallbackReceiverPrx.uncheckedCast(receiverAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver")));

            System.out.println("\nMANAGER STARTED...");

            // rest of the code goes here

            askForInput(distSorter, callbackManager, receiver);

            // subject.attach(observer);

            // run(distSorter, subject, observer);
        }
    }

    private static void askForInput(DistSorterPrx sorter, CallbackManagerPrx manager, CallbackReceiverPrx receiver) {

        String hostname = getHostname();
        long clientId = manager.registerClient(hostname, receiver);
        System.out.println("\n-> Client Id: " + clientId + "\n");

        while (true) {
            // System.out.println("Enter file path: ");
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains("::")) {
                String[] parts = input.split("::");
                String path = parts[1];

                String sharedPath = "/opt/share/";
                path = sharedPath + path;

                long startTime = System.currentTimeMillis();

                // String result = sorter.distSort(path, subject);
                String result = sorter.distSort(path);
                System.out.println("\n" + result + "\n");

                long endTime = System.currentTimeMillis();

                System.out.println("\nTotal execution time: " + (endTime - startTime) + "ms\n");
            }

            if (input.equals("exit")) {
                manager.removeClient(clientId);
                System.out.println("\nDisconnecting from server...\n");
                break;
            }
        }
    }

    private static String getHostname() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return "Unable to retrieve hostname";
        }
    }

    private static void run(DistSorterPrx sorter, SubjectPrx subject, ObserverPrx observer) {
        while (true) {
            // System.out.println("Enter file path: ");
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains("::")) {
                String[] parts = input.split("::");
                String path = parts[1];

                String sharedPath = "/opt/share/";
                path = sharedPath + path;

                long startTime = System.currentTimeMillis();

                // String result = sorter.distSort(path, subject);
                String result = sorter.distSort(path);
                System.out.println("\n" + result + "\n");

                long endTime = System.currentTimeMillis();

                System.out.println("\nTotal execution time: " + (endTime - startTime) + "ms\n");
            }

            if (input.equals("exit")) {
                subject.detach(observer);
                System.out.println("\nDisconnecting from server...\n");
                break;
            }
        }
    }
}
