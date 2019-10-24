import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class RMIMulticastManager extends Thread {
    MulticastSocket socket = null;
    final String MULTICAST_ADDRESS = "224.0.224.0";
    final int PORT = 4369;

    RMIMulticastManager(RMIServer server) {
        this.start();
    }

    public void run() {
        try {
            // Send
            socket = new MulticastSocket(PORT); // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            // 2 tipos de mensagens:
            // multicastserverstarter (nao o result)
            // multicastserverdown

            String type;
            int receivedClientNo;
            String msgReceive;

            while (true) {
                byte[] bufferReceive = new byte[64 * 1024];
                DatagramPacket packetReceive = new DatagramPacket(bufferReceive, bufferReceive.length);
                socket.receive(packetReceive);
                msgReceive = new String(packetReceive.getData(), 0, packetReceive.getLength());

                String[] parameters = msgReceive.split(";");
                type = parameters[0].split("\\|")[1];
                receivedClientNo = Integer.parseInt(parameters[1].split("\\|")[1]);

                System.out.println("Type = " + type);

                // Opercao de adicionar/remover a hashmap
                // byte[] bufferSend = msg.getBytes();
                // DatagramPacket packetSend = new DatagramPacket(bufferSend, bufferSend.length,
                // group, PORT);
                // socket.send(packetSend);

            }

            // System.out.println("Mensagem final: " + msgReceive);
            // dar return de msgReceive

        } catch (Exception e) {
            socket.close();
            System.out.println(
                    "ERROR: Something went wrong. Did you forget the flag? Are there any multicast servers? Aborting program...");
            System.exit(-1);
        } finally {
            socket.close();
        }
    }
}
