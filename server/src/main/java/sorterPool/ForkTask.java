package sorterPool;

import java.util.concurrent.RecursiveTask;

import Services.ForkJoinMasterPrx;

public class ForkTask<T extends Comparable<T>> extends RecursiveTask<T> {

    private final int THRESHOLD = 1000;
    private ForkJoinMasterPrx master;

    ForkTask(ForkJoinMasterPrx master) {
        this.master = master;
    }

    @Override
    protected T compute() {
        master.invoke();
        return null;
    }
}