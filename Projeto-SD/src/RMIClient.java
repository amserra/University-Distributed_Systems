import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class that represents a RMIClient
 */
public class RMIClient extends UnicastRemoteObject implements ClientInterface {
    static final long serialVersionUID = 1L;
    int clientNo;
    String typeOfClient = "anonymous";
    String username = null;
    boolean inRealTimeStatistics = false;
    UI userUI;
    ServerInterface serverInterface; // To use the remote methods
    String RMINAME;

    /**
     * Main method that creates a RMIClient object
     * 
     * @param args
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        if (args.length != 2) {
            System.out.println("Introduce the IP and Port as argument.");
            System.exit(0);
        }
        String rmiName = "rmi://" + args[0] + ":" + args[1] + "RMIConnection";
        new RMIClient(rmiName);
    }

    /**
     * Constructor of the RMIClient class. Starts by connecting to the RMIServer,
     * creating the UI object, the controlCTRLC thread and redirecting the user to
     * the main menu.
     * 
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    RMIClient(String rmiName) throws MalformedURLException, RemoteException, NotBoundException {
        super();
        RMINAME = rmiName;
        connectToRMIServer();
        userUI = new UI(this);
        controlCTRLC();
        userUI.mainMenu();
    }

    // ClientInterface methods (documentation on ClientInterface class)

    public void notification() throws MalformedURLException, RemoteException, NotBoundException {
        if (this.typeOfClient.equals("user")) {
            System.out.print("\n\nNotification: Promoted to admin!\n\nEnter an option: ");
            this.typeOfClient = "admin";
        }
    }

    public void establishConnection() throws MalformedURLException, RemoteException, NotBoundException {
        // Dar para trocar o bkup pelo prim vir aqui. E so para ser + rapido
        serverInterface = (ServerInterface) Naming.lookup(RMINAME);
    }

    public void rtsUpdate(String msg) {
        if (this.inRealTimeStatistics) {
            System.out.println("\n[Update]\n");
            String[] parameters = msg.split(";;");
            printTop10(parameters);
        }
    }

    // End of ClientInterface methods

    /**
     * Connects the client to the PrimaryRMIServer and gives it its reference.
     * 
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
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

    /**
     * When a CTRL-C is set, this thread captures it and makes the user loggout, so
     * that the RMIServer knows the RMIClients connect at all times.
     */
    public void controlCTRLC() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    serverInterface.logout(clientNo, username, true);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RemoteException e) {
                    System.out.println("\nERROR #1: Something went wrong. Are there any RMIServers?");
                }
            }
        });
    }

    /**
     * Handles the authentication of an user, validating and making the necessary
     * verifications.
     * 
     * @param isLogin
     * @param username
     * @param password
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void authentication(boolean isLogin, String username, String password)
            throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.authentication(this.clientNo, isLogin, username, password);

            if (msg != null) {

                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                String status = parameters[2].split("\\|\\|\\|")[1];

                if (this.clientNo == receivedClientNo) {
                    if (status.equals("valid")) {
                        String usr = parameters[3].split("\\|\\|\\|")[1];
                        boolean isAdmin = Boolean.parseBoolean(parameters[4].split("\\|\\|\\|")[1]);
                        if (isAdmin)
                            this.typeOfClient = "admin";
                        else
                            this.typeOfClient = "user";

                        if (isLogin) {
                            System.out.println("Login successful. Welcome " + usr + "\n");
                            boolean notification = Boolean.parseBoolean(parameters[5].split("\\|\\|\\|")[1]);
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
                    }
                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #2: Something went wrong. Returning to main menu");
            userUI.mainMenu();
        }
    }

    /**
     * Handles the logout of an user, validating and making the necessary
     * verifications.
     * 
     * @param result
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void logout(boolean result) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.logout(this.clientNo, this.username, false);

            if (msg != null) {
                if (result == true) {
                    this.typeOfClient = "anonymous";
                }
                System.out.println("\nLogout successful. You are now an anonymous user.\n");
                userUI.mainMenu();
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #3: Something went wrong. Are there any RMIServers? Returning to main menu");
            userUI.mainMenu();
        }
    }

    /**
     * Handles the shutdown of an user, validating and making the necessary
     * verifications.
     */
    public void shutdown() throws RemoteException {
        try {
            serverInterface.logout(this.clientNo, this.username, true);
            System.out.println("\nShutdown complete.\nHope to see you again soon! :)");
            System.exit(1);
        } catch (RemoteException e) {
            System.out.println("ERROR #4: Something went wrong.");
            System.exit(1);
        }
    }

    /**
     * Handles the search operation, validating, making the necessary verifications
     * and printing the results.
     * 
     * @param words
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void search(String[] words) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String searchTerms = String.join(" ", words);
            String msg = serverInterface.search(this.clientNo, username, searchTerms);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                int numOfURLs = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);

                if (this.clientNo == receivedClientNo) {
                    if (numOfURLs == 0) {
                        System.out.println("Nothing came up! Ask an admin to index more pages.");
                    } else {
                        int count = 0;
                        // Starts at index 3
                        int j = 3;
                        int max;
                        if (numOfURLs < 100)
                            max = numOfURLs;
                        else
                            max = 100;

                        System.out.println("Found " + numOfURLs + "results. Showing the first " + max);
                        for (int i = 0; i < max; i++) {
                            count++;
                            System.out.println("\n" + count + "\n");
                            System.out.println("Title: " + parameters[j++].split("\\|\\|\\|")[1]);
                            System.out.println("Url: " + parameters[j++].split("\\|\\|\\|")[1]);
                            System.out.println("Text: " + parameters[j++].split("\\|\\|\\|")[1]);
                        }
                    }
                }
                userUI.search();
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Try again\n");
                userUI.search();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #5: Something went wrong. Returnig to main menu.");
            userUI.mainMenu();
        }
    }

    /**
     * Handles the search history operation, validating, making the necessary
     * verifications and printing the results.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void searchHistory() throws RemoteException, MalformedURLException, NotBoundException {
        // Call RMI Server method to query all the searches
        try {
            String msg = serverInterface.searchHistory(this.clientNo, this.username);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                int searchCount = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);

                if (this.clientNo == receivedClientNo && searchCount != 0) {
                    int count = 0;
                    int startIndex = 3;
                    // Starts at index 3
                    for (int i = startIndex; i < searchCount + startIndex; i++) {
                        count++;
                        System.out.println("Url " + count + ": " + parameters[i].split("\\|\\|\\|")[1]);
                    }
                } else if (this.clientNo == receivedClientNo && searchCount == 0) {
                    System.out.println("You haven't searched for anything yet!");
                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #6: Something went wrong. Returning to main menu");
        }
        userUI.mainMenu();
    }

    /**
     * Handles the links with connection to another link operation, validating,
     * making the necessary verifications and displaying the results.
     * 
     * @param url
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void linksPointing(String url) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.linksPointing(this.clientNo, url);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                int numOfLinks = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);
                if (this.clientNo == receivedClientNo && numOfLinks != 0) {
                    int count = 0;
                    int startIndex = 3;
                    // Starts at index 3
                    for (int i = startIndex; i < numOfLinks + startIndex; i++) {
                        count++;
                        System.out.println("Link " + count + ": " + parameters[i].split("\\|\\|\\|")[1]);
                    }
                } else if (this.clientNo == receivedClientNo && numOfLinks == 0) {
                    System.out.println("Link doesn't have any pages connected to it yet!");
                }
                userUI.linksPointing();
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #7: Something went wrong. Would you mind to try again? :)");
            userUI.linksPointing();
        }
    }

    /**
     * Handles the index new URL operation, validating and making the necessary
     * verifications.
     * 
     * @param url
     */
    public void indexNewURL(String url) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.indexNewURL(this.clientNo, url);
            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);

                if (this.clientNo == receivedClientNo) {
                    System.out.println("Started indexing the requested url!");
                }
                userUI.indexNewURL();
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #8: Something went wrong. Would you mind to try again? :)");
            userUI.indexNewURL();
        }
    }

    /**
     * Handles the RTS operation, retrieving the first update.
     * 
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws RemoteException
     */
    public void realTimeStatistics() throws MalformedURLException, NotBoundException, RemoteException {
        try {
            String msg = serverInterface.realTimeStatistics(this.clientNo);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                System.out.println("Recieved client no: " + receivedClientNo);
                if (this.clientNo == receivedClientNo) {
                    System.out.println("Started receiving updates...");
                    printTop10(parameters);

                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                this.inRealTimeStatistics = false;
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #9: Something went wrong. Returning to main menu");
            this.inRealTimeStatistics = false;
            userUI.mainMenu();
        }
    }

    /**
     * Handles the grant privileges operation, validating, making the necessary
     * verifications and conceding the privilege.
     * 
     * @param username
     */
    public void grantPrivileges(String username) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            String msg = serverInterface.grantPrivileges(this.clientNo, username);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                System.out.println("Recieved client no: " + receivedClientNo);
                String status = parameters[2].split("\\|\\|\\|")[1];
                System.out.println("Status: " + status);
                if (this.clientNo == receivedClientNo) {
                    if (status.equals("valid")) {
                        System.out.println("Conceded admin privileges to " + username + " successfully");
                    } else if (status.equals("invalid")) {
                        String errorMsg = parameters[3].split("\\|\\|\\|")[1];
                        System.out.println("ERROR: " + errorMsg + "\nTry again.");
                    }
                }
                userUI.grantPrivileges();
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                userUI.mainMenu();
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #10: Something went wrong. Would you mind to try again? :)");
            userUI.grantPrivileges();
        }
    }

    /**
     * Auxiliar method to print the RTS.
     * 
     * @param parameters
     */
    public void printTop10(String[] parameters) {
        int cont = 1;
        for (int i = 2; i < parameters.length; i++, cont++) {
            if (i == 2) {
                cont = 1;
                System.out.println("\nTop 10 - Most relevant pages:\n");
            } else if (i == 12) {
                cont = 1;
                System.out.println("\nTop 10 - Most searched terms:\n");
            } else if (i == 22) {
                cont = 1;
                System.out.println("\nActive multicast servers:\n");
            }

            if (i < 22) {
                System.out.println(cont + ". " + parameters[i].split("\\|\\|\\|")[1]);
            } else {
                System.out.println(cont + ". Ip: " + parameters[i++].split("\\|\\|\\|")[1] + " Port: "
                        + parameters[i].split("\\|\\|\\|")[1]);
            }
        }
    }
}