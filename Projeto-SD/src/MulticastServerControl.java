import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashSet;

public class MulticastServerControl extends Thread {

    // Thread que peridodicamente verifica se os multicast servers foram abaixo

    int CHECK_PERIOD = 15000;
    int WAIT_TIME = 5000;

    int PORT;
    InetAddress group;
    MulticastSocket socket;

    ArrayList<Integer> multicastServerNoList;

    MulticastServer server;

    public MulticastServerControl(MulticastServer server, InetAddress group, MulticastSocket socket) {
        this.multicastServerNoList = server.getMulticastServerNoList();
        this.server = server;
        this.PORT = server.getPORT();
        this.group = group;
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try {

                String message = "type|checkStatus";
                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);

                server.setCheckingMulticastServers(true);

                Thread.sleep(WAIT_TIME);

                server.setCheckingMulticastServers(false);

                if(server.getMulticastServerCheckedList().size() != server.getMulticastServerCheckedList().size())
                    for(Integer i: server.getMulticastServerNoList())
                        if(!server.getMulticastServerCheckedList().contains(i))
                            System.out.println("Foi abaixo " + i); //MANDAR MENSAGEM AO SERVIDOR RMI QUE O MULTICAST SERVER FOI DOWN E REMOVER DA LISTA DE SERVERS (do multicast server)
                        else
                            System.out.println("Está bom " + i);

                server.setMulticastServerCheckedList(new HashSet<Integer>());
                

                

                Thread.sleep(CHECK_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}