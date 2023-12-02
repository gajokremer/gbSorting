package clientManager;

import com.zeroc.Ice.Current;

import Services.ResponseReceiverPrx;

public class ResponseManagerI implements Services.ResponseManager {

    private ConnectionManagerI connectionManager;

    public ConnectionManagerI getConnectionManager() {
        return connectionManager;
    }

    public ResponseManagerI(ConnectionManagerI connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    // public boolean initiateCallback(long id, String s, Current current) {
    public boolean respondToClient(long id, String response, Current current) {
        ResponseReceiverPrx receiverProxy = connectionManager.getClientReceivers().get(id);
        boolean found = receiverProxy != null;
        if (found) {
            try {
                // receiver.receiveCallback(s);
                receiverProxy.receiveResponse(response);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return found;
    }
}
