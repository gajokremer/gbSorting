import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Current;

import Services.ObserverPrx;

public class SubjectI implements Services.Subject {

    List<ObserverPrx> observers = new ArrayList<>();

    @Override
    public void attach(ObserverPrx observer, Current current) {
        observers.add(observer);
        System.out.println("-> New observer attached");
    }

    @Override
    public void detach(ObserverPrx observer, Current current) {
        observers.remove(observer);
    }

    @Override
    public void _notifyAll(String s, Current current) {
        for (ObserverPrx o : observers) {
            o.update(s);
        }
    }
}
