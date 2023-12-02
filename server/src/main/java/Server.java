import java.util.ArrayList;
import java.util.List;

import clientManager.ConnectionManagerI;
import clientManager.ResponseManagerI;
import sorterMaster.DistSorterI;
import sorterMaster.SubjectI;
import sorterPool.SorterManagerI;

public class Server {

        public static void main(String[] args) {
                List<String> extraArgs = new ArrayList<>();

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg",
                                extraArgs)) {

                        ConnectionManagerI connectionManager = new ConnectionManagerI();
                        SorterManagerI sorterManager = new SorterManagerI();
                        ResponseManagerI responseManager = new ResponseManagerI(connectionManager);
                        // ForkJoinMasterI forkJoinMaster = new ForkJoinMasterI(sorterManager);
                        SubjectI subjectI = new SubjectI();
                        DistSorterI distSorter = new DistSorterI(responseManager, sorterManager, subjectI);

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

                        com.zeroc.Ice.ObjectAdapter serverAdapter = communicator.createObjectAdapter("Server");
                        serverAdapter.add(distSorter, com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        serverAdapter.add(responseManager, com.zeroc.Ice.Util.stringToIdentity("ResponseManager"));
                        serverAdapter.add(connectionManager, com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
                        serverAdapter.add(sorterManager, com.zeroc.Ice.Util.stringToIdentity("SorterManager"));
                        // adapter.add(forkJoinMaster,
                        // com.zeroc.Ice.Util.stringToIdentity("ForkJoinMaster"));
                        serverAdapter.add(subjectI, com.zeroc.Ice.Util.stringToIdentity("Subject"));
                        serverAdapter.activate();

                        System.out.println("\nSERVER STARTED...");

                        Runtime.getRuntime().addShutdownHook(new Thread() {
                                public void run() {
                                        System.out.println("\n\nSHUTTING DOWN SERVER...\n");
                                }
                        });

                        communicator.waitForShutdown();
                        // communicator.destroy();
                }
        }

}