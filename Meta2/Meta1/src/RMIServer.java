import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rmiserver.ClientInterface;
import rmiserver.ServerInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Class that represents a RMIServer (both Primary and Backup)
 */
public class RMIServer extends UnicastRemoteObject implements ServerInterface {
    static final long serialVersionUID = 1L;
    int clientNo = 1; // Id for RMIServer to indentify RMIClients
    ServerInterface serverInterface; // For backup server to have the reference
    HashMap<Integer, ClientInterface> clientInterfacesMap = new HashMap<>(); // Map between clientNo and rmi reference
    // All the multicast servers
    CopyOnWriteArrayList<MulticastServerInfo> multicastServers = new CopyOnWriteArrayList<>();
    boolean isBackup; // Is backup server?
    MulticastSocket socket = null;
    final String MULTICAST_ADDRESS = "224.0.224.0";
    boolean isWriting = false;

    final int PORT = 4369;
    int RMIPORT;
    String RMINAME;

    //Variables for APIs access

    //Facebook
    final String clientId = "1517623451722247"; //API key
    final String clientSecret = "6e77bf3115c0707179b8ec299ee6d7e4"; //API secret
    final OAuth20Service service = new ServiceBuilder(clientId)  //Service for Facebook Login
            .apiSecret(clientSecret)
            .callback("https://localhost:8443/Meta2/exchangeTokenForCode.jsp")
            .build(FacebookApi.instance());
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v3.2/me";

    /**
     * Main method that creates a RMIServer object
     * 
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        System.setProperty("java.net.preferIPv4Stack", "true");


        if (args.length == 0) {
            System.out.println(
                    "To start with custom IP and PORT enter as arguments.\nStarting with default IP and PORT.");
            new RMIServer("RMIConnection", 2857);
        } else if (args.length == 2) {
            System.setProperty("java.rmi.server.hostname", args[0]);
            new RMIServer(args[0], Integer.parseInt(args[1]));
        }
    }

    /**
     * Constructor of the RMIServer class. Starts by trying to establish/create a
     * connection.
     * 
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    RMIServer(String rmiName, int port) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        RMIPORT = port;
        RMINAME = rmiName;
        connectToRMIServer();
    }

    // rmiserver.ServerInterface methods (documentation on rmiserver.ServerInterface class)

    public String sayHelloFromBackup() throws RemoteException {
        System.out.println("[Backup server] Has just connected.");
        return "Connected to RMI Primary Server successfully!";
    }

    public String sayHelloFromClient() throws RemoteException {
        System.out.println("[Client no " + clientNo + "] " + "Has just connected.");
        String msg = "Connected to RMI Primary Server successfully!\nServer gave me the id no " + clientNo;
        clientNo++;
        return msg;
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

    public HashMap<Integer, ClientInterface> getHashMapFromPrimary() throws RemoteException {
        return this.clientInterfacesMap;
    }

    public int getClientNoFromPrimary() throws RemoteException {
        return this.clientNo;
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

    public String logout(int clientNo, String username) throws RemoteException {
        if (username != null) {
            String msg = "type|||logout;;clientNo|||" + clientNo + ";;username|||" + username;
            System.out.println("Mensagem a ser enviada: " + msg);
            String msgReceive = connectToMulticast(clientNo, msg);
            System.out.println("Mensagem recebida: " + msgReceive);
            return msgReceive;
        }
        // Else
        return "ERROR LOGOUT";
    }

    public String logout(int clientNo, String username, boolean exit) throws RemoteException {
        if (username != null) {
            String msg = "type|||logout;;clientNo|||" + clientNo + ";;username|||" + username;
            System.out.println("Mensagem a ser enviada: " + msg);
            String msgReceive = connectToMulticast(clientNo, msg);
            System.out.println("Mensagem recebida: " + msgReceive);
            if (exit && msgReceive != null) {
                String[] parameters = msgReceive.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                this.clientInterfacesMap.remove(receivedClientNo);
            }
            return msgReceive;
        } else {
            if (exit)
                this.clientInterfacesMap.remove(clientNo);
            return null;
        }
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

    public String indexNewURL(int clientNo, String url) throws RemoteException {
        int serverNo = getLowestLoadedServer();
        String msg = "type|||index;;clientNo|||" + clientNo + ";;serverNo|||" + serverNo + ";;url|||" + url;
        System.out.println("Mensagem a ser enviada: " + msg);
        String msgReceive = connectToMulticast(clientNo, msg);
        System.out.println("Mensagem recebida: " + msgReceive);
        return msgReceive;
    }

    public String realTimeStatistics(int clientNo) throws RemoteException {
        String msg = "type|||rts;;clientNo|||" + clientNo;
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

        if (msgReceive != null) {
            String[] parameters = msgReceive.split(";;");
            String status = parameters[2].split("\\|\\|\\|")[1];

            if ((parameters.length > 3) && (parameters[3] != null) && (status.equals("valid"))) {
                int newAdminNo = Integer.parseInt(parameters[3].split("\\|\\|\\|")[1]);
                ClientInterface client = clientInterfacesMap.get(newAdminNo);
                client.notification();
            }
        }

        return msgReceive;
    }

    public String getAuthorizationUrl(String secretState) {
        return service.getAuthorizationUrl(secretState);
    }

    public JSONObject exchangeCodeForToken(String code, int clientNo) throws InterruptedException, ExecutionException, IOException, ParseException {
        JSONObject json = null;

        //Obtain Access Token
        System.out.println("Trading the Authorization Code for an Access Token...");
        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token!");

        //Get ID and Name of the user that logged in via Facebook
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);

        if(response.getCode() == 200){
            System.out.println("Retrieved information successfully");

            System.out.println(response.getBody());

            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(response.getBody());

            String msgReceived = authentication(clientNo,true, (String) json.get("id"), null );

            System.out.println(msgReceived);

            json.put("msg", msgReceived);
        }



        return json;

    }

    // End of rmiserver.ServerInterface methods

    /**
     * Tries to connect the BackupRMIServer. In case it fails, creates a new
     * connection(becomes the PrimaryRMIServer)
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void connectToRMIServer() throws AccessException, RemoteException, MalformedURLException, NotBoundException {
        // Only used to log on the console
        boolean wasBackup = false;
        try {
            // Tries to be BackupRMIServer
            serverInterface = (ServerInterface) Naming.lookup(RMINAME);
            String msg = serverInterface.sayHelloFromBackup();
            System.out.println(msg);
            wasBackup = true;
            this.isBackup = true;
            checkPrimaryServerStatus();
        } catch (Exception e) { // Usually java.rmi.ConnectException
            System.out.println("No primary RMIServer yet. Connecting...");
        } finally {
            // If it can't be Backup, try to become Primary
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
                            // So that clients know the new PrimaryRMIServer
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

    /**
     * Reconnects all the RMIClients to the new PrimaryRMIServer. This happens when
     * a Primary fails and the Backup has to take its place
     * 
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void reconnectClients() throws MalformedURLException, RemoteException, NotBoundException {
        if (!clientInterfacesMap.isEmpty()) {
            for (HashMap.Entry<Integer, ClientInterface> entry : clientInterfacesMap.entrySet()) {
                ClientInterface client = entry.getValue();
                client.establishConnection();
            }
        }
    }

    /**
     * Method where RMIBackupServer lays. Tests the RMIPrimaryServer, as well as
     * gets the necessary data so that when the Primary fails, it has everything
     * needed. Exception is thrown when primary server stops responding.
     * 
     * @throws InterruptedException
     * @throws AccessException
     * @throws RemoteException
     */
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

