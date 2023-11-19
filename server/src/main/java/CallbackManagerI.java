import java.util.HashMap;
import java.util.Map;

import com.zeroc.Ice.Current;

import Services.CallbackReceiverPrx;

public class CallbackManagerI implements Services.CallbackManager {

    private Map<Long, CallbackReceiverPrx> workers = new HashMap<>();

    @Override
    public boolean initiateCallback(long id, String s, Current current) {
        boolean receiverFound = false;
        CallbackReceiverPrx receiver = workers.get(id);
        boolean found = receiver != null;
        if (found) {
            try {
                receiver.receiveCallback(s);
                receiverFound = true;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return receiverFound;
    }

    @Override
    public long register(String hostname, CallbackReceiverPrx receiverProxy, Current current) {
        long clientId = 0;
        clientId = Server.generateUniqueId(hostname);
        workers.put(clientId, receiverProxy);
        System.out.println("\nClient " + clientId + " registered with hostname: " + hostname);
        return clientId;
    }
}