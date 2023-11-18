package receiver;

import com.zeroc.Ice.Current;

public class CallbackReceiverI implements Services.CallbackReceiver {

    @Override
    public void receiveCallback(String s, Current current) {
        System.out.println("\n" + s);
    }
}
