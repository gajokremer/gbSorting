package observer;
import com.zeroc.Ice.Current;

public class ObserverI implements Services.Observer {

    @Override
    public void update(String s, Current current) {
        System.out.println("\n" + s);
    }
}
