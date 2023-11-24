package processManager;

import java.util.Scanner;

import Services.ConnectionManagerPrx;
import Services.DistSorterPrx;
import Services.ResponseManagerPrx;
import Services.ResponseReceiverPrx;

public class InputReader {

    private Scanner sc = new java.util.Scanner(System.in);

    public void askForInput(DistSorterPrx sorter, ConnectionManagerPrx connectionManager,
            ResponseManagerPrx responseManager, ResponseReceiverPrx receiver) {

        String hostname = getHostname();
        // long clientId = manager.registerClient(hostname, receiver);
        long clientId = connectionManager.registerClient(hostname, receiver);
        System.out.println("\n-> Client Id: " + clientId + "\n");

        while (true) {
            // System.out.println("Enter file path: ");
            System.out.print("\n-> ");
            String input = sc.nextLine();

            if (input.contains(";")) {
                String[] parts = input.split(";");
                String path = parts[1];

                String sharedPath = "/opt/share/gb/";
                path = sharedPath + path;

                long startTime = System.currentTimeMillis();

                // String result = sorter.distSort(path, subject);
                String result = sorter.distSort(clientId, path);
                System.out.println("\n" + result + "\n");

                long endTime = System.currentTimeMillis();

                System.out.println("\nTotal execution time: " + (endTime - startTime) + "ms\n");
            }

            if (input.equals("exit")) {
                // callbackManager.removeClient(clientId);
                connectionManager.removeClient(clientId);
                System.out.println("\nDisconnecting from server...\n");
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
