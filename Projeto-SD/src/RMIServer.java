import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    private static final long serialVersionUID = 1L;
    int clientNo = 1; // Id for RMIServer to indentify RMIClients

    RMIServer() throws RemoteException {
        super();
    }

    public String sayHello() throws RemoteException {
        System.out.println("[Client " + clientNo + "] " + "Has just connected.");
        clientNo++;
        return "Connected to RMI Server successfully!";
    }

    public void login() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        // Preciso de 2 server RMI (2 threads?). Um vai ser o ativo e outro o passivo
        RMIInterface ci = new RMIServer();
        try {
            LocateRegistry.createRegistry(1099).rebind("RMIConnection", ci);
        } catch (Exception e) {
            // Qual a excecao?
            System.out.println("\nSomething went wrong. Aborting program...");
            System.exit(-1);
        }

        System.out.println("RMIServer ready...");
        System.out.println("Print model: \"[Message responsible] Message\"");
    }

}