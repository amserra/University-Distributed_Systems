import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastAdminPage extends Thread {

    private int PERIOD_TIME = 2000; // Period time between updates about real time statistics

    private CopyOnWriteArrayList<Search> searchList;
    private CopyOnWriteArrayList<URL> urlList;
    private CopyOnWriteArrayList<MulticastServerInfo> msiList;

    private String[] top10search = new String[10]; //Top 10 searches
    private String[] top10url = new String[10]; //Top 10 URLs with most links pointing

    private InetAddress group;
    private int PORT;
    private MulticastSocket socket;
     
    
    /** 
     * @param server
     * @param group
     * @param PORT
     * @param socket
     * @return 
     */
    public MulticastAdminPage(MulticastServer server, InetAddress group, int PORT, MulticastSocket socket) {
        searchList = server.getSearchList();
        urlList = server.getUrlList();
        msiList = server.getMulticastServerList();

        this.group = group;
        this.PORT = PORT;
        this.socket = socket;


        this.start();
    }

    /**
     * Sort search List and URL list between x time.
     * If something changed in top 10 send message to RMI server
     */

    public void run() {

        while (true) {

            try {
                Thread.sleep(PERIOD_TIME);

                Collections.sort(searchList);

                Collections.sort(urlList);

                boolean checkSearchList = false;
                boolean checkUrlList = false;

                //Checks if top 10 has changed
                for(int i = 0; i < 10; i++){
                    try{
                        if(!searchList.get(i).getWords().equals(top10search[i]))
                            checkSearchList = true;
                    } catch(Exception e){}
                    try{
                        if(!urlList.get(i).getUrl().equals(top10url[i]))
                            checkUrlList = true;
                    }catch(Exception e){}
                }

                //If something changed, update top 10 arrays and send update to RMI server
                if(checkSearchList || checkUrlList){

                    String message = "type|||rtsUpdate;;clientNo|||0";

                    //Update top 10 URL
                    for (int i = 0; i < 10; i++) {
                        try{
                            message += ";;url_" + i + "|||" + urlList.get(i).getUrl();
                            top10url[i] = urlList.get(i).getUrl();
                        } catch(ArrayIndexOutOfBoundsException e){
                            message += ";;url_" + i + "|||N/A";
                        }
                    }
    
                    //Update top 10 searches
                    for (int i = 0; i < 10; i++) {
                        try{
                            message += ";;search_" + i + "|||" + searchList.get(i).getWords();
                            top10search[i] = searchList.get(i).getWords();
                        } catch(ArrayIndexOutOfBoundsException e){
                            message += ";;search_" + i + "|||N/A";
                        }
                    }

                    for(MulticastServerInfo msi: msiList){
                        message += ";;address|||" + msi.getTCP_ADDRESS() + ";;port|||" + msi.getTCP_PORT();
                    }
            
                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

            
        }

    }
}