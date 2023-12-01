package sorterPool;

import com.zeroc.Ice.Current;

import Services.ForkJoinMasterPrx;

public class ForkTask implements Services.ForkJoinTaskInterface {

    private final int THRESHOLD = 1000;
    private ForkJoinMasterPrx master;

    ForkTask(ForkJoinMasterPrx master, String s) {
        this.master = master;
    }

    @Override
    public void compute(Current current) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compute'");
    }
}