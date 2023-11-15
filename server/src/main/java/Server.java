import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        // try(com.zeroc.Ice.Communicator communicator =
        // com.zeroc.Ice.Util.initialize(args))
        // {
        // com.zeroc.Ice.ObjectAdapter adapter =
        // communicator.createObjectAdapterWithEndpoints("SimplePrinterAdapter",
        // "default -p 10000");
        // com.zeroc.Ice.Object object = new PrinterI();
        // adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
        // adapter.activate();
        // communicator.waitForShutdown();
        // }

        List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg", extraArgs)) {

            System.out.println("\n\nSERVER STARTED...\n");

            com.zeroc.Ice.ObjectAdapter printerAdapter = communicator.createObjectAdapter("Sorter");
            printerAdapter.add(new SorterI(), com.zeroc.Ice.Util.stringToIdentity("SimpleSorter"));
            printerAdapter.activate();

            communicator.waitForShutdown();
        }
    }
}