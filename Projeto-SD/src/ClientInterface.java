import java.rmi.Remote;

public interface ClientInterface extends Remote {
    // public String sayHello(String type) throws java.rmi.RemoteException;

    public void notification()
            throws java.rmi.RemoteException, java.rmi.NotBoundException, java.net.MalformedURLException;

    public void establishConnection()
            throws java.net.MalformedURLException, java.rmi.RemoteException, java.rmi.NotBoundException;
}