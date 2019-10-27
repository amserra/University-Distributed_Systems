import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class MulticastServer extends Thread {

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4369;
    private String TCP_ADDRESS;
    private int TCP_PORT;

    private int multicastServerNo; // Server Number
    private CopyOnWriteArrayList<MulticastServerInfo> multicastServerList = new CopyOnWriteArrayList<>(); // Array List with information about multicast servers
    private CopyOnWriteArraySet<Integer> multicastServerCheckedList = new CopyOnWriteArraySet<>(); // HashSet to verify which multicast server are alive
                                                                                        
    private boolean checkingMulticastServers = false; // check if it is verifying status of other multicast servers

    private CopyOnWriteArrayList<User> listUsers = new CopyOnWriteArrayList<User>(); // List with information about Users
    private CopyOnWriteArrayList<URL> urlList = new CopyOnWriteArrayList<>(); // List with information about URLs
    private CopyOnWriteArrayList<Search> searchList = new CopyOnWriteArrayList<>(); //Searches List

    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index = new ConcurrentHashMap<>(); // HashMap com os URLs para cada palavra
                                                                                                      
    /** 
     * @param args
     * Saves the Address and port given
     */
    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.setTCP_ADDRESS(args[0]);
        server.setTCP_PORT(Integer.parseInt(args[1]));
        server.start();
    }

    
    /** 
     * @return 
     */
    public MulticastServer() {
        super();
    }


    /**
     * Multicast Server Thread
     * Call the methods to get Multicast Server No, files and starts a thread to do synchronization between Multicast Servers
     * After that, it waits to receive calls from the RMI server or other Multicast servers
     */
    public void run() {
        MulticastSocket socket = null;
        try {
            byte[] buf = new byte[64*1024];
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            getMulticastServerNo(socket, group); //Get server number and information about other multicast servers

            getMulticastServerFiles(); //Get information in files

            MulticastServerControl multicastServerControl = new MulticastServerControl(this, group, socket);
            multicastServerControl.start(); //Thread that checks status of other multicast servers

            new Synchronization(this); //Thread that takes care of synchronization

            System.out.println("Server " + multicastServerNo + " is running!");

            while (true) {
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                MulticastServerAction newAction = new MulticastServerAction(received, socket, group, this);
                newAction.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    
    /** 
     * @param socket
     * @param group
     * Send message to RMI server warning that a new Multicast Server has started.
     * Then it receives a message with a given server number and the information about the other Multicast Servers
     */
    private void getMulticastServerNo(MulticastSocket socket, InetAddress group) {
        try {
            String message = "type|||multicastServerStarter;;ipAddress|||" + this.getTCP_ADDRESS() + ";;port|||" + this.getTCP_PORT();
            byte[] buffer = message.getBytes();
            DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packetSent);

            String messageType;
            String[] splitReceived;

            do {
                byte[] buf = new byte[64 * 1024];
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                splitReceived = received.split(";");

                messageType = splitReceived[0].split("\\|\\|\\|")[1];

            } while (!messageType.equals("multicastServerStarterResult"));

            multicastServerNo = Integer.parseInt(splitReceived[1].split("\\|\\|\\|")[1]);

            int multicastServerCount = Integer.parseInt(splitReceived[2].split("\\|\\|\\|")[1]);

            for (int i = 3; i < (multicastServerCount * 3 + 3); i = i + 3) {
                int serverNo = Integer.parseInt(splitReceived[i].split("\\|\\|\\|")[1]);
                String address = splitReceived[i + 1].split("\\|\\|\\|")[1];
                int port = Integer.parseInt(splitReceived[i + 2].split("\\|\\|\\|")[1]);
                MulticastServerInfo msi = new MulticastServerInfo(serverNo, address, port);
                multicastServerList.add(msi);
            }

            for (MulticastServerInfo msi : multicastServerList)
                System.out.println("SERVER: " + msi.getServerNo() + "\nEndereço: " + msi.getTCP_ADDRESS() + "\nPorto: " + msi.getTCP_PORT());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch information from files of this server.
     * Information is from index, URL's and users
     */
    private void getMulticastServerFiles() {
        String index_file = "files/index_" + getMulticastServerNo() + ".txt";
        String url_file = "files/urls_" + getMulticastServerNo() + ".txt";
        String users_file = "files/users_" + getMulticastServerNo() + ".txt";

        //Ler ficheiro do servidor com o hashmap
        try {
            FileInputStream f = new FileInputStream(new File(index_file));
            ObjectInputStream o = new ObjectInputStream(f);

            index = (ConcurrentHashMap<String, CopyOnWriteArraySet<String>>) o.readObject();

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch(ClassNotFoundException e){
            System.out.println("Error transfering HashMap");
        }

        //Ler ficheiro do servidor com a lista de URLs
        try {
            FileInputStream f = new FileInputStream(new File(url_file));
            ObjectInputStream o = new ObjectInputStream(f);

            urlList = (CopyOnWriteArrayList<URL>) o.readObject();

            for(URL url: urlList)
                System.out.println(url);

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch(ClassNotFoundException e){
            System.out.println("Error transfering URL List");
        }

        //Ler ficheiro do servidor com a lista de utilizadores
        try {
            FileInputStream f = new FileInputStream(new File(users_file));
            ObjectInputStream o = new ObjectInputStream(f);

            listUsers = (CopyOnWriteArrayList<User>) o.readObject();

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch(ClassNotFoundException e){
            System.out.println("Error transfering URL List");
        }

    }

    
    /** 
     * @return ConcurrentHashMap<String, CopyOnWriteArraySet<String>>
     */
    public ConcurrentHashMap<String, CopyOnWriteArraySet<String>> getIndex() {
        return index;
    }

    
    /** 
     * @param index
     */
    public void setIndex(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index) {
        this.index = index;
    }

    
    /** 
     * @return CopyOnWriteArrayList<URL>
     */
    public CopyOnWriteArrayList<URL> getUrlList() {
        return urlList;
    }

    
    /** 
     * @param urlList
     */
    public void setUrlList(CopyOnWriteArrayList<URL> urlList) {
        this.urlList = urlList;
    }

    
    /** 
     * @return String
     */
    public String getMULTICAST_ADDRESS() {
        return MULTICAST_ADDRESS;
    }

    
    /** 
     * @param mULTICAST_ADDRESS
     */
    public void setMULTICAST_ADDRESS(String mULTICAST_ADDRESS) {
        MULTICAST_ADDRESS = mULTICAST_ADDRESS;
    }

    
    /** 
     * @return int
     */
    public int getPORT() {
        return PORT;
    }

    
    /** 
     * @param pORT
     */
    public void setPORT(int pORT) {
        PORT = pORT;
    }

    
    /** 
     * @return CopyOnWriteArrayList<User>
     */
    public CopyOnWriteArrayList<User> getListUsers() {
        return listUsers;
    }

    
    /** 
     * @param listUsers
     */
    public void setListUsers(CopyOnWriteArrayList<User> listUsers) {
        this.listUsers = listUsers;
    }

    
    /** 
     * @return int
     */
    public int getMulticastServerNo() {
        return multicastServerNo;
    }

    
    /** 
     * @param multicastServerNo
     */
    public void setMulticastServerNo(int multicastServerNo) {
        this.multicastServerNo = multicastServerNo;
    }

    
    /** 
     * @return CopyOnWriteArrayList<MulticastServerInfo>
     */
    public CopyOnWriteArrayList<MulticastServerInfo> getMulticastServerList() {
        return multicastServerList;
    }

    
    /** 
     * @param multicastServerList
     */
    public void setMulticastServerList(CopyOnWriteArrayList<MulticastServerInfo> multicastServerList) {
        this.multicastServerList = multicastServerList;
    }

    
    /** 
     * @return boolean
     */
    public boolean isCheckingMulticastServers() {
        return checkingMulticastServers;
    }

    
    /** 
     * @param checkingMulticastServers
     */
    public void setCheckingMulticastServers(boolean checkingMulticastServers) {
        this.checkingMulticastServers = checkingMulticastServers;
    }

    
    /** 
     * @return CopyOnWriteArraySet<Integer>
     */
    public CopyOnWriteArraySet<Integer> getMulticastServerCheckedList() {
        return multicastServerCheckedList;
    }

    
    /** 
     * @param multicastServerCheckedList
     */
    public void setMulticastServerCheckedList(CopyOnWriteArraySet<Integer> multicastServerCheckedList) {
        this.multicastServerCheckedList = multicastServerCheckedList;
    }

    
    /** 
     * @return String
     */
    public String getTCP_ADDRESS() {
        return TCP_ADDRESS;
    }

    
    /** 
     * @param tCP_ADDRESS
     */
    public void setTCP_ADDRESS(String tCP_ADDRESS) {
        TCP_ADDRESS = tCP_ADDRESS;
    }

    
    /** 
     * @return int
     */
    public int getTCP_PORT() {
        return TCP_PORT;
    }

    
    /** 
     * @param tCP_PORT
     */
    public void setTCP_PORT(int tCP_PORT) {
        TCP_PORT = tCP_PORT;
    }

    
    /** 
     * @return CopyOnWriteArrayList<Search>
     */
    public CopyOnWriteArrayList<Search> getSearchList() {
        return searchList;
    }

    
    /** 
     * @param searchList
     */
    public void setSearchList(CopyOnWriteArrayList<Search> searchList) {
        this.searchList = searchList;
    }

}
