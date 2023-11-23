package receiver;

import com.zeroc.Ice.Current;

public class ResponseReceiverS implements Services.ResponseReceiver {

    // @Override
    // public void receiveCallback(String s, Current current) {
    //     System.out.println("\n" + s);
    // }
    
    @Override
    public void receiveResponse(String s, Current current) {
        System.out.println("\n" + s);
    }
}
