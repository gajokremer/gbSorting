package receiver;

import com.zeroc.Ice.Current;

public class CallbackReceiver implements Services.CallbackReceiver {

    @Override
    public void receiveCallback(String s, Current current) {
        System.out.println("\n" + s);
    }
}
