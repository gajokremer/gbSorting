package sorterManager;

import com.zeroc.Ice.Current;

// import Server;
import server.Server;
import Services.CallbackReceiverPrx;
import Services.SorterPrx;

public class CallbackManagerI implements Services.CallbackManager {

    // private Server server;

    // public CallbackManagerI(Server server) {
    // this.server = server;
    // }

    @Override
    public boolean initiateCallback(long id, String s, Current current) {
        CallbackReceiverPrx receiver = Server.getClientReceivers().get(id);
        boolean found = receiver != null;
        if (found) {
            try {
                receiver.receiveCallback(s);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return found;
    }

    // @Override
    // public long registerClient(String hostname, CallbackReceiverPrx receiverProxy, Current current) {
    //     long clientId = 0;
    //     clientId = Server.registerClient(receiverProxy);
    //     System.out.println("\n-> Registering Client with ID '" + clientId + "' from host: " + hostname);
    //     return clientId;
    // }

    // @Override
    // public void removeClient(long id, Current current) {
    //     Server.getClientReceivers().remove(id);
    //     Server.setClientCount(Server.getClientCount() - 1);
    //     System.out.println("\n-> Removing Client with ID '" + id + "'");
    // }

    // @Override
    // public long registerWorker(String hostname, CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy,
    //         Current current) {
    //     long workerId = 0;
    //     workerId = Server.registerWorker(receiverProxy, sorterProxy);
    //     System.out.println("\n-> Registering Worker with ID '" + workerId + "' from host: " + hostname);
    //     return workerId;
    // }

    // @Override
    // public void removeWorker(long id, Current current) {
    //     Server.getWorkerReceivers().remove(id);
    //     Server.getWorkerSorters().remove(id);
    //     Server.setWorkerCount(Server.getWorkerCount() - 1);
    //     System.out.println("\n-> Removing Worker with ID '" + id + "'");
    // }
}
