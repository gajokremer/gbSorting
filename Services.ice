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

    interface ForkJoinTaskInterface {
        void compute();
    }

    struct ForkJoinTaskClass {
        int a;
    }

    // Server

    interface DistSorter {
        string distSort(long id, string dataPath);
        //void attach(Sorter* sorterProxy);
        //void detach(Sorter* sorterProxy);
        //bool getRunning();
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
        //void notifyAll(string s);
        bool getRunning();
    }
    interface ForkJoinMaster {
        //void invoke(ForkJoinTaskClass task);
        void invoke(ForkJoinTaskInterface* task);
    }

    // Client

    interface ResponseReceiver {
        void receiveResponse(string s);
    }
    interface Observer {
        void update(bool running);
        //string receiveTask(string task);
    }

    // Sorter

    interface Sorter {
        //string sort(string s);
       //string receiveTask(string content);
        void receiveTaskRange(string dataPath, int start, int end, long sorterId);
    }
}
