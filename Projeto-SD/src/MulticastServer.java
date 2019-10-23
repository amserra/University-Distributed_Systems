import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {

    // Thread para fazer a recusrsividade e adicionar as palavras
    // Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo
    // etc)
    // Thread que peridodicamente comunica com os outros servidores multicast e
    // sincronizam as informacoes
    // Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4369;

    private int multicastServerNo; //Numero do servidor
    private ArrayList<Integer> multicastServerNoList = new ArrayList<>(); //Array List com os multicast servers
    private HashSet<Integer> multicastServerCheckedList = new HashSet<>(); //HashSet para verificar os multicast servers que confirmaram estarem "vivos"
    private boolean checkingMulticastServers = false; //para verificar se este multicast server está a fazer a verificação

    private ArrayList<User> listUsers = new ArrayList<User>();
    private CopyOnWriteArrayList<URL> urlList = new CopyOnWriteArrayList<>();

    private HashMap<String, HashSet<String>> index = new HashMap<>();

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super();
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            byte[] buf = new byte[256];
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

           // getMulticastServerNo(socket, group);

            //System.out.println("Server " + multicastServerNo + " is running!");

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
        try{
            String message = "type|multicastServerStarted"; 
            byte[] buffer = message.getBytes(); 
            DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packetSent);

            String messageType;
            String[] splitReceived;

            do{
                byte[] buf = new byte[64*1024];
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                splitReceived = received.split(";");

                messageType = splitReceived[0].split("\\|")[1];


            } while (!messageType.equals("multicastServerNo"));

            multicastServerNo = Integer.parseInt(splitReceived[1].split("\\|")[1]);

            int multicastServerCount = Integer.parseInt(splitReceived[2].split("\\|")[1]);

            for(int i = 0; i < multicastServerCount; i++){
                multicastServerNoList.add(Integer.parseInt(splitReceived[3 + i].split("\\|")[1]));
            }

            for(Integer i: multicastServerNoList)
                System.out.println("SERVER: " + i);



            MulticastServerControl multicastServerControl = new MulticastServerControl(this, group, socket);
            multicastServerControl.start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public HashMap<String, HashSet<String>> getIndex() {
        return index;
    }

    public void setIndex(HashMap<String, HashSet<String>> index) {
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

    public ArrayList<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(ArrayList<User> listUsers) {
        this.listUsers = listUsers;
    }

    public int getMulticastServerNo() {
        return multicastServerNo;
    }

    public void setMulticastServerNo(int multicastServerNo) {
        this.multicastServerNo = multicastServerNo;
    }

    public ArrayList<Integer> getMulticastServerNoList() {
        return multicastServerNoList;
    }

    public void setMulticastServerNoList(ArrayList<Integer> multicastServerNoList) {
        this.multicastServerNoList = multicastServerNoList;
    }

    public boolean isCheckingMulticastServers() {
        return checkingMulticastServers;
    }

    public void setCheckingMulticastServers(boolean checkingMulticastServers) {
        this.checkingMulticastServers = checkingMulticastServers;
    }

    public HashSet<Integer> getMulticastServerCheckedList() {
        return multicastServerCheckedList;
    }

    public void setMulticastServerCheckedList(HashSet<Integer> multicastServerCheckedList) {
        this.multicastServerCheckedList = multicastServerCheckedList;
    }

}
