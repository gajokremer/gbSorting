import java.util.ArrayList;
import java.util.List;

import reader.InputReader;
import responseReceiver.ResponseReceiverI;

public class Client {

        public static void main(String[] args) {
                List<String> extraArgs = new ArrayList<>();

                try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg",
                                extraArgs)) {

                        // Services.SorterPrx sorter =
                        // Services.SorterPrx.checkedCast(communicator.propertyToProxy("Sorter.Proxy"))
                        // .ice_twoway()
                        // .ice_secure(false);

                        // if (sorter == null) {
                        // throw new Error("Invalid proxy");
                        // }

                        Services.DistSorterPrx distSorterProxy = Services.DistSorterPrx
                                        .checkedCast(communicator.propertyToProxy("DistSorter.Proxy"))
                                        .ice_twoway()
                                        .ice_secure(false);

                        Services.ConnectionManagerPrx connectionManagerProxy = Services.ConnectionManagerPrx
                                        .checkedCast(communicator.propertyToProxy("ConnectionManager.Proxy"))
                                        .ice_twoway()
                                        .ice_secure(false);

                        Services.ResponseManagerPrx responseManagerProxy = Services.ResponseManagerPrx
                                        .checkedCast(communicator.propertyToProxy("ResponseManager.Proxy"))
                                        .ice_twoway()
                                        .ice_secure(false);

                        if (distSorterProxy == null || connectionManagerProxy == null || responseManagerProxy == null) {
                                throw new Error("Invalid proxy");
                        }

                        com.zeroc.Ice.ObjectAdapter responseReceiverAdapter = communicator
                                        .createObjectAdapter("ResponseReceiver");
                        responseReceiverAdapter.add(new ResponseReceiverI(),
                                        com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver"));
                        responseReceiverAdapter.activate();

                        Services.ResponseReceiverPrx receiver = Services.ResponseReceiverPrx
                                        .uncheckedCast(responseReceiverAdapter.createProxy(
                                                        com.zeroc.Ice.Util.stringToIdentity("ResponseReceiver")));

                        System.out.println("\nCLIENT STARTED...");

                        // rest of the code goes here

                        // askForInput(distSorter, connectionManager, responseManager, receiver);

                        InputReader inputReader = new InputReader();
                        inputReader.askForInput(distSorterProxy, connectionManagerProxy, responseManagerProxy, receiver);

                        communicator.waitForShutdown();
                }
        }
}