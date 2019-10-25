import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServerControl extends Thread {

    // Thread que peridodicamente verifica se os multicast servers foram abaixo

    int CHECK_PERIOD = 15000;
    int WAIT_TIME = 5000;

    int PORT;
    InetAddress group;
    MulticastSocket socket;

    CopyOnWriteArrayList<MulticastServerInfo> multicastServerNoList;

    MulticastServer server;

    public MulticastServerControl(MulticastServer server, InetAddress group, MulticastSocket socket) {
        this.multicastServerNoList = server.getMulticastServerList();
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

                if(server.getMulticastServerCheckedList().size() != server.getMulticastServerList().size()){
                    for(MulticastServerInfo msi: server.getMulticastServerList()){
                        boolean check_server = false;
                        System.out.println("A verificar servidor: " + msi.getServerNo());
                        for(Integer i : server.getMulticastServerCheckedList()){
                            System.out.println(i);
                            if(i == msi.getServerNo()){
                                System.out.println("VERIFICADO");
                                check_server = true;
                            }
                        }

                        if(!check_server){
                            String messageServerDown = "type|multicastServerDown;serverNo|" + msi.getServerNo();
                            byte[] bufferServerDown = messageServerDown.getBytes();
                            DatagramPacket packetSentServerDown = new DatagramPacket(bufferServerDown, bufferServerDown.length, group, PORT);
                            socket.send(packetSentServerDown);
                            //System.out.println("Foi abaixo " + msi.getServerNo()); //MANDAR MENSAGEM AO SERVIDOR RMI QUE O MULTICAST SERVER FOI DOWN E REMOVER DA LISTA DE SERVERS (do multicast server)
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