/**
 * Raul Barbosa 2014-11-07
 */
package meta2.model;

import rmiserver.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Model that saves reference to RMI server and username, name, type of client and client number of the current user
 */
public class HeyBean {
	private ServerInterface server;
	private String username; // username and password supplied by the user
	private String name;
	private String typeOfClient = "anonymous";
	private int clientNo;

	public HeyBean() {
		try {
			//server = (ServerInterface) Naming.lookup("RMIConnection");
			server = (ServerInterface) LocateRegistry.getRegistry(2857).lookup("RMIConnection");
			String msg = server.sayHelloFromClient();
			System.out.println(msg);
			this.clientNo = Integer.parseInt(msg.substring(msg.length() - 1));
		}
		catch(NotBoundException|/*MalformedURLException|*/RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	public String getTypeOfClient() {
		return typeOfClient;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setClientNo(int clientNo) {this.clientNo = clientNo;}

	public void setTypeOfClient(String typeOfClient) {this.typeOfClient = typeOfClient;}

	public ServerInterface getServer() {return this.server;}

	public int getClientNo() {return this.clientNo;}

	public String getUsername() {return this.username;}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "HeyBean{" +
				"server=" + server +
				", username='" + username + '\'' +
				", clientNo=" + clientNo +
				'}';
	}
}
