import java.rmi.Remote;

public interface RMIInterface extends Remote {
    public String sayHello() throws java.rmi.RemoteException;

}