package rmiserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * rmiserver.ServerInterface class that includes all the RMIServer remote methods
 */
public interface ServerInterface extends Remote {
        /**
         * Remote Hello RMI method from BackupRMIServer to PrimaryRMIServer
         * 
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String sayHelloFromBackup() throws java.rmi.RemoteException;

        /**
         * Remote Hello RMI method from Tomcat to PrimaryRMIServer
         *
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String sayHelloFromClient() throws java.rmi.RemoteException;

        /**
         * Remote Hello RMI method from Client to PrimaryRMIServer
         * 
         * @param client Gives the client reference
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String sayHelloFromClient(ClientInterface client) throws java.rmi.RemoteException;

        /**
         * Remote method used by BackupRMIServer to test the PrimaryRMIServer
         * 
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String testPrimary() throws java.rmi.RemoteException;

        /**
         * Remote method used by BackupRMIServer to get the map between clientNo and
         * rmiserver.ClientInterface stored in the PrimaryRMIServer
         * 
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public HashMap<Integer, ClientInterface> getHashMapFromPrimary() throws java.rmi.RemoteException;

        /**
         * Remote method used by the Client to get its assigned clientNo by RMIServer
         * 
         * @return Assigned clientNo
         * @throws java.rmi.RemoteException
         */
        public int getClientNoFromPrimary() throws java.rmi.RemoteException;

        /**
         * Remote login and register method
         * 
         * @param clientNo
         * @param isLogin  If is login(true) or register(false)
         * @param username
         * @param password
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String authentication(int clientNo, boolean isLogin, String username, String password)
                        throws java.rmi.RemoteException;

        /**
         * Remote method to get authorization URL when user tries to login with facebook account
         *
         * @param secretState //Client number
         * @return
         * @throws java.rmi.RemoteException
         */
        public String getAuthorizationUrl(String secretState)
                throws java.rmi.RemoteException;

        /**
         *
         * Get Facebook name and ID from client that has logged in
         *
         * @param code
         * @param clientNo
         * @param username
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws IOException
         * @throws ParseException
         */
        public JSONObject exchangeCodeForToken(String code, int clientNo, String username) throws InterruptedException, ExecutionException, IOException, ParseException;

        /**
         * Remote method to logout an user
         *
         * @param clientNo
         * @param username
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String logout(int clientNo, String username) throws java.rmi.RemoteException;

        /**
         * Remote method to logout an user
         * 
         * @param clientNo
         * @param username
         * @param exit
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String logout(int clientNo, String username, boolean exit) throws java.rmi.RemoteException;

        /**
         * Remote search method
         * 
         * @param clientNo
         * @param username
         * @param searchTerms
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String search(int clientNo, String username, String searchTerms) throws java.rmi.RemoteException;

        /**
         * Remote method to retrieve the searchHistory
         * 
         * @param clientNo
         * @param username
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String searchHistory(int clientNo, String username) throws java.rmi.RemoteException;

        /**
         * Remote method to detect language of text
         *
         * @param text
         * @return
         * @throws IOException
         * @throws ParseException
         */
        public String translateText(String text) throws IOException, ParseException;

        /**
         * Remote method the links pointing to another link
         * 
         * @param clientNo
         * @param url
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String linksPointing(int clientNo, String url) throws java.rmi.RemoteException;

        /**
         * Remote method to index a new URL by the admin
         * 
         * @param clientNo
         * @param url
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String indexNewURL(int clientNo, String url) throws java.rmi.RemoteException;

        /**
         * Remote method called by an admin to get the realTimeStatistics
         * 
         * @param clientNo
         * @return Answer message
         * @throws java.rmi.RemoteException
         */
        public String realTimeStatistics(int clientNo) throws java.rmi.RemoteException;

        /**
         * Remote method used by an admin to grant privileges to another admin
         * 
         * @param clientNo
         * @param username
         * @return Answer message
         * @throws java.rmi.RemoteException
         * @throws java.rmi.NotBoundException
         * @throws java.net.MalformedURLException
         */
        public String grantPrivileges(int clientNo, String username)
                        throws java.rmi.RemoteException, java.rmi.NotBoundException, java.net.MalformedURLException;
}