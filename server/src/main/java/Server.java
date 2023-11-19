
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Server {
    private static Map<Long, String> workers = new HashMap<>();
    private static long clientCount = 0;

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

            System.out.println("\nSERVER STARTED...\n");

            // com.zeroc.Ice.ObjectAdapter sorterAdapter =
            // communicator.createObjectAdapter("Sorter");
            // sorterAdapter.add(new SorterI(),
            // com.zeroc.Ice.Util.stringToIdentity("SimpleSorter"));
            // sorterAdapter.activate();

            com.zeroc.Ice.ObjectAdapter readerAdapter = communicator.createObjectAdapter("Reader");
            readerAdapter.add(new ReaderI(), com.zeroc.Ice.Util.stringToIdentity("SimpleReader"));
            readerAdapter.activate();

            com.zeroc.Ice.ObjectAdapter managerAdapter = communicator.createObjectAdapter("CallbackManager");
            managerAdapter.add(new CallbackManagerI(), com.zeroc.Ice.Util.stringToIdentity("CallbackManager"));
            managerAdapter.activate();

            com.zeroc.Ice.ObjectAdapter subjectAdapter = communicator.createObjectAdapter("Subject");
            subjectAdapter.add(new SubjectI(), com.zeroc.Ice.Util.stringToIdentity("Subject"));
            subjectAdapter.activate();

            communicator.waitForShutdown();
            // communicator.destroy();
        }
    }

    public static synchronized long generateUniqueId(String ipAddress) {
        clientCount++;
        long id = clientCount;
        workers.put(id, ipAddress);
        return id;
    }
}