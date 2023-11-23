import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Services.ResponseReceiverPrx;
import Services.SorterPrx;
import receiver.ResponseReceiverS;

public class Sorter {
    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.ResponseManagerPrx reponseManager = Services.ResponseManagerPrx
                    .checkedCast(communicator.propertyToProxy("ResponseManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.ConnectionManagerPrx connectionManager = Services.ConnectionManagerPrx
                    .checkedCast(communicator.propertyToProxy("ConnectionManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (reponseManager == null || connectionManager == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter responseReceiverAdapter = communicator.createObjectAdapter("ResponseReceiver");
            responseReceiverAdapter.add(new ResponseReceiverS(),
                    com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver"));
            responseReceiverAdapter.activate();
            ResponseReceiverPrx receiver = ResponseReceiverPrx.uncheckedCast(responseReceiverAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver")));

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter");
            sorterAdapter.add(new SorterI(), com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            sorterAdapter.activate();
            SorterPrx sorter = SorterPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            System.out.println("\nSORTER STARTED...\n");

            String hostname = getHostname();
            long sorterId = connectionManager.registerSorter(hostname, receiver, sorter);
            System.out.println("-> Sorter Id: " + sorterId + "\n");

            while (true) {
                // do nothing
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals("exit")) {
                    connectionManager.removeSorter(sorterId);
                    System.out.println("\nDisconnecting " + sorterId + " from server...");
                    System.exit(0);
                }
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