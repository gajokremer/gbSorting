module Services {

    sequence<string> StringArr;

    interface DistSorter;
    interface ResponseManager;
    interface Subject;
    interface ResponseReceiver;
    interface Observer;
    interface Sorter;

    // Server

    interface DistSorter {
        string distSort(long id, string path);
        //string sort(string s);
    }
    interface ResponseManager {
        //bool initiateCallback(long id, string s);
        bool respondToClient(long id, string response);
    }
    interface ConnectionManager {
        long registerClient(string hostname, ResponseReceiver* receiverProxy);
        void removeClient(long id);
        //long registerSorter(string hostname, Sorter* sorterProxy);
        //void removeSorter(long id);
    }
    interface SorterManager {
        long registerSorter(string hostname, Sorter* sorterProxy);
        void removeSorter(long id);
    }
    interface Subject {
        void attach(Observer* observer);
        void detach(Observer* observer);
        void notifyAll(string s);
    }

    // Client

    interface ResponseReceiver {
        //void receiveCallback(string response);
        void receiveResponse(string s);
    }
    interface Observer {
        void update(string s);
    }

    // Sorter

    interface Sorter {
        string sort(string s);
    }
}