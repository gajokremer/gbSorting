package communicator;

import com.zeroc.Ice.Current;

public class ResponseReceiverC implements Services.ResponseReceiver {

    // @Override
    // public void receiveCallback(String s, Current current) {
    //     System.out.println("\n" + s);
    // }

    @Override
    public void receiveResponse(String s, Current current) {
        System.out.println("\n" + s);
    }

}
