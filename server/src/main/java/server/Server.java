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

            ConnectionManagerI connectionManager = new ConnectionManagerI();
            CallbackManagerI callbackManager = new CallbackManagerI(connectionManager);

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("DistSorter");
            sorterAdapter.add(new DistSorterI(callbackManager),
                    com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
            sorterAdapter.activate();

            com.zeroc.Ice.ObjectAdapter callbackManagerAdapter = communicator.createObjectAdapter("CallbackManager");
            callbackManagerAdapter.add(callbackManager,
                    com.zeroc.Ice.Util.stringToIdentity("CallbackManager"));
            callbackManagerAdapter.activate();

            com.zeroc.Ice.ObjectAdapter connectionManagerAdapter = communicator
                    .createObjectAdapter("ConnectionManager");
            connectionManagerAdapter.add(connectionManager,
                    com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
            connectionManagerAdapter.activate();

            System.out.println("\nSERVER STARTED...");

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

}