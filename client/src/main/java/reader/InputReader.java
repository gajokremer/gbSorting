package reader;

import java.util.Scanner;

import Services.ConnectionManagerPrx;
import Services.DistSorterPrx;
import Services.ResponseManagerPrx;
import Services.ResponseReceiverPrx;

public class InputReader {

    private Scanner sc = new java.util.Scanner(System.in);

    public void askForInput(DistSorterPrx distSorterProxy, ConnectionManagerPrx connectionManagerProxy,
            ResponseManagerPrx responseManagerProxy, ResponseReceiverPrx receiverProxy) {

        String hostname = getHostname();
        // long clientId = manager.registerClient(hostname, receiver);
        long clientId = connectionManagerProxy.registerClient(hostname, receiverProxy);
        System.out.println("\n-> Client Id: " + clientId + "\n");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                connectionManagerProxy.removeClient(clientId);
                System.out.println("\n\nDisconnecting Client '" + clientId + "' from server...\n");
            }
        });

        while (true) {
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains(";")) {
                // String[] parts = input.split(";");
                // String file = parts[1];

                // String path = "/opt/share/gb/";
                // path = path + file + ".txt";

                // String path = "/opt/share/gb/" + input.split(";")[1] + ".txt";

                // String path = "/opt/share/gb/input.txt";

                String path = "data/test.txt";

                long startTime = System.currentTimeMillis();

                String result = distSorterProxy.distSort(clientId, path);
                System.out.println("\n" + result + "\n");

                long endTime = System.currentTimeMillis();

                System.out.println("\nTotal execution time: " + (endTime - startTime) + "ms\n");
            }

            if (input.equals("exit")) {
                connectionManagerProxy.removeClient(clientId);
                System.out.println("\nDisconnecting Client '" + clientId + "' from server...\n");
                break;
            }
        }
    }

    private String getHostname() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return "Unable to retrieve hostname";
        }
    }
}
