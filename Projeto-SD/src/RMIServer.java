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
import java.util.concurrent.CopyOnWriteArrayList;

public class RMIServer extends UnicastRemoteObject implements ServerInterface {
    static final long serialVersionUID = 1L;
    int clientNo = 1; // Id for RMIServer to indentify RMIClients // ATOMIC INT
    ServerInterface serverInterface; // So that backup server has the reference
    HashMap<Integer, ClientInterface> clientInterfacesMap = new HashMap<>();
    CopyOnWriteArrayList<MulticastServerInfo> multicastServers = new CopyOnWriteArrayList<>();
    boolean isBackup; // Is backup server?
    MulticastSocket socket = null;
    final String MULTICAST_ADDRESS = "224.0.224.0";
    boolean isWriting = false;

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
                        System.out.println("Reconnecting to clients, please wait...");
                        if (this.clientInterfacesMap.isEmpty()) {
                            System.out.println("No clients connected.");
                        } else {
                            reconnectClients();
                            System.out.println("Done! Reconnected to " + this.clientInterfacesMap.size() + " clients.");
                        }
                        System.out.println("Backup server is now Primary Server.");
                    } else {
                        new RMIMulticastManager(this);
                        System.out.println("Primary RMIServer ready...");
                    }
                    System.out.println("Print model: \"[Message responsible] Message\"");
                } catch (Exception err) {
                    System.out.println("\nERROR: Something went wrong. Aborting program...");
                    System.exit(-1);
                }
            }
        }
    }

    public void reconnectClients() throws MalformedURLException, RemoteException, NotBoundException {
        if (!clientInterfacesMap.isEmpty()) {
            for (HashMap.Entry<Integer, ClientInterface> entry : clientInterfacesMap.entrySet()) {
                ClientInterface client = entry.getValue();
                client.establishConnection();
            }
        }
    }

    public HashMap<Integer, ClientInterface> getHashMapFromPrimary() throws RemoteException {
        return this.clientInterfacesMap;
    }

    public int getClientNoFromPrimary() throws RemoteException {
        return this.clientNo;
    }

    // For the secondary server to check if primary failed
    public void checkPrimaryServerStatus() throws InterruptedException, AccessException, RemoteException {
        new RMIMulticastManager(this);

        boolean run = true;
        while (run) {
            Thread.sleep(1000);
            try {
                String res = serverInterface.testPrimary();
                System.out.println(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
                        + LocalDateTime.now().getSecond() + " [Primary server] " + res);
                this.clientNo = serverInterface.getClientNoFromPrimary();
                this.clientInterfacesMap = serverInterface.getHashMapFromPrimary();

                // this.multicastServers = serverInterface.getMulticastServersFromPrimary(); //
                // AQUI

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

                String[] parameters = msgReceive.split(";;");
                type = parameters[0].split("\\|\\|\\|")[1];
                receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);

                System.out.println("Type = " + type);

            } while ((!type.contains("Result")) || (receivedClientNo != clientNo));

            // System.out.println("Mensagem final: " + msgReceive);
            socket.close();
            return msgReceive;
            // dar return de msgReceive

        } catch (Exception e) {
            socket.close();
            System.out.println(
                    "ERROR: Something went wrong. Did you forget the flag? Are there any multicast servers? Aborting program...");
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
            msg = "type|||login;;clientNo|||" + clientNo + ";;username|||" + username + ";;password|||" + password;
        else
            msg = "type|||register;;clientNo|||" + clientNo + ";;username|||" + username + ";;password|||" + password;

        System.out.println("Mensgem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String logout(int clientNo, String username, boolean exit) throws RemoteException {
        String msg = "type|||logout;;clientNo|||" + clientNo + ";;username|||" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        if (exit) {
            String[] parameters = msgReceive.split(";;");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
            this.clientInterfacesMap.remove(receivedClientNo);
        }
        return msgReceive;
    }

    public String search(int clientNo, String username, String searchTerms) throws RemoteException {
        String msg;

        if (username != null)
            msg = "type|||search;;clientNo|||" + clientNo + ";;word|||" + searchTerms + ";;username|||" + username;
        else
            msg = "type|||search;;clientNo|||" + clientNo + ";;word|||" + searchTerms;

        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public int getLowestLoadedServer() {
        // To initialize the vars, in case nothing found the first server is taken
        int serverNo = 1;
        MulticastServerInfo ref = multicastServers.get(0);
        // Start at a big number
        int min = 100000;

        for (int i = 0; i < multicastServers.size(); i++) {
            MulticastServerInfo s = multicastServers.get(i);
            if (s.getCarga() < min) {
                serverNo = s.getServerNo();
                min = s.getCarga();
                ref = s;
            }
        }
        ref.incrementCarga();
        return serverNo;
    }

    public String indexNewURL(int clientNo, String url) throws RemoteException {
        int serverNo = getLowestLoadedServer();
        String msg = "type|||index;;clientNo|||" + clientNo + ";;serverNo|||" + serverNo + ";;url|||" + url;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String searchHistory(int clientNo, String username) throws RemoteException {
        String msg = "type|||searchHistory;;clientNo|||" + clientNo + ";;username|||" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String linksPointing(int clientNo, String url) throws RemoteException {
        String msg = "type|||linksPointing;;clientNo|||" + clientNo + ";;url|||" + url;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String grantPrivileges(int clientNo, String username)
            throws RemoteException, MalformedURLException, NotBoundException {
        String msg = "type|||promote;;clientNo|||" + clientNo + ";;username|||" + username;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        String[] parameters = msgReceive.split(";;");
        String status = parameters[2].split("\\|\\|\\|")[1];
        // Erro esta aqui
        if ((parameters.length > 3) && (parameters[3] != null) && (status.equals("valid"))) {
            int newAdminNo = Integer.parseInt(parameters[3].split("\\|\\|\\|")[1]);
            ClientInterface client = clientInterfacesMap.get(newAdminNo);
            client.notification();
        }

        return msgReceive;
    }

    public void sendRtsToAll(String msg) throws MalformedURLException, RemoteException, NotBoundException {
        for (HashMap.Entry<Integer, ClientInterface> entry : clientInterfacesMap.entrySet()) {
            ClientInterface client = entry.getValue();
            // boolean inRts = client.getIsInRealTimeStatistics();
            // if (inRts)
            client.rtsUpdate(msg);
        }
    }

    public String realTimeStatistics(int clientNo) throws RemoteException {
        String msg = "type|||rts;;clientNo|||" + clientNo;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

}