import java.rmi.Remote;

public interface RMIInterface extends Remote {
    public String sayHello(String type) throws java.rmi.RemoteException;

}