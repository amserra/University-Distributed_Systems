package rmiserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * ClientInterface class that includes all the RMIClient remote methods
 */
public interface ClientInterface extends Remote {

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
     * Remote method the links pointing to another link
     *
     * @param clientNo
     * @param url
     * @return Answer message
     * @throws java.rmi.RemoteException
     */
    public String linksPointing(int clientNo, String url) throws java.rmi.RemoteException;


    public void notification()
            throws IOException, java.rmi.NotBoundException;

    /**
     * Remote method used by the RMIServer to make the Client print a new rtsUpdate.
     *
     * @param msg
     * @throws java.net.MalformedURLException
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public void rtsUpdate(String msg)
            throws java.net.MalformedURLException, java.rmi.RemoteException, java.rmi.NotBoundException;
}
