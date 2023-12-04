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
            System.out.println("\nWaiting for response...\n");

            if (input.startsWith(";")) {
                // String[] parts = input.split(";");
                // String file = parts[1];

                // String path = "/opt/share/gb/";
                // path = path + file + ".txt";

                String dataPath = "/opt/share/gb/tests/" + input.split(";")[1] + ".txt";

                // String path = "/opt/share/gb/input.txt";

                long startTime = System.currentTimeMillis();

                String result = distSorterProxy.distSort(clientId, dataPath);
                System.out.println("\nSERVER -> " + result);

                long endTime = System.currentTimeMillis();

                long executionTime = endTime - startTime;
                long timeInSeconds = executionTime / 1000;
                System.out.println("\nTotal execution time: " + executionTime + "ms" + " (" + timeInSeconds + "s)\n");
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
