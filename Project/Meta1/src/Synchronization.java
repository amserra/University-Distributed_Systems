import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class Synchronization extends Thread {

    private String index_file = "Meta1/files/index_"; //Name of file with index
    private String url_file = "Meta1/files/urls_"; //Name of file with URLs
    private String user_file = "Meta1/files/users_"; //Name of file with users
    private String search_file = "Meta1/files/search_"; // Namoe of files with searches

    private int TIME_PERIOD = 10000; // Period time between synchronizations

    private int serverNo;
    private CopyOnWriteArrayList<MulticastServerInfo> serversList;
    private CopyOnWriteArrayList<URL> urlList;
    private CopyOnWriteArrayList<Search> searchList;

    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index;

    private CopyOnWriteArrayList<User> usersList;

    private MulticastServer server;

    private int TCP_PORT;
    private String TCP_ADDRESS;

    
    /** 
     * Saves information in files and sends it to another Multicast Servers
     * @param server
     * @return 
     */
    public Synchronization(MulticastServer server) {
        this.serverNo = server.getMulticastServerNo();
        this.serversList = server.getMulticastServerList();
        this.index = server.getIndex();
        this.urlList = server.getUrlList();
        this.usersList = server.getListUsers();
        this.searchList = server.getSearchList();
        this.server = server;

        this.TCP_PORT = server.getTCP_PORT();
        this.TCP_ADDRESS = server.getTCP_ADDRESS();

        index_file += serverNo + ".txt";
        url_file += serverNo + ".txt";
        user_file += serverNo + ".txt";
        search_file += serverNo + ".txt";

        new IndexReceiveSync(index, urlList, TCP_PORT, usersList, searchList);

        this.start();
    }

    /**
     * Save information to files and sends it other multicast servers via TCP sockets
     */
    public void run() {
        while (true) {
            try {
                System.out.println("Writing to files. Do not CTRL+C");

                //Saves hashmap in file
                FileOutputStream f = new FileOutputStream(new File(index_file));
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(index);

                o.close();
                f.close();

                //Save URL list in files
                f = new FileOutputStream(new File(url_file));
                o = new ObjectOutputStream(f);

                o.writeObject(urlList);

                o.close();
                f.close();

                //Save users list in files
                f = new FileOutputStream(new File(user_file));
                o = new ObjectOutputStream(f);

                o.writeObject(usersList);

                o.close();
                f.close();

                //Save search list in files
                f = new FileOutputStream(new File(search_file));
                o = new ObjectOutputStream(f);

                o.writeObject(searchList);

                o.close();
                f.close();

                Socket s = null;
                ObjectOutputStream out = null;

                serversList = server.getMulticastServerList();

                //Send to others Multicast servers
                for (MulticastServerInfo msi : serversList) {
                    if(!(msi.getTCP_ADDRESS().equals(TCP_ADDRESS) && msi.getTCP_PORT() == TCP_PORT)){

                        s = new Socket(msi.getTCP_ADDRESS(), msi.getTCP_PORT());

                        out = new ObjectOutputStream(s.getOutputStream());

                        out.writeObject(index);
                        out.writeObject(urlList);
                        out.writeObject(usersList);
                        out.writeObject(searchList);

                        out.close();
                        s.close();
                    }
                }

                System.out.println("You can now CTRL+C");

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            } catch(Exception e){
                System.out.print(e.getMessage());
            }

            try {
                Thread.sleep(TIME_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

/**
 * Thread that wait for TCP connections from other multicast servers
 */
class IndexReceiveSync extends Thread {

    private CopyOnWriteArrayList<URL> urlList;
    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index;
    private CopyOnWriteArrayList<User> usersList;
    private CopyOnWriteArrayList<Search> searchList;
    private int TCP_PORT;

    public IndexReceiveSync(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, int TCP_PORT, CopyOnWriteArrayList<User> usersList, CopyOnWriteArrayList<Search> searchList){
        this.index = index;
        this.urlList = urlList;
        this.usersList = usersList;
        this.searchList = searchList;
        this.TCP_PORT = TCP_PORT;
        this.start();
    }
    
    /**
     * Listen socket that wait for TCP connections
     */
    public void run(){
        try{
            ServerSocket listenSocket = new ServerSocket(TCP_PORT);
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                new Connection(clientSocket,index, urlList, usersList, searchList);
            }
        }catch(IOException e){
            System.out.println("Listen:" + e.getMessage());
        }
    }
}

/**
 * Thread that takes care of the data received
 */
class Connection extends Thread{
    Socket clientSocket;
    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index;
    private CopyOnWriteArrayList<URL> urlList;
    private CopyOnWriteArrayList<User> usersList;
    private CopyOnWriteArrayList<Search> searchList;

    public Connection(Socket clientSocket, ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, CopyOnWriteArrayList<User> usersList, CopyOnWriteArrayList<Search> searchList){
        this.clientSocket = clientSocket;
        this.index = index;
        this.urlList = urlList;
        this.usersList = usersList;
        this.searchList = searchList;
        this.start();
    }

    /**
     * Compare the data received and the data from the multicast server and concatenates the information
     */

    public synchronized void run(){
        try{
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            for(int i = 0; i < 4; i++){ //Receives 1 HashMap and 3 ArrayLists
                Object data = in.readObject();
                if(data instanceof ConcurrentHashMap<?,?>){
                    ConcurrentHashMap<String, CopyOnWriteArraySet<String>> receivedIndex = (ConcurrentHashMap<String, CopyOnWriteArraySet<String>>) data;

                    //Synchronize data from hash maps
                    for(String s: receivedIndex.keySet()){
                        if(index.containsKey(s)){
                            CopyOnWriteArraySet<String> indexValue = index.get(s);
                            CopyOnWriteArraySet<String> receivedIndexValue = receivedIndex.get(s);
                            indexValue.addAll(receivedIndexValue);
                        }
                        else{
                            index.put(s, receivedIndex.get(s));
                        }
                    }
                } else if(data instanceof CopyOnWriteArrayList<?>){
                    try{
                        CopyOnWriteArrayList<URL> receivedList = (CopyOnWriteArrayList<URL>) data;

                        //Synchronize data from array lists
                        for(URL url: receivedList){

                            if(urlList.contains(url)){
                                int urlIndex = urlList.indexOf(url);
                                URL editedURL = urlList.get(urlIndex);
    
                                editedURL.getUrlPointingToMeList().addAll(url.getUrlPointingToMeList());

                                editedURL.setLinksCount(editedURL.getUrlPointingToMeList().size());
    
                            }else{
                                urlList.add(url);
                            }
                        } 
                    } catch(ClassCastException e){
                        try{
                            CopyOnWriteArrayList<User> receivedList = (CopyOnWriteArrayList<User>) data;

                            //Check is server has user in list
                            for(User user: receivedList){

                                if(!usersList.contains(user))
                                    usersList.add(user);

                            } 
                        } catch(ClassCastException e2){
                            CopyOnWriteArrayList<Search> receivedList = (CopyOnWriteArrayList<Search>) data;

                            //Check is server has search in list
                            for(Search s: receivedList){

                                if(!searchList.contains(s))
                                    searchList.add(s);

                            } 
                        }

                    }
                }

            }

            in.close();
            clientSocket.close();
        } catch(EOFException e){
            System.out.println("EOF:" + e);
            e.printStackTrace();
        } catch(IOException e){
            System.out.println("IO:" + e);
        } catch(ClassNotFoundException e){
            System.out.println(e);
        }
    }
}