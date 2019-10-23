import java.rmi.Remote;

public interface ServerInterface extends Remote {
    public String sayHelloFromBackup() throws java.rmi.RemoteException; // For backup server

    public String sayHelloFromClient(ClientInterface client) throws java.rmi.RemoteException; // For
                                                                                              // clients

    public String authentication(int clientNo, boolean isLogin, String username, String password)
            throws java.rmi.RemoteException;

    public String testPrimary() throws java.rmi.RemoteException;

    public String search(int clientNo, String username, String searchTerms) throws java.rmi.RemoteException;

    public String indexNewURL(int clientNo, String url) throws java.rmi.RemoteException;

    public String searchHistory(int clientNo, String username) throws java.rmi.RemoteException;

    public String linksPointing(int clientNo, String url) throws java.rmi.RemoteException;

    public String grantPrivileges(int clientNo, String username)
            throws java.rmi.RemoteException, java.rmi.NotBoundException, java.net.MalformedURLException;

    public String logout(int clientNo, String username) throws java.rmi.RemoteException;

}