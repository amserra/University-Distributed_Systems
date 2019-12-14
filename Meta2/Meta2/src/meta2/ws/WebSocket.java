package meta2.ws;

import meta2.model.HeyBean;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import rmiserver.ClientInterface;
import rmiserver.ServerInterface;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

@ServerEndpoint(value = "/meta2/ws", configurator = GetHttpSessionConfigurator.class)
public class WebSocket extends UnicastRemoteObject implements ClientInterface {
    // private static final AtomicInteger sequence = new AtomicInteger(1);
    private static final Set<WebSocket> connections = new CopyOnWriteArraySet<>();
    // private final String username;
    private ServerInterface server;
    private Session wsSession;
    private HttpSession httpSession;
    private int clientNo;
    private HeyBean heyBean;

    public WebSocket() throws RemoteException {
        System.out.println("CONSTRUCTOR");
        // username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(Session session, EndpointConfig config) {
        System.out.println("START");
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.heyBean = (HeyBean) this.httpSession.getAttribute("heyBean");
        this.clientNo = heyBean.getClientNo();
        this.server = heyBean.getServer();
        try {
            this.server.sayHelloFromClient(clientNo, this);
        } catch (RemoteException e) {
            System.out.println("Exception in ws start");
        }
        connections.add(this);
        // String message = "*" + username + "* connected.";
        // broadcast(message);
        // sendMessage(message);
    }

    @OnClose
    public void end() {
        System.out.println("END");
        try {
            server.removeClient(this.clientNo);
        } catch (RemoteException e) {
            System.out.println("Error removing client. Ignoring");
        }
        connections.remove(this);
        try {
            this.wsSession.close();
        } catch (IOException e) {
            // Ignore
        }
        // broadcast("* "+username+" disconnected");
        // clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message) {
        System.out.println("RECEIVEMSG:" + message);
        if (message == "inRTS") {
            startRts();
        }
        // one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
        // String upperCaseMessage = message.toUpperCase();
        // sendMessage("[" + username + "] " + upperCaseMessage);
        // broadcast("["+username+"] "+message);
    }

    public String startRts() {
        return null;
    }

    @OnError
    public void handleError(Throwable t) {
        System.out.println("ERROR");
    }

    public static void broadcast(String msg) {
        System.out.println("BROADCAST");
        for (WebSocket client : connections) {
            try {
                synchronized (client) {
                    client.wsSession.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                connections.remove(client);
                try {
                    client.wsSession.close();
                } catch (IOException e1) {
                    // Ignore
                }
                // String message = String.format("*␣%s␣%s", client.username,
                // "has␣been␣disconnected.");
                // broadcast(message);
            }
        }
    }

    @Override
    public boolean userMatchesPassword(String user, String password) throws RemoteException {
        return false;
    }

    @Override
    public ArrayList<String> getAllUsers() throws RemoteException {
        return null;
    }

    @Override
    public String sayHelloFromBackup() throws RemoteException {
        return null;
    }

    @Override
    public String sayHelloFromClient(ClientInterface client) throws RemoteException {
        return null;
    }

    @Override
    public String testPrimary() throws RemoteException {
        return null;
    }

    @Override
    public HashMap<Integer, ClientInterface> getHashMapFromPrimary() throws RemoteException {
        return null;
    }

    @Override
    public int getClientNoFromPrimary() throws RemoteException {
        return 0;
    }

    @Override
    public String authentication(int clientNo, boolean isLogin, String username, String password)
            throws RemoteException {
        return null;
    }

    @Override
    public String getAuthorizationUrl(String secretState) throws RemoteException {
        return null;
    }

    @Override
    public JSONObject exchangeCodeForToken(String code, int clientNo, String username)
            throws InterruptedException, ExecutionException, IOException, ParseException {
        return null;
    }

    @Override
    public String logout(int clientNo, String username, boolean exit) throws RemoteException {
        return null;
    }

    @Override
    public String search(int clientNo, String username, String searchTerms) throws RemoteException {
        return null;
    }

    @Override
    public String translateText(String text) throws IOException, ParseException {
        return null;
    }

    @Override
    public String searchHistory(int clientNo, String username) throws RemoteException {
        return null;
    }

    @Override
    public String linksPointing(int clientNo, String url) throws RemoteException {
        return null;
    }

    @Override
    public String indexNewURL(int clientNo, String url) throws RemoteException {
        return null;
    }

    @Override
    public String realTimeStatistics(int clientNo) throws RemoteException {
        return null;
    }

    @Override
    public String grantPrivileges(int clientNo, String username)
            throws RemoteException, NotBoundException, MalformedURLException {
        return null;
    }

    @Override
    public void notification() throws RemoteException, NotBoundException, MalformedURLException {

        /*
         * if (this.heyBean.getTypeOfClient().equals("user")) {
         * System.out.print("\n\nNotification: Promoted to admin! Sending to ui... ");
         * try {
         * this.wsSession.getBasicRemote().sendText("You have been promoted to admin!");
         * this.heyBean.setTypeOfClient("admin");
         * this.httpSession.setAttribute("typeOfClient","admin"); } catch (IOException
         * e) { server.removeClient(this.clientNo); connections.remove(this); try {
         * this.wsSession.close(); } catch ( JMSException e1) { // Ignore } } }
         */
    }

}
