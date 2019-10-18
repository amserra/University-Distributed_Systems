import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class MulticastServer extends Thread {

    //Thread para fazer a recusrsividade e adicionar as palavras
    //Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo etc)
    //Thread que peridodicamente comunica com os outros servidores multicast e sincronizam as informacoes
    //Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private int HashSetInitialCapacity = 10000;
    private float HashSetLoadFactor = 0.75f;

    private ArrayList<User> listUsers = new ArrayList<User>();
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
        long counter = 0;
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
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    

    private void recursiveUrlIndex(String url){
        try { 
            HashSet<String> indexURLs;
            Document doc = Jsoup.connect(url).get(); 
            System.out.println("URL: " + url);
            System.out.println("Title: " + doc.title());
            StringTokenizer tokens = new StringTokenizer(doc.text()); 
            String currentToken = tokens.nextToken();
            while (tokens.hasMoreElements()) {
                indexURLs = index.get(currentToken.toLowerCase());

                if(indexURLs == null){
                    indexURLs = new HashSet<String>(HashSetInitialCapacity,HashSetLoadFactor);
                    index.put(currentToken.toLowerCase(), indexURLs);
                }

                indexURLs.add(url);

                //System.out.println("Word: " + currentToken.toLowerCase() + " URL: " + index.get(currentToken.toLowerCase()));

                currentToken = tokens.nextToken();
            }
            Elements links = doc.select("a[href]"); 
            for (Element link : links){ 
                System.out.println(url != link.attr("abs:href"));
                if(url != link.attr("abs:href"))
                    recursiveUrlIndex(link.attr("abs:href"));
                //System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n"); 
                //System.out.println("Word: " + word.toLowerCase() + " URLs: " + index.get(word.toLowerCase()));
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    public HashMap<String, HashSet<String>> getIndex() {
        return index;
    }

    public void setIndex(HashMap<String, HashSet<String>> index) {
        this.index = index;
    }
}
