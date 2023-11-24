package connections;

import com.zeroc.Ice.Current;

import java.util.HashMap;
import java.util.Map;

import Services.ResponseReceiverPrx;
import Services.SorterPrx;

public class ConnectionManagerI implements Services.ConnectionManager {

    // private Map<Long, ResponseReceiverPrx> sorterReceivers = new HashMap<>();
    private Map<Long, SorterPrx> sorters = new HashMap<>();
    private Map<Long, ResponseReceiverPrx> clientReceivers = new HashMap<>();

    private int clientCount = 0;
    private int sorterCount = 0;

    // public Map<Long, ResponseReceiverPrx> getSorterReceivers() {
    // return sorterReceivers;
    // }

    public Map<Long, SorterPrx> getSorters() {
        return sorters;
    }

    public Map<Long, ResponseReceiverPrx> getClientReceivers() {
        return clientReceivers;
    }

    public int getClientCount() {
        return clientCount;
    }

    public int getSorterCount() {
        return sorterCount;
    }

    @Override
    public long registerClient(String hostname, ResponseReceiverPrx receiverProxy, Current current) {
        long clientId = 0;
        synchronized (this) {
            clientId = registerClient(receiverProxy);
        }
        System.out.println("\n-> Registering Client with ID '" + clientId + "' from host: " + hostname);
        return clientId;
    }

    @Override
    public void removeClient(long id, Current current) {
        clientReceivers.remove(id);
        clientCount--;
        System.out.println("\n-> Removing Client with ID '" + id + "'");
    }

    @Override
    public long registerSorter(String hostname, SorterPrx sorterProxy, Current current) {
        long workerId = 0;
        synchronized (this) {
            // workerId = registerSorter(receiverProxy, sorterProxy);
            workerId = registerSorter(sorterProxy);
        }
        System.out.println("\n-> Registering Sorter with ID '" + workerId + "' from host: " + hostname);
        return workerId;
    }

    @Override
    public void removeSorter(long id, Current current) {
        // sorterReceivers.remove(id);
        sorters.remove(id);
        sorterCount--;
        System.out.println("\n-> Removing Sorter with ID '" + id + "'");
    }

    private synchronized long registerSorter(SorterPrx sorterProxy) {
        sorterCount++;
        long id = sorterCount;
        // sorterReceivers.put(id, receiverProxy);
        sorters.put(id, sorterProxy);
        return id;
    }

    private synchronized long registerClient(ResponseReceiverPrx receiver) {
        clientCount++;
        long id = clientCount;
        clientReceivers.put(id, receiver);
        return id;
    }
}
