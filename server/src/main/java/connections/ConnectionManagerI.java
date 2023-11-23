package connections;

import com.zeroc.Ice.Current;

import java.util.HashMap;
import java.util.Map;

import Services.CallbackReceiverPrx;
import Services.SorterPrx;

public class ConnectionManagerI implements Services.ConnectionManager {

    private Map<Long, CallbackReceiverPrx> sorterReceivers = new HashMap<>();
    private Map<Long, SorterPrx> sorters = new HashMap<>();
    private Map<Long, CallbackReceiverPrx> clientReceivers = new HashMap<>();

    private long clientCount = 0;
    private long sorterCount = 0;

    public Map<Long, CallbackReceiverPrx> getSorterReceivers() {
        return sorterReceivers;
    }

    public Map<Long, SorterPrx> getSorters() {
        return sorters;
    }

    public Map<Long, CallbackReceiverPrx> getClientReceivers() {
        return clientReceivers;
    }

    public long getClientCount() {
        return clientCount;
    }

    public long getSorterCount() {
        return sorterCount;
    }

    @Override
    public long registerClient(String hostname, CallbackReceiverPrx receiverProxy, Current current) {
        long clientId = 0;
        clientId = registerClient(receiverProxy);
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
    public long registerSorter(String hostname, CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy,
            Current current) {
        long workerId = 0;
        workerId = registerWorker(receiverProxy, sorterProxy);
        System.out.println("\n-> Registering Sorter with ID '" + workerId + "' from host: " + hostname);
        return workerId;
    }

    @Override
    public void removeSorter(long id, Current current) {
        sorterReceivers.remove(id);
        sorters.remove(id);
        sorterCount--;
        System.out.println("\n-> Removing Sorter with ID '" + id + "'");
    }

    public synchronized long registerWorker(CallbackReceiverPrx receiverProxy, SorterPrx sorterProxy) {
        sorterCount++;
        long id = sorterCount;
        sorterReceivers.put(id, receiverProxy);
        sorters.put(id, sorterProxy);
        return id;
    }

    public synchronized long registerClient(CallbackReceiverPrx receiver) {
        clientCount++;
        long id = clientCount;
        clientReceivers.put(id, receiver);
        return id;
    }
}
