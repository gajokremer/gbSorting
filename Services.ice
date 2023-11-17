module Services {

    sequence<string> StringArr;

    interface Sorter {
        string sort(string s);
    }
    interface Reader {
        string readFile(string path);
    }
    interface Registry {
        void register(string hostname);
    }


}