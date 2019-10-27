import java.rmi.Remote;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ServerInterface extends Remote {
        public String sayHelloFromBackup() throws java.rmi.RemoteException; // For backup server

        public String sayHelloFromClient(ClientInterface client) throws java.rmi.RemoteException; // For
                                                                                                  // clients

        public String authentication(int clientNo, boolean isLogin, String username, String password)
                        throws java.rmi.RemoteException;

        public String testPrimary() throws java.rmi.RemoteException;

        public HashMap<Integer, ClientInterface> getHashMapFromPrimary() throws java.rmi.RemoteException;

        public int getClientNoFromPrimary() throws java.rmi.RemoteException;

        public String search(int clientNo, String username, String searchTerms) throws java.rmi.RemoteException;

        public String indexNewURL(int clientNo, String url) throws java.rmi.RemoteException;

        public String searchHistory(int clientNo, String username) throws java.rmi.RemoteException;

        public String linksPointing(int clientNo, String url) throws java.rmi.RemoteException;

        public String grantPrivileges(int clientNo, String username)
                        throws java.rmi.RemoteException, java.rmi.NotBoundException, java.net.MalformedURLException;

        public String logout(int clientNo, String username, boolean exit) throws java.rmi.RemoteException;

        public String realTimeStatistics(int clientNo) throws java.rmi.RemoteException;

        public CopyOnWriteArrayList<MulticastServerInfo> activeMulticastServers() throws java.rmi.RemoteException;

}