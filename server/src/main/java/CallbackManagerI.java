
import com.zeroc.Ice.Current;

import Services.CallbackReceiverPrx;
import Services.SorterPrx;

public class CallbackManagerI implements Services.CallbackManager {

    @Override
    public boolean initiateCallback(long id, String s, Current current) {
        CallbackReceiverPrx receiver = Server.getClients().get(id);
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

    @Override
    public long registerClient(String hostname, CallbackReceiverPrx receiverProxy, Current current) {
        long clientId = 0;
        clientId = Server.registerClient(receiverProxy);
        System.out.println("\nRegistering client with ID '" + clientId + "' from host: " + hostname);
        return clientId;
    }

    @Override
    public void removeClient(long id, Current current) {
        Server.getClients().remove(id);
        Server.setClientCount(Server.getClientCount() - 1);
        System.out.println("\nRemoving client with ID '" + id + "'");
    }

    @Override
    public long registerWorker(String hostname, CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy,
            Current current) {
        long workerId = 0;
        workerId = Server.registerWorker(receiverProxy, sorterProxy);
        System.out.println("\nRegistering worker with ID '" + workerId + "' from host: " + hostname);
        return workerId;
    }
}
