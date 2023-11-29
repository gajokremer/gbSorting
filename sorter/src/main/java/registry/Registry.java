package registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import Services.SorterManagerPrx;
import Services.SorterPrx;

public class Registry {

    Scanner sc = new Scanner(System.in);

    public void register(SorterManagerPrx sorterManager, SorterPrx sorter) {
        String hostname = getHostname();
        // long sorterId = connectionManager.registerSorter(hostname, receiver, sorter);
        long sorterId = sorterManager.registerSorter(hostname, sorter);
        System.out.println("-> Sorter Id: " + sorterId + "\n");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                sorterManager.removeSorter(sorterId);
                System.out.println("\n\nDisconnecting Sorter '" + sorterId + "' from server...\n");
            }
        });

        while (true) {
            // do nothing
            String input = sc.nextLine();
            if (input.equals("exit")) {
                sorterManager.removeSorter(sorterId);
                System.out.println("\nDisconnecting Sorter '" + sorterId + "' from server...");
                System.out.println();
                // System.exit(0);
                break;
            }
        }

    }

    private static String getHostname() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return "Unable to retrieve hostname";
        }
    }
}
