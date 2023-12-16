import java.util.ArrayList;
import java.util.List;

import clientManager.ConnectionManagerI;
import clientManager.ResponseManagerI;
import sorterMaster.ContentManager;
import sorterMaster.DistSorterI;
import sorterPool.SorterManagerI;
import sortingStatus.SubjectI;

public class Server {

        public static void main(String[] args) {
                List<String> extraArgs = new ArrayList<>();

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg",
                                extraArgs)) {

                        ConnectionManagerI connectionManager = new ConnectionManagerI();
                        SorterManagerI sorterManager = new SorterManagerI();
                        ResponseManagerI responseManager = new ResponseManagerI(connectionManager);
                        SubjectI subjectI = new SubjectI();
                        ContentManager contentManager = new ContentManager();
                        DistSorterI distSorter = new DistSorterI(responseManager, sorterManager, subjectI,
                                        contentManager);

                        // com.zeroc.Ice.ObjectAdapter serverAdapter =
                        // communicator.createObjectAdapter("Clients");
                        com.zeroc.Ice.ObjectAdapter clientsAdapter = communicator.createObjectAdapter("Clients");
                        clientsAdapter.add(distSorter, com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        clientsAdapter.add(responseManager, com.zeroc.Ice.Util.stringToIdentity("ResponseManager"));
                        clientsAdapter.add(connectionManager, com.zeroc.Ice.Util.stringToIdentity("ConnectionManager"));
                        clientsAdapter.activate();

                        com.zeroc.Ice.ObjectAdapter workersAdapter = communicator.createObjectAdapter("Workers");
                        workersAdapter.add(distSorter, com.zeroc.Ice.Util.stringToIdentity("DistSorter"));
                        workersAdapter.add(sorterManager, com.zeroc.Ice.Util.stringToIdentity("SorterManager"));
                        workersAdapter.add(subjectI, com.zeroc.Ice.Util.stringToIdentity("Subject"));
                        workersAdapter.activate();

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