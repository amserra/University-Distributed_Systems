import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServerControl extends Thread {

    int CHECK_PERIOD = 1000; //Time period between multicast server check
    int WAIT_TIME = 5000; // Time to wait for an answer from multicast serveres

    int PORT;
    InetAddress group;
    MulticastSocket socket;

    CopyOnWriteArrayList<MulticastServerInfo> multicastServerNoList;

    MulticastServer server;

    
    /** 
     * @param server
     * @param group
     * @param socket
     * @return 
     */
    public MulticastServerControl(MulticastServer server, InetAddress group, MulticastSocket socket) {
        this.multicastServerNoList = server.getMulticastServerList();
        this.server = server;
        this.PORT = server.getPORT();
        this.group = group;
        this.socket = socket;

        this.start();
    }

    /**
     * First sends multicast message to other multicast servers
     * Then waits for their response and checks which ones have answered
     * The one who didn't answer, it sends a message to RMI server
     */
    public void run() {
        while (true) {
            try {

                String message = "type|||checkStatus";
                byte[] buffer = message.getBytes();
                DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packetSent);

                System.out.println("Message sent: " + message);

                server.setCheckingMulticastServers(true);

                Thread.sleep(WAIT_TIME);

                server.setCheckingMulticastServers(false);

                if(server.getMulticastServerCheckedList().size() != server.getMulticastServerList().size()){
                    for(MulticastServerInfo msi: server.getMulticastServerList()){
                        boolean check_server = false;
                        for(Integer i : server.getMulticastServerCheckedList()){
                            if(i == msi.getServerNo()){
                                check_server = true;
                                break;
                            }
                        }

                        if(!check_server){
                            String messageServerDown = "type|||multicastServerDown;;serverNo|||" + msi.getServerNo();
                            byte[] bufferServerDown = messageServerDown.getBytes();
                            DatagramPacket packetSentServerDown = new DatagramPacket(bufferServerDown, bufferServerDown.length, group, PORT);
                            socket.send(packetSentServerDown);
                            server.getMulticastServerList().remove(msi);
                        }
                    }
                }

                server.getMulticastServerCheckedList().clear();

                Thread.sleep(CHECK_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}