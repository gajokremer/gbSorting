package sorterPool;

import java.util.HashMap;
import java.util.Map;

import com.zeroc.Ice.Current;

import Services.SorterPrx;

public class SorterManagerI implements Services.SorterManager {
    private Map<Long, SorterPrx> sorters = new HashMap<>();
    private int sorterCount = 0;

    public Map<Long, SorterPrx> getSorters() {
        return sorters;
    }

    public int getSorterCount() {
        return sorterCount;
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
}
