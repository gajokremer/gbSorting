package connections;

import com.zeroc.Ice.Current;

import Services.CallbackReceiverPrx;
import Services.SorterPrx;
import server.Server;

public class ConnectionManagerI implements Services.ConnectionManager {

    @Override
    public long registerClient(String hostname, CallbackReceiverPrx receiverProxy, Current current) {
        long clientId = 0;
        clientId = Server.registerClient(receiverProxy);
        System.out.println("\n-> Registering Client with ID '" + clientId + "' from host: " + hostname);
        return clientId;
    }

    @Override
    public void removeClient(long id, Current current) {
        Server.getClientReceivers().remove(id);
        Server.setClientCount(Server.getClientCount() - 1);
        System.out.println("\n-> Removing Client with ID '" + id + "'");
    }

    @Override
    public long registerSorter(String hostname, CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy,
            Current current) {
        long workerId = 0;
        workerId = Server.registerWorker(receiverProxy, sorterProxy);
        System.out.println("\n-> Registering Sorter with ID '" + workerId + "' from host: " + hostname);
        return workerId;
    }

    @Override
    public void removeSorter(long id, Current current) {
        Server.getSorterReceivers().remove(id);
        Server.getSorters().remove(id);
        Server.setSorterCount(Server.getSorterCount() - 1);
        System.out.println("\n-> Removing Sorter with ID '" + id + "'");
    }
}
