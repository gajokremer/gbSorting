package responseReceiver;

import com.zeroc.Ice.Current;

public class ResponseReceiverI implements Services.ResponseReceiver {

    // @Override
    // public void receiveCallback(String s, Current current) {
    // System.out.println("\n" + s);
    // }

    @Override
    public void receiveResponse(String response, Current current) {
        System.out.println("\n" + response);
    }

}
