package sorterMaster;

import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Current;

import Services.ObserverPrx;

public class SubjectI implements Services.Subject {

    private List<ObserverPrx> observers = new ArrayList<>();
    private boolean running = false;

    public List<ObserverPrx> getObservers() {
        return observers;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void attach(ObserverPrx observerProxy, Current current) {
        observers.add(observerProxy);

        System.out.println("\nAttached...");
    }

    @Override
    public void detach(ObserverPrx observer, Current current) {
        observers.remove(observer);
        System.out.println("\nDetached...");
    }

    @Override
    public boolean getRunning(Current current) {
        return running;
    }

    public void _notifyAll() {
        for (ObserverPrx observerPrx : observers) {
            observerPrx.update(running);
        }
    }
}
