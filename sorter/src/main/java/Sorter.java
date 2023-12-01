import java.util.ArrayList;
import java.util.List;

import Services.SorterPrx;
import registry.Registry;
import worker.SorterI;

public class Sorter {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.SorterManagerPrx sorterManagerPrx = Services.SorterManagerPrx
                    .checkedCast(communicator.propertyToProxy("SorterManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.DistSorterPrx distSorterPrx = Services.DistSorterPrx
                    .checkedCast(communicator.propertyToProxy("DistSorter.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            // Services.ForkJoinMasterPrx forkJoinMaster = Services.ForkJoinMasterPrx
            // .checkedCast(communicator.propertyToProxy("ForkJoinMaster.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            if (sorterManagerPrx == null || distSorterPrx == null) {
                throw new Error("Invalid proxy");
            }

            SorterI sorterI = new SorterI(distSorterPrx);

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter");
            sorterAdapter.add(sorterI, com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            sorterAdapter.activate();

            SorterPrx sorterPrx = SorterPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            System.out.println("\nSORTER STARTED...");

            // Registry registry = new Registry(sorterManager, distSorter);
            // registry.register(sorter);

            distSorterPrx.attach(sorterPrx);
            System.out.println("\nAttached...");

            System.out.println("\nThread id 1: "+Thread.currentThread().getId());
            distSorterPrx.getRunning();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    distSorterPrx.detach(sorterPrx);
                    System.out.println("\n\nDetached...\n");
                }
            });

            communicator.waitForShutdown();
        }
    }
}