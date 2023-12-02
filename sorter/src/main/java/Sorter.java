import java.util.ArrayList;
import java.util.List;

import Services.ObserverPrx;
import Services.SorterPrx;
import worker.Attacher;
import worker.ObserverI;
import worker.SorterI;

public class Sorter {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.SorterManagerPrx sorterManagerProxy = Services.SorterManagerPrx
                    .checkedCast(communicator.propertyToProxy("SorterManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.DistSorterPrx distSorterProxy = Services.DistSorterPrx
                    .checkedCast(communicator.propertyToProxy("DistSorter.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.SubjectPrx subjectProxy = Services.SubjectPrx
                    .checkedCast(communicator.propertyToProxy("Subject.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            // Services.ForkJoinMasterPrx forkJoinMaster = Services.ForkJoinMasterPrx
            // .checkedCast(communicator.propertyToProxy("ForkJoinMaster.Proxy"))
            // .ice_twoway()
            // .ice_secure(false);

            if (sorterManagerProxy == null || distSorterProxy == null) {
                throw new Error("Invalid proxy");
            }

        //     int threadPoolSize = 10;
            SorterI sorterI = new SorterI();
            ObserverI observerI = new ObserverI(sorterI);

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter");
            sorterAdapter.add(sorterI, com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            sorterAdapter.add(observerI, com.zeroc.Ice.Util.stringToIdentity("Observer"));
            sorterAdapter.activate();

            SorterPrx sorterProxy = SorterPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            ObserverPrx observerProxy = ObserverPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Observer")));

            System.out.println("\nSORTER STARTED...");

            // Registry registry = new Registry(sorterManager, distSorter);
            // registry.register(sorter);

            Attacher attacher = new Attacher();
            attacher.attach(subjectProxy, observerProxy, sorterProxy);

            // long id = subjectProxy.attach(observerProxy, sorterProxy);
            // System.out.println("\nAttached...");

            // Runtime.getRuntime().addShutdownHook(new Thread() {
            // public void run() {
            // subjectProxy.detach(id);
            // System.out.println("\n\nDetached...\n");
            // }
            // });

            communicator.waitForShutdown();
        }
    }
}