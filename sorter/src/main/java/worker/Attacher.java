package worker;

import Services.ObserverPrx;
import Services.SorterPrx;
import Services.SubjectPrx;

public class Attacher {
    public void attach(SubjectPrx subjectProxy, ObserverPrx observerProxy, SorterPrx sorterProxy) {
        long id = subjectProxy.attach(observerProxy, sorterProxy);
        System.out.println("\nAttaching...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                subjectProxy.detach(id);
                System.out.println("\n\nDetaching...\n");
            }
        });
    }
}
