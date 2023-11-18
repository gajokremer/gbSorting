module Services {

    sequence<string> StringArr;

    // Server

    interface CallbackManager;
    interface CallbackReceiver;

    interface Sorter {
        string sort(string s);
    }
    interface Reader {
        string readFile(string path);
    }
    interface CallbackManager {
        bool initiateCallback(long id, string s);
        long register(string hostname, CallbackReceiver* receiverProxy);
    }

    // Manager

    interface CallbackReceiver {
        void receiveCallback(string s);
    }
}