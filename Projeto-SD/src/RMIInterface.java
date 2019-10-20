import java.rmi.Remote;

public interface RMIInterface extends Remote {
    public String sayHello(String type) throws java.rmi.RemoteException;

    public String register(int clientNo, String username, String password) throws java.rmi.RemoteException;

    public String testPrimary() throws java.rmi.RemoteException;

}