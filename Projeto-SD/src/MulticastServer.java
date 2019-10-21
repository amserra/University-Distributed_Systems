import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
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
            while (true) {
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                System.out.println(received);

                String[] receivedSplit = received.split(";");
                String[] type = receivedSplit[0].split("\\|");
                String messageType = type[1];

                String message = "";

                if (messageType.equals("register")) {
                    User newUser;
                    boolean checkUser = false;

                    String clientNo = receivedSplit[1].split("\\|")[1];
                    System.out.println(clientNo);
                    String username = receivedSplit[2].split("\\|")[1];
                    System.out.println(username);
                    String password = receivedSplit[3].split("\\|")[1];
                    System.out.println(password);

                    for (User u : listUsers) {
                        if (u.getUsername().equals(username))
                            checkUser = true;
                    }

                    if (!checkUser) {
                        // Primeiro Utilizador, logo é admin
                        if (listUsers.isEmpty())
                            newUser = new User(username, password, true);
                        // Outro utilizador logo é um utilizador
                        else
                            newUser = new User(username, password, false);

                        listUsers.add(newUser);
                        message = "type|registerResult;clientNo|" + clientNo + ";status|valid;username|" + newUser.getUsername();
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    } else {
                        message = "type|registerResult;clientNo|" + clientNo + ";status|invalid";
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }

                } else if (messageType.equals("login")) {

                    boolean checkUser = false;

                    User user = null;

                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String username = receivedSplit[2].split("\\|")[1];
                    String password = receivedSplit[3].split("\\|")[1];

                    for (User u : listUsers) {
                        if (u.getUsername().equals(username) && u.getPassword().equals(password)){
                            checkUser = true;
                            user = u;
                            break;
                        }
                    }

                    if (checkUser) {
                        message = "type|loginResult;clientNo|" + clientNo + ";status|valid;username|" + user.getUsername() + ";isAdmin|" + user.isAdmin();
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    } else {
                        message = "type|loginResult;clientNo|" + clientNo + ";status|invalid";
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }

                }

                else if (messageType.equals("index")) {
                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String word = receivedSplit[2].split("\\|")[1];
                    String url = receivedSplit[3].split("\\|")[1];

                    WebCrawler getUrls = new WebCrawler(this, word, url);
                    getUrls.start();

                    message = "type|indexResult;clientNo|" + clientNo + ";status|started";
                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                }

                else if (messageType.equals("search")) {
                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String word = receivedSplit[2].split("\\|")[1];

                    try {
                        String username = receivedSplit[2].split("\\|")[1];

                        User user = null;

                        for (User u : listUsers)
                            if (u.equals(username)) {
                                user = u;
                                break;
                            }

                        if (user != null)
                            user.getSearchHistory().add(0, word);

                    } catch (Exception e) {

                    }

                    HashSet<String> urlResults = index.get(word);

                    message = "type|searchResult;clientNo|" + clientNo + ";urlCount|";

                    if (urlResults != null) {
                        message += urlResults.size();

                        int urlCount = 0;

                        Collections.sort(urlList);

                        for (URL url : urlList) {
                            if (urlResults.contains(url.getUrl()))
                                message += ";url_" + urlCount++ + "|" + url.getUrl();
                        }
                    } else
                        message += 0;

                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                } else if (messageType.equals("searchHistory")) {

                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String username = receivedSplit[2].split("\\|")[1];

                    User user = null;

                    for (User u : listUsers)
                        if (u.equals(username)) {
                            user = u;
                            break;
                        }

                    ArrayList<String> searchList = user.getSearchHistory();

                    message = "type|searchHistoryResult;clientNo|" + clientNo + ";searchCount|" + searchList.size();

                    int searchCount = 0;

                    for (String s : searchList) {
                        message += ";search_" + searchCount + "|" + s;
                        searchCount++;
                    }

                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                } else if (messageType.equals("linksPointing")) {
                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String url = receivedSplit[2].split("\\|")[1];

                    message = "type|linksPointingResult;clientNo|" + clientNo + ";linkCount|";
                    String saveMessage = message;

                    URL urlObject = new URL(url);

                    if (urlList.contains(urlObject)) {
                        for (URL u : urlList) {
                            if (u.equals(url)) {
                                ArrayList<String> urlPointingList = u.getUrlPointingToMeList();
                                if(urlPointingList == null){
                                    message += "0";
                                    break;
                                }
                                message += urlPointingList.size();
                                int linkCount = 0;
                                for (String s : urlPointingList) {
                                    message += ";link_" + linkCount + "|" + s;
                                    linkCount++;
                                }
                                break;
                            }
                        }
                    }

                    if (message.equals(saveMessage))
                        message += "0";

                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                } else if(messageType.equals("promote")){
                    String clientNo = receivedSplit[1].split("\\|")[1];
                    String username = receivedSplit[2].split("\\|")[1];

                    User tempUser = new User(username);

                    if(listUsers.contains(tempUser)){
                        int indexOfUser = listUsers.indexOf(tempUser);
                        User user = listUsers.get(indexOfUser);
                        if(user.isAdmin())
                            message = "type|promoteResult;clientNo|" + clientNo + ";status|invalid;message|User is already admin";
                        else{
                            message = "type|promoteResult;clientNo|" + clientNo + ";status|valid";
                            user.setAdmin(true);
                        }
                    } else{
                        message = "type|promoteResult;clientNo|" + clientNo + ";status|invalid;message|That user doesn't exist";
                    }
                }

                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);

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



}
