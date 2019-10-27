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

    private String index_file = "files/index_"; //Name of file with index
    private String url_file = "files/urls_"; //Name of file with URLs
    private String user_file = "files/users_"; //Name of file with users

    private int TIME_PERIOD = 1000; // Period time between synchronizations

    private int serverNo;
    private CopyOnWriteArrayList<MulticastServerInfo> serversList;
    private CopyOnWriteArrayList<URL> urlList;

    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index;

    private CopyOnWriteArrayList<User> usersList;

    private MulticastServer server;

    private int TCP_PORT;
    private String TCP_ADDRESS;

    
    /** 
     * @param server
     * @return 
     * 
     * Saves information in files and sends it to another Multicast Servers
     */
    public Synchronization(MulticastServer server) {
        this.serverNo = server.getMulticastServerNo();
        this.serversList = server.getMulticastServerList();
        this.index = server.getIndex();
        this.urlList = server.getUrlList();
        this.usersList = server.getListUsers();
        this.server = server;

        this.TCP_PORT = server.getTCP_PORT();
        this.TCP_ADDRESS = server.getTCP_ADDRESS();

        index_file += serverNo + ".txt";
        url_file += serverNo + ".txt";
        user_file += serverNo + ".txt";

        new IndexReceiveSync(index, urlList, TCP_PORT, usersList);

        this.start();
    }

    public void run() {
        while (true) {
            try {
                //Guardar no ficheiro de objetos o hashmap
                FileOutputStream f = new FileOutputStream(new File(index_file));
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(index);

                o.close();
                f.close();

                //Guardar no ficheiro de objetos a lista de urls
                f = new FileOutputStream(new File(url_file));
                o = new ObjectOutputStream(f);

                o.writeObject(urlList);

                o.close();
                f.close();

                //Guardar no ficheiro de objetos a lista de utilizadores
                f = new FileOutputStream(new File(user_file));
                o = new ObjectOutputStream(f);

                o.writeObject(usersList);

                o.close();
                f.close();

                //Enviar para os restantes Multicast Servers
                Socket s = null;
                ObjectOutputStream out = null;

                serversList = server.getMulticastServerList();

                for (MulticastServerInfo msi : serversList) {
                    //System.out.println("Server: " + msi.getTCP_ADDRESS() + ": " + msi.getTCP_PORT());
                    if(!(msi.getTCP_ADDRESS().equals(TCP_ADDRESS) && msi.getTCP_PORT() == TCP_PORT)){
                        // Abre o socket
                        //System.out.println("A mandar para: " + msi.getTCP_ADDRESS() + ": " + msi.getTCP_PORT());

                        s = new Socket(msi.getTCP_ADDRESS(), msi.getTCP_PORT());

                        // Stream para mandar
                        out = new ObjectOutputStream(s.getOutputStream());

                        // Manda o objeto
                        out.writeObject(index);
                        out.writeObject(urlList);
                        out.writeObject(usersList);

                        out.close();
                        s.close();
                    }
                }

                Thread.sleep(TIME_PERIOD);

            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            } catch(Exception e){
                System.out.print(e.getMessage());
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
    private int TCP_PORT;

    public IndexReceiveSync(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, int TCP_PORT, CopyOnWriteArrayList<User> usersList){
        this.index = index;
        this.urlList = urlList;
        this.usersList = usersList;
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
                new Connection(clientSocket,index, urlList, usersList);
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

    public Connection(Socket clientSocket, ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, CopyOnWriteArrayList<User> usersList){
        this.clientSocket = clientSocket;
        this.index = index;
        this.urlList = urlList;
        this.usersList = usersList;
        this.start();
    }

    /**
     * Compare the data received and the data from the multicast server and concatenates the information
     */

    public void run(){
        try{
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            for(int i = 0; i < 3; i++){ //Vai receber o HashMap com os indices e a lista de URLs
                Object data = in.readObject();
                if(data instanceof ConcurrentHashMap<?,?>){
                    ConcurrentHashMap<String, CopyOnWriteArraySet<String>> receivedIndex = (ConcurrentHashMap<String, CopyOnWriteArraySet<String>>) data;

                   // System.out.println(receivedIndex);

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

                        for(URL url: receivedList){
                            //System.out.println(url);
                            if(urlList.contains(url)){
                                int urlIndex = urlList.indexOf(url);
                                URL editedURL = urlList.get(urlIndex);
    
                                //System.out.println(editedURL);
                                editedURL.getUrlPointingToMeList().addAll(url.getUrlPointingToMeList());

                                editedURL.setLinksCount(editedURL.getUrlPointingToMeList().size());
    
                            }else{
                                urlList.add(url);
                            }
                        } 
                    } catch(ClassCastException e){
                        CopyOnWriteArrayList<User> receivedList = (CopyOnWriteArrayList<User>) data;

                        for(User user: receivedList){

                            if(!usersList.contains(user))
                                usersList.add(user);

                        } 
                    }

                    //System.out.println(receivedUrlList);

                   
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