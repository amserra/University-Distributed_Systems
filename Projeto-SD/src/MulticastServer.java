import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MulticastServer extends Thread {

    // Thread para fazer a recusrsividade e adicionar as palavras
    // Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo
    // etc)
    // Thread que peridodicamente comunica com os outros servidores multicast e
    // sincronizam as informacoes
    // Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    private ArrayList<User> listUsers = new ArrayList<User>();
    private ArrayList<URL> urlList = new ArrayList<>();

    private HashMap<String, HashSet<String>> index = new HashMap<>();

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");
        try {
            // socket = new MulticastSocket(); // create socket without binding it (only for
            // sending)
            byte[] buf = new byte[256];
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            /*String messageNewConnection = "type|connectionStarted";
            byte[] bufferNewConnection = messageNewConnection.getBytes();
            DatagramPacket packetSentNewConnection = new DatagramPacket(bufferNewConnection, bufferNewConnection.length, group, PORT);
            socket.send(packetSentNewConnection);*/


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

    public HashMap<String, HashSet<String>> getIndex() {
        return index;
    }

    public void setIndex(HashMap<String, HashSet<String>> index) {
        this.index = index;
    }

    public ArrayList<URL> getUrlList() {
        return urlList;
    }

    public void setUrlList(ArrayList<URL> urlList) {
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

    public ArrayList<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(ArrayList<User> listUsers) {
        this.listUsers = listUsers;
    }



}
