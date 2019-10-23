import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServerAction extends Thread {

    String received;
    MulticastSocket socket;
    InetAddress group;

    MulticastServer server;
    String MULTICAST_ADDRESS;
    int PORT;
    ArrayList<User> listUsers;
    CopyOnWriteArrayList<URL> urlList;

    HashMap<String, HashSet<String>> index;

    public MulticastServerAction(String received, MulticastSocket socket, InetAddress group, MulticastServer server) {
        this.received = received;
        this.socket = socket;
        this.group = group;
        this.server = server;
        this.MULTICAST_ADDRESS = server.getMULTICAST_ADDRESS();
        this.PORT = server.getPORT();
        this.listUsers = server.getListUsers();
        this.urlList = server.getUrlList();
        this.index = server.getIndex();
    }

    public void run() {
        try {
            System.out.println("RECEIVED: " + received);

            String[] receivedSplit = received.split(";");
            String[] type = receivedSplit[0].split("\\|");
            String messageType = type[1];

            String message = "";

            if (messageType.equals("register")) {
                User newUser;
                boolean checkUser = false;

                String clientNo = receivedSplit[1].split("\\|")[1];
                String username = receivedSplit[2].split("\\|")[1];
                String password = receivedSplit[3].split("\\|")[1];

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
                    message = "type|registerResult;clientNo|" + clientNo + ";status|valid;username|"
                            + newUser.getUsername() + ";isAdmin|" + newUser.isAdmin();
                } else {
                    message = "type|registerResult;clientNo|" + clientNo + ";status|invalid";
                }

            } else if (messageType.equals("login")) {

                boolean checkUser = false;

                User user = null;

                String clientNo = receivedSplit[1].split("\\|")[1];
                String username = receivedSplit[2].split("\\|")[1];
                String password = receivedSplit[3].split("\\|")[1];

                for (User u : listUsers) {
                    if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                        checkUser = true;
                        user = u;
                        break;
                    }
                }

                if (checkUser) {
                    message = "type|loginResult;clientNo|" + clientNo + ";status|valid;username|" + user.getUsername() + ";isAdmin|" + user.isAdmin();
                } else {
                    message = "type|loginResult;clientNo|" + clientNo + ";status|invalid";
                }

            }

            else if (messageType.equals("index")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String url = receivedSplit[2].split("\\|")[1];

                WebCrawler getUrls = new WebCrawler(server, url);
                getUrls.start();

                message = "type|indexResult;clientNo|" + clientNo + ";status|started";
            }

            else if (messageType.equals("search")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String words = receivedSplit[2].split("\\|")[1];

                try {
                    String username = receivedSplit[3].split("\\|")[1];

                    User user = null;

                    for (User u : listUsers)
                        if (u.equals(username)) {
                            user = u;
                            break;
                        }

                    if (user != null)
                        user.getSearchHistory().add(0, words);

                } catch (Exception e) {

                }

                HashSet<String> urlResults;

                try{
                    String[] wordList = words.split(" "); 

                    System.out.println(words);

                    System.out.println(wordList[0]);

                    HashSet<String> wordUrlResults_1 = index.get(wordList[0]);

                    urlResults = new HashSet<>(wordUrlResults_1);

                    for(int i = 1; i < wordList.length; i++){
                        HashSet<String> wordUrlResults_2 = index.get(wordList[i]);
                        for(String s: urlResults)
                            if(!wordUrlResults_2.contains(s))
                                urlResults.remove(s);
                    }

                } catch(NullPointerException e){
                    e.printStackTrace();

                    urlResults = null;
                } catch (Exception e){
                    e.printStackTrace();

                    urlResults = index.get(words);

                }



                message = "type|searchResult;clientNo|" + clientNo + ";urlCount|";

                if (urlResults != null) {
                    message += urlResults.size();

                    int urlCount = 0;

                    for(URL url: urlList){
                        System.out.println(url.getTitle() + ": " + url.getLinksCount());
                    }

                    Collections.sort(urlList);

                    for (URL url : urlList) {
                        if (urlResults.contains(url.getUrl()))
                            message += ";url_" + urlCount++ + "|" + url.getUrl();
                    }
                } else
                    message += 0;

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
                            if (urlPointingList == null) {
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

            } else if (messageType.equals("promote")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String username = receivedSplit[2].split("\\|")[1];

                User tempUser = new User(username);

                if (listUsers.contains(tempUser)) {
                    int indexOfUser = listUsers.indexOf(tempUser);
                    User user = listUsers.get(indexOfUser);
                    if (user.isAdmin())
                        message = "type|promoteResult;clientNo|" + clientNo
                                + ";status|invalid;message|User is already admin";
                    else {
                        message = "type|promoteResult;clientNo|" + clientNo + ";status|valid";
                        user.setAdmin(true);
                    }
                } else {
                    message = "type|promoteResult;clientNo|" + clientNo
                            + ";status|invalid;message|That user doesn't exist";
                }
            } else if(messageType.equals("checkStatusConfirm")){

                if(server.isCheckingMulticastServers()){

                    int serverNo = Integer.parseInt(receivedSplit[1].split("\\|")[1]);

                    server.getMulticastServerCheckedList().add(serverNo);
                }

                for(Integer i: server.getMulticastServerCheckedList())
                    System.out.println("SERVER CHECKED: " + i);
                

            } else if(messageType.equals("checkStatus")){
                message = "type|checkStatusConfirm;serverNo|" + server.getMulticastServerNo();
            } else if(messageType.equals("multicastServerNo")){
                //Atualizar o array dos multicast servers que estão up

                int multicastServerCount = Integer.parseInt(receivedSplit[2].split("\\|")[1]);

                ArrayList<Integer> newMulticastServerNoList = new ArrayList<>();

                for(int i = 0; i < multicastServerCount; i++){
                    newMulticastServerNoList.add(Integer.parseInt(receivedSplit[3 + i].split("\\|")[1]));
                }

                server.setMulticastServerNoList(newMulticastServerNoList);

                for(Integer i: server.getMulticastServerNoList())
                    System.out.println("SERVER: " + i);

            }

            if(!message.equals("")){
                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}