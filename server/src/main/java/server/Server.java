package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Services.CallbackReceiverPrx;
import Services.SorterPrx;
// import serversorterManager.CallbackManagerI;
// import server.sorterManager.DistSorterI;
// import server.sorterManager.SubjectI;
import sorterManager.CallbackManagerI;
import sorterManager.DistSorterI;
import connections.ConnectionManagerI;

import java.util.HashMap;

public class Server {
    private static Map<Long, CallbackReceiverPrx> sorterReceivers = new HashMap<>();
    private static Map<Long, SorterPrx> sorters = new HashMap<>();
    private static Map<Long, CallbackReceiverPrx> clientReceivers = new HashMap<>();
    private static long clientCount = 0;
    private static long workerCount = 0;

    public static void main(String[] args) {
        // try(com.zeroc.Ice.Communicator communicator =
        // com.zeroc.Ice.Util.initialize(args))
        // {
        // com.zeroc.Ice.ObjectAdapter adapter =
        // communicator.createObjectAdapterWithEndpoints("SimplePrinterAdapter",
        // "default -p 10000");
        // com.zeroc.Ice.Object object = new PrinterI();
        // adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
        // adapter.activate();
        // communicator.waitForShutdown();
        // }

        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg", extraArgs)) {

            System.out.println("\nSERVER STARTED...");

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("DistSorter");
            sorterAdapter.add(new DistSorterI(), com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
            sorterAdapter.activate();

            com.zeroc.Ice.ObjectAdapter callbackManagerAdapter = communicator.createObjectAdapter("CallbackManager");
            callbackManagerAdapter.add(new CallbackManagerI(),
                    com.zeroc.Ice.Util.stringToIdentity("CallbackManager"));
            callbackManagerAdapter.activate();

            com.zeroc.Ice.ObjectAdapter connectionManagerAdapter = communicator
                    .createObjectAdapter("ConnectionManager");
            connectionManagerAdapter.add(new ConnectionManagerI(),
                    com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
            connectionManagerAdapter.activate();

            communicator.waitForShutdown();
            // communicator.destroy();
        }
    }

    // public static synchronized long generateUniqueId(String ipAddress) {
    // clientCount++;
    // long id = clientCount;
    // workers.put(id, ipAddress);
    // return id;
    // }

    public static synchronized long registerWorker(CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy) {
        workerCount++;
        long id = workerCount;
        sorterReceivers.put(id, receiverProxy);
        sorters.put(id, sorterProxy);
        return id;
    }

    public static synchronized long registerClient(CallbackReceiverPrx receiver) {
        clientCount++;
        long id = clientCount;
        clientReceivers.put(id, receiver);
        return id;
    }

    public static Map<Long, CallbackReceiverPrx> getSorterReceivers() {
        return sorterReceivers;
    }

    public static Map<Long, SorterPrx> getSorters() {
        return sorters;
    }

    public static Map<Long, CallbackReceiverPrx> getClientReceivers() {
        return clientReceivers;
    }

    public static long getClientCount() {
        return clientCount;
    }

    public static void setClientCount(long clientCount) {
        Server.clientCount = clientCount;
    }

    public static long getSorterCount() {
        return workerCount;
    }

    public static void setSorterCount(long sorterCount) {
        Server.workerCount = sorterCount;
    }
}