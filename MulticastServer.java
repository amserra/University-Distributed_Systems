import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class MulticastServer extends Thread {

    //Thread para fazer a recusrsividade e adicionar as palavras
    //Thread para cada comunicacao com o servidor rmi (pesquisa, login, registo etc)
    //Thread que peridodicamente comunica com os outros servidores multicast e sincronizam as informacoes
    //Usar multicast client para testar cenas

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;
    protected byte[] buf = new byte[256];

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running...");
        try {
            //socket = new MulticastSocket();  // create socket without binding it (only for sending)
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packetReceived = new DatagramPacket(buf, buf.length);
                socket.receive(packetReceived);
                String received = new String(packetReceived.getData(), 0, packetReceived.getLength());

                System.out.println("Received: " + received);

                /*String message = this.getName() + " packet " + counter++;
                byte[] buffer = message.getBytes();*/

                String message = "Message received";
                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
