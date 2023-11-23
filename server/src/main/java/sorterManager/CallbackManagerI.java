package sorterManager;

import com.zeroc.Ice.Current;

import Services.CallbackReceiverPrx;
import connections.ConnectionManagerI;

public class CallbackManagerI implements Services.CallbackManager {

    private ConnectionManagerI connectionManager;

    public ConnectionManagerI getConnectionManager() {
        return connectionManager;
    }

    public CallbackManagerI(ConnectionManagerI connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean initiateCallback(long id, String s, Current current) {
        // CallbackReceiverPrx receiver = Server.getClientReceivers().get(id);
        CallbackReceiverPrx receiver = connectionManager.getClientReceivers().get(id);
        boolean found = receiver != null;
        if (found) {
            try {
                receiver.receiveCallback(s);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return found;
    }
}
