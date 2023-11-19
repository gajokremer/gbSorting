import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import Services.CallbackReceiverPrx;
import receiver.CallbackReceiver;

public class Sorter {
    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "sorter.cfg", extraArgs)) {

            Services.CallbackManagerPrx callbackManager = Services.CallbackManagerPrx
                    .checkedCast(communicator.propertyToProxy("CallbackManager.Proxy"))
                    .ice_twoway()
                    .ice_secure(false);

            if (callbackManager == null) {
                throw new Error("Invalid proxy");
            }

            com.zeroc.Ice.ObjectAdapter callbackReceiverAdapter = communicator.createObjectAdapter("CallbackReceiver");
            callbackReceiverAdapter.add(new CallbackReceiver(),
                    com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver"));
            callbackReceiverAdapter.activate();
            CallbackReceiverPrx receiver = CallbackReceiverPrx.uncheckedCast(callbackReceiverAdapter
                    .createProxy(com.zeroc.Ice.Util.stringToIdentity("CallbackReceiver")));

            System.out.println("\nSORTER STARTED...\n");

            String hostname = getHostname();
            long clientId = callbackManager.register(hostname, receiver);
            System.out.println("-> Client Id: " + clientId + "\n");

            while (true) {
                // do nothing
            }
        }
    }

    private static String getHostname() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return "Unable to retrieve hostname";
        }
    }
}