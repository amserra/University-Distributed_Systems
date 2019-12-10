package rmiserver;

import java.rmi.Remote;

/**
 * rmiserver.ClientInterface class that includes all the RMIClient remote methods
 */
public interface ClientInterface extends Remote {

        /**
         * Remote method used by the PrimaryRMIServer that prints a live notification to
         * a user.
         * 
         * @throws java.rmi.RemoteException
         * @throws java.rmi.NotBoundException
         * @throws java.net.MalformedURLException
         */
        public void notification()
                        throws java.rmi.RemoteException, java.rmi.NotBoundException, java.net.MalformedURLException;

        /**
         * Remote method used by the new PrimaryRMIServer(former Backup) to reestablish
         * the Client-Server connection.
         * 
         * @throws java.net.MalformedURLException
         * @throws java.rmi.RemoteException
         * @throws java.rmi.NotBoundException
         */
        public void establishConnection()
                        throws java.net.MalformedURLException, java.rmi.RemoteException, java.rmi.NotBoundException;

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