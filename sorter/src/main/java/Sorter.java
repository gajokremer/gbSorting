import java.util.ArrayList;
import java.util.List;
import Services.SorterPrx;
import registry.Registry;
import worker.SorterI;

public class Sorter {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.SorterManagerPrx sorterManager = Services.SorterManagerPrx
                    .checkedCast(communicator.propertyToProxy("SorterManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.ForkJoinMasterPrx forkJoinMaster = Services.ForkJoinMasterPrx
                    .checkedCast(communicator.propertyToProxy("ForkJoinMaster.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (sorterManager == null || forkJoinMaster == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter");
            sorterAdapter.add(new SorterI(forkJoinMaster), com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            sorterAdapter.activate();
            SorterPrx sorter = SorterPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            System.out.println("\nSORTER STARTED...\n");

            Registry registry = new Registry();
            registry.register(sorterManager, sorter);
        }
    }
}