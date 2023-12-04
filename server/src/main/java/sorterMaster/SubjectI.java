package sorterMaster;

import java.util.HashMap;
import java.util.Map;

import com.zeroc.Ice.Current;

import Services.ObserverPrx;
import Services.SorterPrx;

public class SubjectI implements Services.Subject {

    private Map<Long, ObserverPrx> observerProxies = new HashMap<>();
    private Map<Long, SorterPrx> sorterProxies = new HashMap<>();
    private int workerCount = 0;

    private boolean running = false;

    // public Map<Long, ObserverPrx> getObserverProxies() {
    // return observerProxies;
    // }

    public Map<Long, SorterPrx> getSorterProxies() {
        return sorterProxies;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean getRunning(Current current) {
        return running;
    }

    @Override
    public long attach(ObserverPrx observerProxy, SorterPrx sorterProxy, Current current) {
        workerCount++;
        long id = workerCount;
        synchronized (this) {
            add(id, observerProxy, sorterProxy);
        }
        System.out.println("\n-> Attached...");
        return id;
    }

    @Override
    public void detach(long id, Current current) {
        workerCount--;
        synchronized (this) {
            remove(id);
        }
        System.out.println("\n-> Detached...");
    }

    public void _notifyAll() {
        for (ObserverPrx observerPrx : observerProxies.values()) {
            observerPrx.update(running);
        }
    }

    private synchronized void add(long id, ObserverPrx observerProxy, SorterPrx sorterProxy) {
        observerProxies.put(id, observerProxy);
        sorterProxies.put(id, sorterProxy);
    }

    private synchronized void remove(long id) {
        sorterProxies.remove(id);
        observerProxies.remove(id);
    }
}
