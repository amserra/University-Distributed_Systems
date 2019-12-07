/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {
	public boolean userMatchesPassword(String user, String password) throws RemoteException;
	public ArrayList<String> getAllUsers() throws RemoteException;
}
