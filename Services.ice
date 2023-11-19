module Services {

    sequence<string> StringArr;

    interface Reader;
    interface CallbackManager;
    interface Subject;
    interface CallbackReceiver;
    interface Observer;
    interface Sorter;

    // Server

    interface Reader {
        // string readFile(string path, long id, CallbackManager* callbackManager);
        string readFile(string path, Subject* subject);
    }
    interface CallbackManager {
        bool initiateCallback(long id, string s);
        long register(string hostname, CallbackReceiver* receiverProxy, Sorter* sorterProxy);
    }
    interface Subject {
        void attach(Observer* observer);
        void detach(Observer* observer);
        void notifyAll(string s);
    }

    // Manager

    interface CallbackReceiver {
        void receiveCallback(string s);
    }
    interface Observer {
        void update(string s);
    }

    // Sorter

     interface Sorter {
        string sort(string s);
    }
}