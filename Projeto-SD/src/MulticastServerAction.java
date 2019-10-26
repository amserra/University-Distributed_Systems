import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class MulticastServerAction extends Thread {

    String received;
    MulticastSocket socket;
    InetAddress group;

    MulticastServer server;
    String MULTICAST_ADDRESS;
    int PORT;
    CopyOnWriteArrayList<User> listUsers;
    CopyOnWriteArrayList<URL> urlList;

    ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index;

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
                        newUser = new User(username, password, true, true, Integer.parseInt(clientNo), false);
                    // Outro utilizador logo é um utilizador
                    else
                        newUser = new User(username, password, false, true, Integer.parseInt(clientNo), false);

                    listUsers.add(newUser);
                    message = "type|registerResult;clientNo|" + clientNo + ";status|valid;username|"
                            + newUser.getUsername() + ";isAdmin|" + newUser.isAdmin();

                    saveUsers();

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
                    user.setLoggedIn(true);
                    user.setClientNo(Integer.parseInt(clientNo));
                    message = "type|loginResult;clientNo|" + clientNo + ";status|valid;username|" + user.getUsername()
                            + ";isAdmin|" + user.isAdmin() + ";notification|" + user.isNotification();
                    user.setNotification(false);

                    saveUsers();

                } else {
                    message = "type|loginResult;clientNo|" + clientNo + ";status|invalid";
                }

            }

            else if (messageType.equals("index")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String serverNo = receivedSplit[2].split("\\|")[1];
                String url = receivedSplit[3].split("\\|")[1];

                if (Integer.parseInt(serverNo) == server.getMulticastServerNo()) {

                    WebCrawler getUrls = new WebCrawler(server, url);
                    getUrls.start();

                    message = "type|indexResult;clientNo|" + clientNo + ";status|started";
                }
            }

            else if (messageType.equals("search")) {

                // System.out.println(index);

                String clientNo = receivedSplit[1].split("\\|")[1];
                String words = receivedSplit[2].split("\\|")[1].toLowerCase();

                CopyOnWriteArrayList<Search> searchList = server.getSearchList();

                if (searchList.contains(words)) {

                    int indexWords = searchList.indexOf(words);
                    Search search = searchList.get(indexWords);
                    search.setnSearches((search.getnSearches()) + 1);
                } else {
                    searchList.add(new Search(words, 1));
                }

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

                    saveUsers();

                } catch (Exception e) {

                }

                CopyOnWriteArraySet<String> urlResults;

                try {
                    String[] wordList = words.split(" ");

                    CopyOnWriteArraySet<String> wordUrlResults_1 = index.get(wordList[0]);

                    urlResults = new CopyOnWriteArraySet<>(wordUrlResults_1);

                    for (int i = 1; i < wordList.length; i++) {
                        CopyOnWriteArraySet<String> wordUrlResults_2 = index.get(wordList[i]);
                        for (String s : wordUrlResults_1)
                            if (!wordUrlResults_2.contains(s))
                                urlResults.remove(s);
                    }

                } catch (NullPointerException e) {
                    System.out.println("Word not indexed");

                    urlResults = null;
                } catch (Exception e) {
                    e.printStackTrace();

                    urlResults = index.get(words);

                }

                message = "type|searchResult;clientNo|" + clientNo + ";urlCount|";

                if (urlResults != null) {
                    message += urlResults.size();

                    int urlCount = 0;

                    for (URL url : urlList) {
                        System.out.println(url.getUrl() + "   : " + url.getLinksCount());
                    }

                    try {
                        Collections.sort(urlList);
                    } catch (Exception e) {

                    }

                    HashSet<String> check = new HashSet<>();

                    for (URL url : urlList) {
                        if (urlResults.contains(url.getUrl()) && !check.contains(url.getUrl())) {
                            check.add(url.getUrl());
                            message += ";url_" + urlCount++ + "|" + url.getUrl();
                        }
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
                    System.out.println(s);
                    message += ";search_" + searchCount + "|" + s;
                    searchCount++;
                }

            } else if (messageType.equals("linksPointing")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String url = receivedSplit[2].split("\\|")[1];

                message = "type|linksPointingResult;clientNo|" + clientNo + ";linkCount|";
                String saveMessage = message;

                if (urlList.contains(url)) {
                    for (URL u : urlList) {
                        if (u.equals(url)) {
                            CopyOnWriteArraySet<String> urlPointingList = u.getUrlPointingToMeList();
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
                        if (user.isLoggedIn())
                            message += ";newAdminNo|" + user.getClientNo();
                        else
                            user.setNotification(true);

                        saveUsers();

                    }
                } else {
                    message = "type|promoteResult;clientNo|" + clientNo
                            + ";status|invalid;message|That user doesn't exist";
                }
            } else if (messageType.equals("rts")) {
                String clientNo = receivedSplit[1].split("\\|")[1];

                CopyOnWriteArrayList<Search> searchList = server.getSearchList();

                Collections.sort(searchList);

                Collections.sort(urlList);

                message = "type|rtsResult;clientNo|" + clientNo;

                for (int i = 0; i < 10; i++) {
                    try{
                        message += ";url_" + i + "|" + urlList.get(i).getUrl();
                    } catch(ArrayIndexOutOfBoundsException e){
                        message += ";url_" + i + "|N/A";
                    }
                }

                for (int i = 0; i < 10; i++) {
                    try{
                        message += ";search_" + i + "|" + searchList.get(i).getWords();
                    } catch(ArrayIndexOutOfBoundsException e){
                        message += ";search_" + i + "|N/A";
                    }
                }

            } else if (messageType.equals("logout")) {
                String clientNo = receivedSplit[1].split("\\|")[1];
                String username = receivedSplit[2].split("\\|")[1];

                User tempUser = new User(username);

                int indexOfUser = listUsers.indexOf(tempUser);
                User user = listUsers.get(indexOfUser);

                user.setLoggedIn(false);

                message = "type|logoutResult;clientNo|" + clientNo + ";status|valid";

                saveUsers();

            } else if (messageType.equals("checkStatusConfirm")) {

                if (server.isCheckingMulticastServers()) {

                    int serverNo = Integer.parseInt(receivedSplit[1].split("\\|")[1]);

                    System.out.println("SERVER CHECKED: " + serverNo);

                    server.getMulticastServerCheckedList().add(serverNo);
                }

            } else if (messageType.equals("checkStatus")) {

                message = "type|checkStatusConfirm;serverNo|" + server.getMulticastServerNo();

            } else if (messageType.equals("multicastServerStarterResult")) {
                // Atualizar o array dos multicast servers que estão up

                int multicastServerCount = Integer.parseInt(receivedSplit[2].split("\\|")[1]);

                CopyOnWriteArrayList<MulticastServerInfo> newMulticastServerList = new CopyOnWriteArrayList<>();

                for (int i = 3; i < (multicastServerCount * 3 + 3); i = i + 3) {
                    int serverNo = Integer.parseInt(receivedSplit[i].split("\\|")[1]);
                    String address = receivedSplit[i + 1].split("\\|")[1];
                    int port = Integer.parseInt(receivedSplit[i + 2].split("\\|")[1]);
                    MulticastServerInfo msi = new MulticastServerInfo(serverNo, address, port);
                    newMulticastServerList.add(msi);
                }

                server.setMulticastServerList(newMulticastServerList);

                for (MulticastServerInfo msi : server.getMulticastServerList())
                    System.out.println("SERVER: " + msi.getServerNo() + "\nEndereço: " + msi.getTCP_ADDRESS()
                            + "\nPorto: " + msi.getTCP_PORT());

            }

            if (!message.equals("")) {
                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveUsers() {
        try {

            String file = "files/users_" + server.getMulticastServerNo() + ".txt";

            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            
            o.writeObject(server.getListUsers());

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        } catch(Exception e){
            System.out.print(e.getMessage());
        }
    }

}