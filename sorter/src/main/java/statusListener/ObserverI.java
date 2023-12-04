package statusListener;

import com.zeroc.Ice.Current;

import worker.SorterI;

public class ObserverI implements Services.Observer {

    private SorterI sorterI;

    public ObserverI(SorterI sorterI) {
        this.sorterI = sorterI;
    }

    @Override
    public void update(boolean running, Current current) {
        System.out.println("\n-> Update received...");
        // System.out.println("Observer thread: " + Thread.currentThread().getId());
        sorterI.updateRunning(running);
    }

    // @Override
    // public String receiveTask(String task, Current current) {
    // System.out.println("\nTask received from Server -> \n");
    // System.out.println(task);
    // return sorterI.sort(task);
    // }
}
