module Services {

    sequence<string> StringArr;
    sequence<int> IntArr;

    interface DistSorter;
    interface ResponseManager;
    interface Subject;
    interface ResponseReceiver;
    interface Observer;
    interface Sorter;
    interface ConnectionManager;
    interface SorterManager;
    interface ForkJoinMaster;

    // Server

    interface DistSorter {
        void distSort(long id, string dataPath);
    }
    interface ResponseManager {
        bool respondToClient(long id, string response);
    }
    interface ConnectionManager {
        long registerClient(string hostname, ResponseReceiver* receiverProxy);
        void removeClient(long id);
    }
    interface SorterManager {
        long registerSorter(string hostname, Sorter* sorterProxy);
        void removeSorter(long id);
    }
    interface Subject {
        long attach(Observer* observerProxy, Sorter* sorterProxy);
        void detach(long id);
    }

    // Client

    interface ResponseReceiver {
        void receiveResponse(string s);
    }
    interface Observer {
        void update(bool running);
    }

    // Sorter

    interface Sorter {
        void receiveTask(string dataPath, int start, int end, long sorterId);
    }
}
