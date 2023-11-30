import java.util.ArrayList;
import java.util.List;

import clientManager.ConnectionManagerI;
import clientManager.ResponseManagerI;
import sorterMaster.DistSorterI;
import sorterPool.ForkJoinMasterI;
import sorterPool.SorterManagerI;

public class Server {

        public static void main(String[] args) {
                List<String> extraArgs = new ArrayList<>();

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg",
                                extraArgs)) {

                        ConnectionManagerI connectionManager = new ConnectionManagerI();
                        SorterManagerI sorterManager = new SorterManagerI();
                        ResponseManagerI responseManager = new ResponseManagerI(connectionManager);
                        DistSorterI distSorter = new DistSorterI(responseManager, sorterManager);
                        ForkJoinMasterI forkJoinMaster = new ForkJoinMasterI();

                        // com.zeroc.Ice.ObjectAdapter sorterAdapter =
                        // communicator.createObjectAdapter("DistSorter");
                        // sorterAdapter.add(distSorter,
                        // com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        // sorterAdapter.activate();

                        // com.zeroc.Ice.ObjectAdapter responseManagerAdapter = communicator
                        // .createObjectAdapter("ResponseManager");
                        // responseManagerAdapter.add(responseManager,
                        // com.zeroc.Ice.Util.stringToIdentity("ResponseManager"));
                        // responseManagerAdapter.activate();

                        // com.zeroc.Ice.ObjectAdapter connectionManagerAdapter = communicator
                        // .createObjectAdapter("ConnectionManager");
                        // connectionManagerAdapter.add(connectionManager,
                        // com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
                        // connectionManagerAdapter.activate();

                        // com.zeroc.Ice.ObjectAdapter sorterManagerAdapter = communicator
                        // .createObjectAdapter("SorterManager");
                        // sorterManagerAdapter.add(sorterManager,
                        // com.zeroc.Ice.Util.stringToIdentity("SorterManager"));

                        com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Server");
                        adapter.add(distSorter, com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        adapter.add(responseManager, com.zeroc.Ice.Util.stringToIdentity("ResponseManager"));
                        adapter.add(connectionManager, com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
                        adapter.add(sorterManager, com.zeroc.Ice.Util.stringToIdentity("SorterManager"));
                        adapter.add(forkJoinMaster, com.zeroc.Ice.Util.stringToIdentity("ForkJoinMaster"));
                        adapter.activate();

                        System.out.println("\nSERVER STARTED...");

                        communicator.waitForShutdown();
                        // communicator.destroy();
                }
        }

}