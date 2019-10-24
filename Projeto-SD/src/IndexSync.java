import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class IndexSync {
    String file = "../files/index_";

    public IndexSync(int serverNo) {
        file += serverNo + ".txt";
    }

    public void saveUsers(ConcurrentHashMap<String, HashSet<String>> index) {
        try {
            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(index);

            o.close();
            f.close();

            /*
             * FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new
             * ObjectInputStream(fis);
             * 
             * HashMap<String, HashSet<String>> hashmap= new HashMap<>();
             * 
             * try { hashmap = (HashMap) ois.readObject(); } catch (ClassNotFoundException
             * e) { e.printStackTrace(); }
             * 
             * for(User u : hashmap){ System.out.println(u.getUsername()); }
             * 
             * ois.close(); fis.close();
             */

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }

    }

    public void test(String TCP_ADDRESS, int TCP_PORT, MulticastServer server) {
        ServerSocket listenSocket;
        try {
            listenSocket = new ServerSocket(TCP_PORT + server.getMulticastServerNo());

            while (true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                new Connection(clientSocket, server.getMulticastServerNo());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    int server_number;

    public Connection(Socket aClientSocket, int serverNo) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            server_number = serverNo;
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    // =============================
    public void run() {
        String resposta;
        try {
            while (true) {
                // an echo server
                String data = in.readUTF();
                System.out.println("Recebeu: " + data);
                resposta ="Mensagem recebida pelo servidor " + server_number;
                out.writeUTF(resposta);
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e);
        } catch (IOException e) {
            System.out.println("IO:" + e);
        }
    }
}