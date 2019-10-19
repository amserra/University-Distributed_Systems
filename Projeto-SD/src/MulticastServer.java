import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;



public class MulticastServer extends Thread {

    //Thread para fazer a recusrsividade e adicionar as palavras
    //Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo etc)
    //Thread que peridodicamente comunica com os outros servidores multicast e sincronizam as informacoes
    //Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;


    private ArrayList<User> listUsers = new ArrayList<User>();
    private ArrayList<URL> urlLinksCount = new ArrayList<>();

    private HashMap<String,HashSet<String>> index = new HashMap<>();

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
           // socket = new MulticastSocket();  // create socket without binding it (only for sending)
            byte[] buf = new byte[256];
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                String[] receivedSplit = received.split(";");
                String[] type = receivedSplit[0].split("\\|");
                String messageType = type[1];
                if(messageType.equals("register")){
                    User newUser;
                    boolean checkUser = false;

                    String username = receivedSplit[1].split("\\|")[1];
                    String password = receivedSplit[2].split("\\|")[1];


                    for(User u: listUsers){
                        if(u.getUsername().equals(username))
                            checkUser = true;
                    }

                    if(!checkUser){
                        //Primeiro Utilizador, logo é admin
                        if(listUsers.isEmpty())
                            newUser = new User(username,password,true);
                        //Outro utilizador logo é um utilizador
                        else
                            newUser = new User(username,password,false);

                        listUsers.add(newUser);
                        String message = "type|registerComplete;username|" + newUser.getUsername();
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }else{
                        String message = "type|invalidRegister;username|" + username;
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }
                    
                }
                else if(messageType.equals("login")){

                    boolean checkUser = false;

                    String username = receivedSplit[1].split("\\|")[1];
                    String password = receivedSplit[2].split("\\|")[1];


                    for(User u: listUsers){
                        if(u.getUsername().equals(username) && u.getPassword().equals(password))
                            checkUser = true;
                    }

                    if(checkUser){
                        String message = "type|validLogin;username|" + username;
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }
                    else{
                        String message = "type|invalidLogin";
                        byte[] buffer = message.getBytes();
                        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packetSent);
                    }

                }

                else if(messageType.equals("index")){
                    String word = receivedSplit[1].split("\\|")[1];
                    String url = receivedSplit[2].split("\\|")[1];

                    WebCrawler getUrls = new WebCrawler(this,word,url);
                    getUrls.start();
                }

                else if(messageType.equals("search")){
                    String word = receivedSplit[1].split("\\|")[1];

                    try{
                        String username = receivedSplit[2].split("\\|")[1];

                        User user = null;

                        for(User u: listUsers)
                            if(u.equals(username)){
                                user = u;
                                break;
                            }

                        if(user != null)
                            user.getSearches().add(0,word);
                        
                    } catch (Exception e){
                       
                    }

                    HashSet<String> urlResults = index.get(word);

                    String message = "type|searchResult;urlCount|";              

                    if(urlResults != null){
                        message += urlResults.size();

                        int urlCount = 0;

                        Collections.sort(urlLinksCount);

                        for(URL url: urlLinksCount){
                            if(urlResults.contains(url.getUrl()))
                                message += ";url_" + urlCount++ + "|" + url.getUrl();
                        }
                    }
                    else
                        message += 0;
                        
                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                } else if(messageType.equals("searchList")){

                    String username = receivedSplit[1].split("\\|")[1];

                    User user = null;

                    for(User u: listUsers)
                        if(u.equals(username)){
                            user = u;
                            break;
                        }

                    ArrayList<String> searchList = user.getSearches();

                    String message = "type|searchListResults;searchCount|" + searchList.size();

                    int searchCount = 0;

                    for(String s: searchList){
                        message += ";search_" + searchCount + "|" + s;
                        searchCount++;
                    }

                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
            }
                
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

    public ArrayList<URL> getUrlLinksCount() {
        return urlLinksCount;
    }

    public void setUrlLinksCount(ArrayList<URL> urlLinksCount) {
        this.urlLinksCount = urlLinksCount;
    }
}
