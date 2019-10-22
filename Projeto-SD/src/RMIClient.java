import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    int clientNo;
    String typeOfClient = "anonymous";
    String username;
    UI userUI;
    RMIInterface ci;

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        new RMIClient();
    }

    RMIClient() throws MalformedURLException, RemoteException, NotBoundException {
        connectToRMIServer();
        userUI = new UI(this);
        userUI.mainMenu();
    }

    public void connectToRMIServer() throws MalformedURLException, RemoteException, NotBoundException {
        try {
            ci = (RMIInterface) Naming.lookup("RMIConnection");
        } catch (java.rmi.ConnectException e) {
            System.out.println("\nConnect the server first.");
            System.exit(-1);
        }
        String msg = ci.sayHello("client");
        clientNo = Integer.parseInt(msg.substring(msg.length() - 1));
        System.out.println(msg);

    }

    public void authentication(boolean isLogin, String username, String password)
            throws RemoteException, MalformedURLException, NotBoundException {
        try {
            ci = (RMIInterface) Naming.lookup("RMIConnection");
            String msg = ci.authentication(this.clientNo, isLogin, username, password);
            System.out.println("Recebi a mensagem: " + msg);

            String[] parameters = msg.split(";");
            String status = parameters[2].split("\\|")[1];
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);
            // Sera preciso esta confirmacao?
            if (this.clientNo == receivedClientNo) {
                if (status.equals("valid")) {
                    String usr = parameters[3].split("\\|")[1];
                    if (isLogin)
                        System.out.println("Login successful. Welcome " + usr + "\n");
                    else
                        System.out.println("Register successful. Welcome " + usr + "\n");

                    this.username = usr;
                    this.typeOfClient = "user";
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
        // Chamar metodo do server RMI para enviar termos de procura
        // Listar termos obtidos
        // Voltar para o search menu

        userUI.search();
    }

    public void searchHistory() {
        // Get searches from database and send them to userUI.searchHistory to be
        // printed
        // Call RMI Server method to query all the searches
    }

    public void indexNewURL(String url) throws RemoteException, MalformedURLException, NotBoundException {
        // Send URL from RMI server to Multicast Server to be indexed
        // return to indexNewURL menu to index more
        userUI.indexNewURL();
    }

    public void realTimeStatistics() {
        // Ask to the multicast server?
    }

    public void getUsers() {
        // Get users para conceder admin a um
        // Pedir ao RMI Server os clientes todos
    }
}