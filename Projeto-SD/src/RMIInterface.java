import java.rmi.Remote;

public interface RMIInterface extends Remote {
    public String sayHello(String type) throws java.rmi.RemoteException;

    public String authentication(int clientNo, boolean isLogin, String username, String password)
            throws java.rmi.RemoteException;

    public String testPrimary() throws java.rmi.RemoteException;

    public String search(int clientNo, String username, String searchTerms) throws java.rmi.RemoteException;

    public String indexNewURL(int clientNo, String url) throws java.rmi.RemoteException;

}