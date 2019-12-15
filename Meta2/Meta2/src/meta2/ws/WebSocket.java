package meta2.ws;

import meta2.model.HeyBean;
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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/meta2/ws", configurator = GetHttpSessionConfigurator.class)
public class WebSocket extends UnicastRemoteObject implements ClientInterface {
    private static final Set<WebSocket> connections = new CopyOnWriteArraySet<>();
    private ServerInterface server;
    private Session wsSession;
    private HttpSession httpSession;
    private int clientNo;
    private HeyBean heyBean;
    private boolean inRTS = false;

    public WebSocket() throws RemoteException {
        System.out.println("CONSTRUCTOR");
    }

    @OnOpen
    public void start(Session session, EndpointConfig config) {
        System.out.println("START begin");
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
        System.out.println("START end");
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
    }

    @OnMessage
    public void receiveMessage(String message) {
        System.out.println("RECEIVEMSG:" + message);
        if (message.equals("inRTS")) {
            System.out.println("In rts");
            this.inRTS = true;
        } else {
            System.out.println("false");
        }
    }

    @OnError
    public void handleError(Throwable t) {
        System.out.println("ERROR");
    }

    @Override
    public void notification() {
        if (this.heyBean.getTypeOfClient().equals("user")) {
            System.out.print("\n\nNotification: Promoted to admin! Sending to ui... ");
            try {
                this.wsSession.getBasicRemote().sendText("You have been promoted to admin!");
                this.heyBean.setTypeOfClient("admin");
                this.httpSession.setAttribute("typeOfClient","admin");
            } catch (IOException e) {
                try {
                    server.removeClient(this.clientNo);
                    connections.remove(this);
                } catch (RemoteException e1) {
                    try {
                        this.wsSession.close();
                    } catch (IOException e2) {
                        // Ignore
                    }
                }
            }
        }
    }

    @Override
    public void rtsUpdate(String msg) throws MalformedURLException, RemoteException, NotBoundException {
        System.out.println("RECEIVED RTS UPDATE IN WEBSOCKET");
        try {
            this.wsSession.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            System.out.println("Couldn't send rts update...");
        }
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
    public String searchHistory(int clientNo, String username) throws RemoteException {
        return null;
    }

    @Override
    public String linksPointing(int clientNo, String url) throws RemoteException {
        return null;
    }
}
