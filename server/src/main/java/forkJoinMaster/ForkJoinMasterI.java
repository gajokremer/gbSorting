package forkJoinMaster;

import com.zeroc.Ice.Current;

import Services.ForkJoinTaskClass;
import Services.ForkJoinTaskInterfacePrx;
import Services.SorterPrx;
import sorterPool.SorterManagerI;

public class ForkJoinMasterI implements Services.ForkJoinMaster {

    SorterManagerI sorterManager;
    ForkJoinTaskClass task;

    public ForkJoinMasterI(SorterManagerI sorterManager) {
        this.sorterManager = sorterManager;
    }

    @Override
    public void invoke(ForkJoinTaskInterfacePrx task, Current current) {
        SorterPrx sorter = sorterManager.getSorters().get(1L);
        // sorter.executeTask(task);
    }

    // @Override
    // public void invoke(ForkJoinTaskClass task, Current current) {
    //     SorterPrx sorter = sorterManager.getSorters().get(1L);
    //     sorter.executeTask(task);
    // }
}
