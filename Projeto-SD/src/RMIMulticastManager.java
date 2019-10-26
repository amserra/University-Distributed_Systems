import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class RMIMulticastManager extends Thread {
    RMIServer server;
    MulticastSocket socket = null;
    final String MULTICAST_ADDRESS = "224.0.224.0";
    final int PORT = 4369;

    RMIMulticastManager(RMIServer server) {
        this.server = server;
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

                if (type.equals("multicastServerStarter")) {
                    System.out.println("[Thread] Type = " + type);
                    // Read message
                    String ipAddress = parameters[1].split("\\|")[1];
                    int port = Integer.parseInt(parameters[2].split("\\|")[1]);
                    // Process message
                    int serverNo = getBestServerNo();
                    this.server.multicastServers.add(new MulticastServerInfo(serverNo, ipAddress, port));
                    sortArrayList();

                    // Send message
                    if (!this.server.isBackup) {
                        int serverCount = this.server.multicastServers.size();
                        String msg = "type|multicastServerStarterResult;serverNo|" + serverNo + ";serverCount|"
                                + serverCount;
                        for (int i = 0; i < this.server.multicastServers.size(); i++) {
                            MulticastServerInfo s = this.server.multicastServers.get(i);
                            msg += ";serverNo_" + i + "|" + s.getServerNo() + ";ip_" + i + "|" + s.getTCP_ADDRESS()
                                    + ";porto_" + i + "|" + s.getTCP_PORT();
                        }
                        System.out.println("[Thread] Msg sent: " + msg);

                        byte[] bufferSend = msg.getBytes();
                        DatagramPacket packetSend = new DatagramPacket(bufferSend, bufferSend.length, group, PORT);
                        socket.send(packetSend);
                    }

                } else if (type.equals("multicastServerDown")) {
                    System.out.println("[Thread] Type = " + type);
                    // Read message
                    int serverNo = Integer.parseInt(parameters[1].split("\\|")[1]);
                    // Process message
                    deleteMulticastServer(serverNo);
                } else if (type.equals("rtsResult") && !this.server.isBackup) {
                    this.server.sendRtsToAll(msgReceive);
                }
            }
        } catch (Exception e) {
            socket.close();
            System.out.println("[Thread] ERROR: Something went wrong. Aborting program...");
            System.exit(-1);
        } finally {
            socket.close();
        }
    }

    public void deleteMulticastServer(int serverNo) {
        if (this.server.multicastServers.stream().filter(o -> o.getServerNo() == serverNo).findFirst().isPresent()) {
            this.server.multicastServers.removeIf(t -> t.getServerNo() == serverNo);
        }
    }

    public int getBestServerNo() {
        // Ns se ta a dar
        int finalValue = 1;
        boolean foundValue = false;
        int j = 1;
        while (!foundValue) {
            final int i = j;
            if (!this.server.multicastServers.stream().filter(o -> o.getServerNo() == i).findFirst().isPresent()) {
                finalValue = i;
                foundValue = true;
            }
            j++;
        }
        return finalValue;
    }

    public void sortArrayList() {
        Collections.sort(this.server.multicastServers, (o1, o2) -> o1.compareTo(o2.getServerNo()));
    }
}
