import java.util.ArrayList;
import java.util.List;

import sorterManager.DistSorterI;
import connections.ResponseManagerI;
import connections.ConnectionManagerI;

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

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg",
                                extraArgs)) {

                        ConnectionManagerI connectionManager = new ConnectionManagerI();
                        ResponseManagerI responseManager = new ResponseManagerI(connectionManager);

                        com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("DistSorter");
                        sorterAdapter.add(new DistSorterI(responseManager),
                                        com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        sorterAdapter.activate();

                        com.zeroc.Ice.ObjectAdapter responseManagerAdapter = communicator
                                        .createObjectAdapter("ResponseManager");
                        responseManagerAdapter.add(responseManager,
                                        com.zeroc.Ice.Util.stringToIdentity("ResponseManager"));
                        responseManagerAdapter.activate();

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

}