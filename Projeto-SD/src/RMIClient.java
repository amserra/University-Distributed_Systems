import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    int clientNo;
    String typeOfClient;
    String username;
    UI userUI;
    RMIInterface ci;

    RMIClient() throws MalformedURLException, RemoteException, NotBoundException {
        connectToRMIServer();
        this.typeOfClient = "anonymous";
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
        this.clientNo = Integer.parseInt(msg.substring(msg.length() - 1));
        System.out.println(msg);

    }

    public void login() throws RemoteException {
        // Chamar metodo do server RMI para verificar se user ja existe.
        // Se sim, entao meter outra vez a página login do UI userUI.login()
        // Se nao, dizer "Registo bem sucedido" e ir para o userUI.mainMenu()
        // Change type of user

        userUI.mainMenu();
    }

    public void register(String username, String password) throws RemoteException {
        String msg = ci.register(this.clientNo, username, password);
        // if msg == success, main menu
        // else, register
        // Chamar metodo do server RMI para verificar se user ja existe.
        // Se sim, entao meter outra vez a página registo do UI userUI.register()
        // Se nao, dizer "Registo bem sucedido" e ir para o userUI.mainMenu()
        // Change type of user

        userUI.mainMenu();
    }

    public void logout(boolean result) throws RemoteException {
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

    public void search(String[] words) throws RemoteException {
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

    public void indexNewURL(String url) throws RemoteException {
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

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        new RMIClient();
    }

}