    /**
     * Method used by almost all functionalities to send a multicast message to all
     * the MulticastServers. In case there's no response within 30s, returns a null
     * message.
     * 
     * @param clientNo
     * @param msg
     * @return
     * @throws RemoteException
     */
    public String connectToMulticast(int clientNo, String msg) throws RemoteException {
        try {
            // Send
            socket = new MulticastSocket(PORT); // create socket and bind it
            socket.setSoTimeout(30000);
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

                String[] parameters = msgReceive.split(";;");
                type = parameters[0].split("\\|\\|\\|")[1];
                receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);

                System.out.println("Type = " + type);

            } while ((!type.contains("Result")) || (receivedClientNo != clientNo));

            socket.close();
            return msgReceive;

        } catch (SocketTimeoutException er) {
            System.out.println("Timeout! Did not recieve answer. Are there any multicast servers?");
        } catch (Exception e) {
            socket.close();
            System.out.println(
                    "ERROR: Something went wrong. Did you forget the flag? Are there any multicast servers? Aborting program...");
            System.exit(-1);
        }
        return null;
    }

    /**
     * Method used to send a rts message to all the clients. Only the ones that are
     * in the rts page will see it.
     * 
     * @param msg
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void sendRtsToAll(String msg) throws MalformedURLException, RemoteException, NotBoundException {
        for (HashMap.Entry<Integer, ClientInterface> entry : clientInterfacesMap.entrySet()) {
            ClientInterface client = entry.getValue();
            client.rtsUpdate(msg);
        }
    }

    /**
     * Method that sends a live notification to an user.
     * 
     * @param clientNo
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void notification(int clientNo) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            ClientInterface client = clientInterfacesMap.get(clientNo);
            client.notification();
        } catch (RemoteException e) {
            System.out.println("ERROR #7: Something went wrong.");
        }
    }

    /**
     * Auxiliar method that gets the lowest loaded server available, in order to
     * distribute the ammount of work.
     * 
     * @return
     */
    public int getLowestLoadedServer() {
        // To initialize the vars, in case nothing found the first server is taken
        int serverNo = 1;
        MulticastServerInfo ref = multicastServers.get(0);
        // Start at a big number
        int min = 100000;

        for (int i = 0; i < multicastServers.size(); i++) {
            MulticastServerInfo s = multicastServers.get(i);
            if (s.getLoad() < min) {
                serverNo = s.getServerNo();
                min = s.getLoad();
                ref = s;
            }
        }
        ref.incrementLoad();
        return serverNo;
    }
}