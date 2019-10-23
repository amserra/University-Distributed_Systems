import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.HashMap;

public class RMIServer extends UnicastRemoteObject implements ServerInterface {
    static final long serialVersionUID = 1L;
    int clientNo = 1; // Id for RMIServer to indentify RMIClients
    ServerInterface serverInterface; // So that backup server has the reference
    HashMap<Integer, ClientInterface> clientInterfacesMap = new HashMap<>();
    boolean isBackup; // Is backup server?
    MulticastSocket socket = null;
    final String MULTICAST_ADDRESS = "224.0.224.0";
    final int PORT = 4369;
    final int RMIPORT = 1099;
    final String RMINAME = "RMIConnection";

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        new RMIServer();
    }

    RMIServer() throws RemoteException, NotBoundException, MalformedURLException {
        super();
        connectToRMIServer();
    }

    public void notification(int clientNo) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            ClientInterface client = clientInterfacesMap.get(clientNo);
            client.notification();
        } catch (RemoteException e) {
            System.out.println("ERROR #7: Something went wrong. Would you mind to try again? :)");
        }
    }

    public void connectToRMIServer() throws AccessException, RemoteException, MalformedURLException, NotBoundException {
        // So para mensagem na consola
        boolean wasBackup = false;
        try {
            // Tentar primeiro conectar ao primary RMIServer(no caso de ser backup)
            serverInterface = (ServerInterface) Naming.lookup(RMINAME);
            String msg = serverInterface.sayHelloFromBackup();
            System.out.println(msg);
            wasBackup = true;
            this.isBackup = true;
            checkPrimaryServerStatus();
        } catch (Exception e) { // Usually java.rmi.ConnectException
            // Qual a excecao?
            System.out.println("No primary RMIServer yet. Connecting...");
        } finally {
            // Se nao for o backup server, cria ligacao
            if (!isBackup) {
                try {
                    LocateRegistry.createRegistry(RMIPORT).rebind(RMINAME, this);
                    if (wasBackup) {
                        wasBackup = false;
                        System.out.println("I am now the primary server!");
                    } else {
                        System.out.println("Primary RMIServer ready...");
                    }
                    System.out.println("Print model: \"[Message responsible] Message\"");
                } catch (Exception err) {
                    System.out.println("\nERROR: Something went wrong. Aborting program...");
                    err.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

    // For the secondary server to check if primary failed
    public void checkPrimaryServerStatus() throws InterruptedException, AccessException, RemoteException {
        boolean run = true;
        while (run) {
            Thread.sleep(5000);
            try {
                String res = serverInterface.testPrimary();

                System.out.println(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
                        + LocalDateTime.now().getSecond() + " [Primary server] " + res);
            } catch (RemoteException e) {
                System.out.println("Primary server not responding. Assuming primary functions...");
                run = false;
                this.isBackup = false;
            }
        }
    }

    public String connectToMulticast(int clientNo, String msg) throws RemoteException {
        try {
            // Send
            socket = new MulticastSocket(PORT); // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            byte[] bufferSend = msg.getBytes();
            DatagramPacket packetSend = new DatagramPacket(bufferSend, bufferSend.length, group, PORT);
            socket.send(packetSend);

            // Receive
            String type;
            int receivedClientNo;
            String msgReceive;
            do {
                byte[] bufferReceive = new byte[64 * 1024];
                DatagramPacket packetReceive = new DatagramPacket(bufferReceive, bufferReceive.length);
                socket.receive(packetReceive);
                msgReceive = new String(packetReceive.getData(), 0, packetReceive.getLength());

                // System.out.println("Mensagem recebida: " + msgReceive);

                String[] parameters = msgReceive.split(";");
                type = parameters[0].split("\\|")[1];
                receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);

                System.out.println("Type = " + type);

            } while ((!type.contains("Result")) || (receivedClientNo != clientNo));

            // System.out.println("Mensagem final: " + msgReceive);
            socket.close();
            return msgReceive;
            // dar return de msgReceive

        } catch (Exception e) {
            socket.close();
            System.out.println("ERROR: Something went wrong. Did you forget the flag? Aborting program...");
            System.exit(-1);
        }
        // Necessary but unimportant
        return null;
    }

    public String sayHelloFromBackup() throws RemoteException {
        System.out.println("[Backup server] Has just connected.");
        return "Connected to RMI Primary Server successfully!";
    }

    public String sayHelloFromClient(ClientInterface client) throws RemoteException {
        System.out.println("[Client no " + clientNo + "] " + "Has just connected.");
        this.clientInterfacesMap.put(clientNo, client);
        String msg = "Connected to RMI Primary Server successfully!\nServer gave me the id no " + clientNo;
        clientNo++;
        return msg;
    }

    public String testPrimary() throws RemoteException {
        // Called by backup to test
        // Message from primary to backup to confirm that all is ok
        return "All good";
    }

    public String authentication(int clientNo, boolean isLogin, String username, String password)
            throws RemoteException {
        String msg;
        if (isLogin)
            msg = "type|login;clientNo|" + clientNo + ";username|" + username + ";password|" + password;
        else
            msg = "type|register;clientNo|" + clientNo + ";username|" + username + ";password|" + password;

        System.out.println("Mensgem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String logout(int clientNo, String username) throws RemoteException {
        String msg = "type|logout;clientNo" + clientNo + ";username|" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String search(int clientNo, String username, String searchTerms) throws RemoteException {
        String msg;

        if (username != null)
            msg = "type|search;clientNo|" + clientNo + ";word|" + searchTerms + ";username|" + username;
        else
            msg = "type|search;clientNo|" + clientNo + ";word|" + searchTerms;

        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String indexNewURL(int clientNo, String url) throws RemoteException {
        String msg = "type|index;clientNo|" + clientNo + ";url|" + url;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String searchHistory(int clientNo, String username) throws RemoteException {
        String msg = "type|searchHistory;clientNo|" + clientNo + ";username|" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String linksPointing(int clientNo, String url) throws RemoteException {
        String msg = "type|linksPointing;clientNo|" + clientNo + ";url|" + url;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String grantPrivileges(int clientNo, String username)
            throws RemoteException, MalformedURLException, NotBoundException {
        String msg = "type|promote;clientNo|" + clientNo + ";username|" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        String[] parameters = msgReceive.split(";");
        String status = parameters[2].split("\\|")[1];
        // Prevents ArrayIndexOutOfBounds
        if ((parameters.length > 3) && (parameters[3] != null) && (status.equals("valid"))) {
            int newAdminNo = Integer.parseInt(parameters[3].split("\\|")[1]);
            ClientInterface client = clientInterfacesMap.get(newAdminNo);
            client.notification();
        }

        return msgReceive;
    }
}