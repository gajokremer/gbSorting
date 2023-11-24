import java.util.ArrayList;
import java.util.List;
import processManager.InputReader;
import responseReceiver.ResponseReceiverI;

public class Client {

        public static void main(String[] args) {
                List<String> extraArgs = new ArrayList<>();

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg",
                                extraArgs)) {

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

                        Services.ConnectionManagerPrx connectionManager = Services.ConnectionManagerPrx
                                        .checkedCast(communicator.propertyToProxy("ConnectionManager.Proxy"))
                                        .ice_twoway()
                                        .ice_secure(false);

                        Services.ResponseManagerPrx responseManager = Services.ResponseManagerPrx
                                        .checkedCast(communicator.propertyToProxy("ResponseManager.Proxy"))
                                        .ice_twoway()
                                        .ice_secure(false);

                        if (distSorter == null || connectionManager == null || responseManager == null) {
                                throw new Error("Invalid proxy");
                        }

                        com.zeroc.Ice.ObjectAdapter responseReceiverAdapter = communicator
                                        .createObjectAdapter("ResponseReceiver");
                        responseReceiverAdapter.add(new ResponseReceiverI(),
                                        com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver"));
                        responseReceiverAdapter.activate();
                        Services.ResponseReceiverPrx receiver = Services.ResponseReceiverPrx
                                        .uncheckedCast(responseReceiverAdapter
                                                        .createProxy(com.zeroc.Ice.Util
                                                                        .stringToIdentity("ResponseReceiver")));

                        System.out.println("\nCLIENT STARTED...");

                        // rest of the code goes here

                        // askForInput(distSorter, connectionManager, responseManager, receiver);

                        InputReader inputReader = new InputReader();
                        inputReader.askForInput(distSorter, connectionManager, responseManager, receiver);
                }
        }

        // private static void askForInput(DistSorterPrx sorter, ConnectionManagerPrx
        // connectionManager,
        // ResponseManagerPrx responseManager, ResponseReceiverPrx receiver) {

        // String hostname = getHostname();
        // // long clientId = manager.registerClient(hostname, receiver);
        // long clientId = connectionManager.registerClient(hostname, receiver);
        // System.out.println("\n-> Client Id: " + clientId + "\n");

        // while (true) {
        // // System.out.println("Enter file path: ");
        // System.out.print("\n-> ");
        // String input = sc.nextLine();

        // if (input.contains("::")) {
        // String[] parts = input.split("::");
        // String path = parts[1];

        // String sharedPath = "/opt/share/";
        // path = sharedPath + path;

        // long startTime = System.currentTimeMillis();

        // // String result = sorter.distSort(path, subject);
        // String result = sorter.distSort(path);
        // System.out.println("\n" + result + "\n");

        // long endTime = System.currentTimeMillis();

        // System.out.println("\nTotal execution time: " + (endTime - startTime) +
        // "ms\n");
        // }

        // if (input.equals("exit")) {
        // // callbackManager.removeClient(clientId);
        // connectionManager.removeClient(clientId);
        // System.out.println("\nDisconnecting from server...\n");
        // break;
        // }
        // }
        // }

        // private static String getHostname() {
        // try {
        // java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
        // return localHost.getHostName();
        // } catch (java.net.UnknownHostException e) {
        // e.printStackTrace();
        // // Handle the exception as needed
        // return "Unable to retrieve hostname";
        // }
        // }
}
