package forkJoinMaster;

import com.zeroc.Ice.Current;

import Services.ForkJoinMasterPrx;

public class ForkTask implements Services.ForkJoinTaskInterface {

    private final int THRESHOLD = 1000;
    private ForkJoinMasterPrx master;

    // public ForkTask(ForkJoinMasterPrx master, String s) {
    // this.master = master;
    // }

    public ForkJoinMasterPrx getMaster() {
        return master;
    }

    public void setMaster(ForkJoinMasterPrx master) {
        this.master = master;
    }

    public ForkTask(String s) {

    }

    @Override
    public void compute(Current current) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compute'");
    }
}