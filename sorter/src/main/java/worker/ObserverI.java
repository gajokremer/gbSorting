package worker;

import com.zeroc.Ice.Current;

public class ObserverI implements Services.Observer {

    private SorterI sorterI;

    public ObserverI(SorterI sorterI) {
        this.sorterI = sorterI;
    }

    @Override
    public void update(boolean running, Current current) {
        System.out.println("\nUpdated...");
        System.out.println("Observer thread: " + Thread.currentThread().getId());
        sorterI.setRunning(running);
        // sorterI.requestTask();
    }

    @Override
    public void receiveTask(String task, Current current) {
        System.out.println("\nTask received from Server -> \n");
        System.out.println(task);
        sorterI.sort(task);
    }
}
