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

    // Thread para fazer a recusrsividade e adicionar as palavras
    // Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo
    // etc)
    // Thread que peridodicamente comunica com os outros servidores multicast e
    // sincronizam as informacoes
    // Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4369;
    private String TCP_ADDRESS;
    private int TCP_PORT;

    private int multicastServerNo; // Numero do servidor
    private CopyOnWriteArrayList<MulticastServerInfo> multicastServerList = new CopyOnWriteArrayList<>(); // Array List com os multicast servers
    private CopyOnWriteArraySet<Integer> multicastServerCheckedList = new CopyOnWriteArraySet<>(); // HashSet para
                                                                                                   // verificar os
                                                                                                   // multicast servers
                                                                                                   // que confirmaram
                                                                                                   // estarem "vivos"
    private boolean checkingMulticastServers = false; // para verificar se este multicast server está a fazer a
                                                      // verificação

    private CopyOnWriteArrayList<User> listUsers = new CopyOnWriteArrayList<User>(); // Lista de utilizadores
    private CopyOnWriteArrayList<URL> urlList = new CopyOnWriteArrayList<>(); // Lista de URLs
    private CopyOnWriteArrayList<Search> searchList = new CopyOnWriteArrayList<>(); //Lista de pesquisas

    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index = new ConcurrentHashMap<>(); // HashMap com os
                                                                                                      // URLs para cada
                                                                                                      // palavra

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.setTCP_ADDRESS(args[0]);
        server.setTCP_PORT(Integer.parseInt(args[1]));
        server.start();
    }

    public MulticastServer() {
        super();
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            byte[] buf = new byte[64*1024];
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            getMulticastServerNo(socket, group);

            getMulticastServerFiles();

            new IndexSync(this);

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

    private void getMulticastServerNo(MulticastSocket socket, InetAddress group) {
        try {
            String message = "type|multicastServerStarter;ipAddress|" + this.getTCP_ADDRESS() + ";port|" + this.getTCP_PORT();
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

                messageType = splitReceived[0].split("\\|")[1];

            } while (!messageType.equals("multicastServerStarterResult"));

            multicastServerNo = Integer.parseInt(splitReceived[1].split("\\|")[1]);

            int multicastServerCount = Integer.parseInt(splitReceived[2].split("\\|")[1]);

            for (int i = 3; i < (multicastServerCount * 3 + 3); i = i + 3) {
                int serverNo = Integer.parseInt(splitReceived[i].split("\\|")[1]);
                String address = splitReceived[i + 1].split("\\|")[1];
                int port = Integer.parseInt(splitReceived[i + 2].split("\\|")[1]);
                MulticastServerInfo msi = new MulticastServerInfo(serverNo, address, port);
                multicastServerList.add(msi);
            }

            for (MulticastServerInfo msi : multicastServerList)
                System.out.println("SERVER: " + msi.getServerNo() + "\nEndereço: " + msi.getTCP_ADDRESS() + "\nPorto: " + msi.getTCP_PORT());

            MulticastServerControl multicastServerControl = new MulticastServerControl(this, group, socket);
            multicastServerControl.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMulticastServerFiles() {
        String index_file = "files/index_" + getMulticastServerNo() + ".txt";
        String url_file = "files/urls_" + getMulticastServerNo() + ".txt";

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

    public ConcurrentHashMap<String, CopyOnWriteArraySet<String>> getIndex() {
        return index;
    }

    public void setIndex(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index) {
        this.index = index;
    }

    public CopyOnWriteArrayList<URL> getUrlList() {
        return urlList;
    }

    public void setUrlList(CopyOnWriteArrayList<URL> urlList) {
        this.urlList = urlList;
    }

    public String getMULTICAST_ADDRESS() {
        return MULTICAST_ADDRESS;
    }

    public void setMULTICAST_ADDRESS(String mULTICAST_ADDRESS) {
        MULTICAST_ADDRESS = mULTICAST_ADDRESS;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int pORT) {
        PORT = pORT;
    }

    public CopyOnWriteArrayList<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(CopyOnWriteArrayList<User> listUsers) {
        this.listUsers = listUsers;
    }

    public int getMulticastServerNo() {
        return multicastServerNo;
    }

    public void setMulticastServerNo(int multicastServerNo) {
        this.multicastServerNo = multicastServerNo;
    }

    public CopyOnWriteArrayList<MulticastServerInfo> getMulticastServerList() {
        return multicastServerList;
    }

    public void setMulticastServerList(CopyOnWriteArrayList<MulticastServerInfo> multicastServerList) {
        this.multicastServerList = multicastServerList;
    }

    public boolean isCheckingMulticastServers() {
        return checkingMulticastServers;
    }

    public void setCheckingMulticastServers(boolean checkingMulticastServers) {
        this.checkingMulticastServers = checkingMulticastServers;
    }

    public CopyOnWriteArraySet<Integer> getMulticastServerCheckedList() {
        return multicastServerCheckedList;
    }

    public void setMulticastServerCheckedList(CopyOnWriteArraySet<Integer> multicastServerCheckedList) {
        this.multicastServerCheckedList = multicastServerCheckedList;
    }

    public String getTCP_ADDRESS() {
        return TCP_ADDRESS;
    }

    public void setTCP_ADDRESS(String tCP_ADDRESS) {
        TCP_ADDRESS = tCP_ADDRESS;
    }

    public int getTCP_PORT() {
        return TCP_PORT;
    }

    public void setTCP_PORT(int tCP_PORT) {
        TCP_PORT = tCP_PORT;
    }

    public CopyOnWriteArrayList<Search> getSearchList() {
        return searchList;
    }

    public void setSearchList(CopyOnWriteArrayList<Search> searchList) {
        this.searchList = searchList;
    }

}
