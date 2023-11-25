import java.util.ArrayList;
import java.util.List;
import Services.SorterPrx;
import registry.Registry;
import worker.SorterI;

public class Sorter {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.ResponseManagerPrx reponseManager = Services.ResponseManagerPrx
                    .checkedCast(communicator.propertyToProxy("ResponseManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            Services.ConnectionManagerPrx connectionManager = Services.ConnectionManagerPrx
                    .checkedCast(communicator.propertyToProxy("ConnectionManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (reponseManager == null || connectionManager == null) {
                throw new Error("Invalid proxy");
            }

            // com.zeroc.Ice.ObjectAdapter responseReceiverAdapter =
            // communicator.createObjectAdapter("ResponseReceiver");
            // responseReceiverAdapter.add(new ResponseReceiverS(),
            // com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver"));
            // responseReceiverAdapter.activate();
            // ResponseReceiverPrx receiver =
            // ResponseReceiverPrx.uncheckedCast(responseReceiverAdapter
            // .createProxy(com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver")));

            com.zeroc.Ice.ObjectAdapter sorterAdapter = communicator.createObjectAdapter("Sorter");
            sorterAdapter.add(new SorterI(), com.zeroc.Ice.Util.stringToIdentity("Sorter"));
            sorterAdapter.activate();
            SorterPrx sorter = SorterPrx.uncheckedCast(sorterAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("Sorter")));

            System.out.println("\nSORTER STARTED...\n");

            Registry registry = new Registry();
            registry.register(connectionManager, sorter);
        }
    }
}