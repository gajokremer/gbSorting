module Services {

    sequence<string> StringArr;

    interface DistSorter;
    interface CallbackManager;
    interface Subject;
    interface CallbackReceiver;
    interface Observer;
    interface Sorter;

    // Server

    interface DistSorter {
        // string distSort(string path, Subject* subject);
        string distSort(string path);
        string sort(string s);
    }
    interface CallbackManager {
        bool initiateCallback(long id, string s);
        long registerClient(string hostname, CallbackReceiver* receiverProxy);
        void removeClient(long id);
        long registerWorker(string hostname, CallbackReceiver* receiverProxy, Sorter* sorterProxy);
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