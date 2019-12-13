package meta2.ws;

import meta2.model.HeyBean;
import rmiserver.ServerInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/meta2/ws", configurator = GetHttpSessionConfigurator.class)
public class WebSocket {
    // private static final AtomicInteger sequence = new AtomicInteger(1);
    private static final Set<WebSocket> connections = new CopyOnWriteArraySet<>();
    // private final String username;
    private ServerInterface server;
    private Session wsSession;
    private HttpSession httpSession;
    private int clientNo;

    public WebSocket() {
        System.out.println("CONSTRUCTOR");
        // username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(Session session, EndpointConfig config) {
        System.out.println("START");
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        HeyBean heyBean = (HeyBean) this.httpSession.getAttribute("heyBean");
        this.clientNo = heyBean.getClientNo();
        this.server = heyBean.getServer();
        System.out.println("Server:" + this.server);
        try {
            System.out.println(this.clientNo);
            this.server.sayHelloFromWebSocket(this.clientNo, this.wsSession);
        } catch (RemoteException e) {
            System.out.println("Exception in ws start");
            e.printStackTrace();
        }

        // startRts();

        connections.add(this);
        // String message = "*" + username + "* connected.";
        // broadcast(message);
        // sendMessage(message);
    }

    public void startRts() {
        try {
            String msg = server.realTimeStatistics(this.clientNo);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                System.out.println("Recieved client no: " + receivedClientNo);
                if (this.clientNo == receivedClientNo) {
                    System.out.println("Started receiving updates...");
                    // printTop10(parameters);

                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                // this.inRealTimeStatistics = false;
                // userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #9: Something went wrong. Returning to main menu");
            // this.inRealTimeStatistics = false;
            // userUI.mainMenu();
        }
    }

    @OnClose
    public void end() {
        connections.remove(this);
        try {
            this.wsSession.close();
        } catch (IOException e) {
            // Ignore
        }
        System.out.println("END");
        // broadcast("* "+username+" disconnected");
        // clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message) {
        System.out.println("RECEIVEMSG");
        // one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
        String upperCaseMessage = message.toUpperCase();
        // sendMessage("[" + username + "] " + upperCaseMessage);
        // broadcast("["+username+"] "+message);
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
}
