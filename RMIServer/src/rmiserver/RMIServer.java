/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.ArrayList;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
	private static final long serialVersionUID = 20141107L;
	private HashMap<String, String> users;
	
	public RMIServer() throws RemoteException {
		super();
		users = new HashMap<String, String>();
		users.put("bender", "rodriguez"); // static users and passwords, to simplify the example
		users.put("fry",    "philip");
		users.put("leela",  "turanga");
		users.put("homer",  "simpson");
	}
	
	/**
	 * returns true if and only if user matches password
	 */
	public boolean userMatchesPassword(String user, String password) throws RemoteException {
		System.out.println("Looking up " + user + "...");
		return users.containsKey(user) && users.get(user).equals(password);
	}
	
	/**
	 * returns all the user names
	 */
	public ArrayList<String> getAllUsers() {
		System.out.println("Looking up all users...");
		return new ArrayList<String>(users.keySet());	
	}
	
	public static void main(String[] args) throws RemoteException {
		RMIServerInterface s = new RMIServer();
		LocateRegistry.createRegistry(1099).rebind("server", s);
		System.out.println("Server ready...");
	}
}
