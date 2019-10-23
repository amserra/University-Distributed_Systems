import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements ClientInterface {
    static final long serialVersionUID = 1L;
    int clientNo;
    String typeOfClient = "anonymous";
    String username = null;
    UI userUI;
    ServerInterface serverInterface;
    final String RMINAME = "RMIConnection";

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        new RMIClient();
    }

    RMIClient() throws MalformedURLException, RemoteException, NotBoundException {
        super();
        connectToRMIServer();
        userUI = new UI(this);
        userUI.mainMenu();
    }

    public void notification() {
        System.out.println("\nNotification: Promoted to admin!");
        this.typeOfClient = "admin";
    }

    public void connectToRMIServer() throws MalformedURLException, RemoteException, NotBoundException {
        try {
            serverInterface = (ServerInterface) Naming.lookup(RMINAME);
        } catch (java.rmi.ConnectException e) {
            System.out.println("\nConnect the server first.");
            System.exit(-1);
        }
        // So that RMIServer has RMIClient reference (client send subscription to
        // server)
        String msg = serverInterface.sayHelloFromClient(this);
        clientNo = Integer.parseInt(msg.substring(msg.length() - 1));
        System.out.println(msg);
    }

    public void authentication(boolean isLogin, String username, String password)
            throws RemoteException, MalformedURLException, NotBoundException {
        try {
            // Dar para trocar o bkup pelo prim vir aqui. E so para ser + rapido
            // serverInterface = (RMIInterface) Naming.lookup(RMINAME);
            String msg = serverInterface.authentication(this.clientNo, isLogin, username, password);
            System.out.println("Recebi a mensagem: " + msg);

            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            String status = parameters[2].split("\\|")[1];
            // Sera preciso esta confirmacao?
            if (this.clientNo == receivedClientNo) {
                if (status.equals("valid")) {
                    String usr = parameters[3].split("\\|")[1];
                    boolean isAdmin = Boolean.parseBoolean(parameters[4].split("\\|")[1]);
                    if (isAdmin)
                        this.typeOfClient = "admin";
                    else
                        this.typeOfClient = "user";

                    if (isLogin) {
                        System.out.println("Login successful. Welcome " + usr + "\n");
                        boolean notification = Boolean.parseBoolean(parameters[5].split("\\|")[1]);
                        if (notification)
                            System.out.println("Notification: You have been promoted to admin!");
                    } else {
                        System.out.println("Register successful. Welcome " + usr + "\n");
                    }

                    this.username = usr;
                    userUI.mainMenu();

                } else if (status.equals("invalid")) {
                    if (isLogin) {
                        System.out.println("Login failed. Try again.\n");
                        userUI.login();
                    } else {
                        System.out.println("Register failed. Try again.\n");
                        userUI.register();
                    }
                } else {
                    // CHEGA AQUI QD MUDA DE SERVER
                    // Caso aconteca alguma coisa à mensagem
                    System.out.println("ERROR #1: Something went wrong. Would you mind to try again? :)");
                    if (isLogin)
                        userUI.login();
                    else
                        userUI.register();
                }
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #2: Something went wrong. Would you mind to try again? :)");
        }
    }

    public void logout(boolean result) throws RemoteException, MalformedURLException, NotBoundException {
        if (result == true) {
            this.typeOfClient = "anonymous";
        }
        System.out.println("\nLogout successful. You are now an anonymous user.\n");
        userUI.mainMenu();
    }

    public void shutdown() {
        // Save previous typeOfClient in case of change? How about when force shutdown?
        // Or is this info in Multicast?
        // Desligar conexao
        System.out.println("\nShutdown complete.\nHope to see you again soon! :)");
        System.exit(1);
    }

    public void search(String[] words) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String searchTerms = String.join(" ", words);
            String msg = serverInterface.search(this.clientNo, username, searchTerms);
            System.out.println("Recebi a mensagem: " + msg);
            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            int numOfURLs = Integer.parseInt(parameters[2].split("\\|")[1]);

            if (this.clientNo == receivedClientNo) {
                int count = 0;
                int startIndex = 3;
                // Starts at index 3
                for (int i = startIndex; i < numOfURLs + startIndex; i++) {
                    count++;
                    System.out.println("Url " + count + ": " + parameters[i]);
                }
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #3: Something went wrong. Would you mind to try again? :)");
        }

        userUI.search();
    }

    public void searchHistory() throws RemoteException, MalformedURLException, NotBoundException {
        // Call RMI Server method to query all the searches
        try {
            String msg = serverInterface.searchHistory(this.clientNo, this.username);
            System.out.println("Recebi a mensagem: " + msg);
            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            int searchCount = Integer.parseInt(parameters[2].split("\\|")[1]);

            if (this.clientNo == receivedClientNo && searchCount != 0) {
                int count = 0;
                int startIndex = 3;
                // Starts at index 3
                for (int i = startIndex; i < searchCount + startIndex; i++) {
                    count++;
                    System.out.println("Url " + count + ": " + parameters[i]);
                }
            } else if (this.clientNo == receivedClientNo && searchCount == 0) {
                System.out.println("You haven't searched for anything yet!");
            }

        } catch (RemoteException e) {
            System.out.println("ERROR #5: Something went wrong. Would you mind to try again? :)");
        }
        userUI.mainMenu();
    }

    public void linksPointing(String url) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.linksPointing(this.clientNo, url);
            System.out.println("Recebi a mensagem: " + msg);
            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            int numOfLinks = Integer.parseInt(parameters[2].split("\\|")[1]);
            if (this.clientNo == receivedClientNo && numOfLinks != 0) {
                int count = 0;
                int startIndex = 3;
                // Starts at index 3
                for (int i = startIndex; i < numOfLinks + startIndex; i++) {
                    count++;
                    System.out.println("Link " + count + ": " + parameters[i]);
                }
            } else if (this.clientNo == receivedClientNo && numOfLinks == 0) {
                System.out.println("Link doesn't have any pages connected to it yet!");
            }

        } catch (RemoteException e) {
            System.out.println("ERROR #6: Something went wrong. Would you mind to try again? :)");
        }
        userUI.mainMenu();
    }

    public void indexNewURL(String url) throws RemoteException, MalformedURLException, NotBoundException {
        // Send URL from RMI server to Multicast Server to be indexed
        // return to indexNewURL menu to index more
        try {
            String msg = serverInterface.indexNewURL(this.clientNo, url);
            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            System.out.println("Recebi a mensagem: " + msg);
            if (this.clientNo == receivedClientNo) {
                System.out.println("Started indexing the requested url!");
            }

        } catch (RemoteException e) {
            System.out.println("ERROR #4: Something went wrong. Would you mind to try again? :)");
        }
        userUI.indexNewURL();
    }

    public void grantPrivileges(String username) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.grantPrivileges(this.clientNo, username);
            System.out.println("Recebi a mensagem: " + msg);
            String[] parameters = msg.split(";");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            String status = parameters[2].split("\\|")[1];
            if (this.clientNo == receivedClientNo) {
                if (status.equals("valid")) {
                    System.out.println("Conceded admin privileges to " + username + " successfully");
                    // Falta o outro receber essa notificacao
                } else if (status.equals("invalid")) {
                    String errorMsg = parameters[3].split("\\|")[1];
                    System.out.println("ERROR: " + errorMsg + "\nTry again.");
                }
            }

        } catch (RemoteException e) {
            System.out.println("ERROR #7: Something went wrong. Would you mind to try again? :)");
        }
        userUI.grantPrivileges();
    }

    public void realTimeStatistics() {
        // Ask to the multicast server?
        // Callback!
    }
}