
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Services.CallbackReceiverPrx;
import Services.SorterPrx;
// import serversorterManager.CallbackManagerI;
// import server.sorterManager.DistSorterI;
// import server.sorterManager.SubjectI;

import java.util.HashMap;

public class Server {
    private static Map<Long, CallbackReceiverPrx> workerReceivers = new HashMap<>();
    private static Map<Long, SorterPrx> workerSorters = new HashMap<>();
    private static Map<Long, CallbackReceiverPrx> clients = new HashMap<>();
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

            // com.zeroc.Ice.ObjectAdapter sorterAdapter =
            // communicator.createObjectAdapter("Sorter");
            // sorterAdapter.add(new SorterI(),
            // com.zeroc.Ice.Util.stringToIdentity("SimpleSorter"));
            // sorterAdapter.activate();

            com.zeroc.Ice.ObjectAdapter readerAdapter = communicator.createObjectAdapter("DistSorter");
            readerAdapter.add(new DistSorterI(), com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
            readerAdapter.activate();

            com.zeroc.Ice.ObjectAdapter callbackManagerAdapter = communicator.createObjectAdapter("CallbackManager");
            callbackManagerAdapter.add(new CallbackManagerI(), com.zeroc.Ice.Util.stringToIdentity("CallbackManager"));
            callbackManagerAdapter.activate();

            // com.zeroc.Ice.ObjectAdapter subjectAdapter =
            // communicator.createObjectAdapter("Subject");
            // subjectAdapter.add(new SubjectI(),
            // com.zeroc.Ice.Util.stringToIdentity("Subject"));
            // subjectAdapter.activate();

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
        workerReceivers.put(id, receiverProxy);
        workerSorters.put(id, sorterProxy);
        return id;
    }

    public static synchronized long registerClient(CallbackReceiverPrx receiver) {
        clientCount++;
        long id = clientCount;
        clients.put(id, receiver);
        return id;
    }

    public static Map<Long, CallbackReceiverPrx> getWorkerReceivers() {
        return workerReceivers;
    }

    public static Map<Long, SorterPrx> getWorkerSorters() {
        return workerSorters;
    }

    public static Map<Long, CallbackReceiverPrx> getClients() {
        return clients;
    }

    public static long getClientCount() {
        return clientCount;
    }

    public static void setClientCount(long clientCount) {
        Server.clientCount = clientCount;
    }

    public static long getWorkerCount() {
        return workerCount;
    }

    public static void setWorkerCount(long workerCount) {
        Server.workerCount = workerCount;
    }
}