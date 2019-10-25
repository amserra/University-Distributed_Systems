import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastAdminPage extends Thread {

    private int PERIOD_TIME = 5000;

    private CopyOnWriteArrayList<Search> searchList;
    private CopyOnWriteArrayList<URL> urlList;

    private String[] top10search = new String[10];
    private String[] top10url = new String[10];

    private InetAddress group;
    private int PORT;
    private MulticastSocket socket;
     
    public MulticastAdminPage(MulticastServer server, InetAddress group, int PORT, MulticastSocket socket) {
        searchList = server.getSearchList();
        urlList = server.getUrlList();

        this.group = group;
        this.PORT = PORT;
        this.socket = socket;


        this.start();
    }

    public void run() {

        while (true) {

            try {
                Thread.sleep(PERIOD_TIME);

                Collections.sort(searchList);

                Collections.sort(urlList);

                boolean checkSearchList = false;
                boolean checkUrlList = false;

                //Vai ver se houve alguma alteracao
                for(int i = 0; i < 10; i++){
                    if(!searchList.get(i).getWords().equals(top10search[i]))
                        checkSearchList = true;
                    if(!urlList.get(i).getUrl().equals(top10url[i]))
                        checkUrlList = true;
                }

                //Se tiver havido alguma alteracao no top 10 pesquisas
                if(checkSearchList){
                    String message = "type|top10searchUpdate";
                    for(int i = 0; i < 10; i++){
                        top10search[i] = searchList.get(i).getWords();
                        message += "search_" + i + "|" + top10search[i];
                    }
                    byte[] buffer = message.getBytes();
                    DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packetSent);
                }

                //Se tiver havido alguma alteracao no top 10 urls
                if(checkUrlList){
                    String message = "type|top10urlUpdate";
                    for(int i = 0; i < 10; i++){
                        top10url[i] = urlList.get(i).getUrl();
                        message += "url_" + i + "|" + top10url[i];
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