import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler extends Thread implements Runnable{

    private MulticastServer server;
    private String url;
    private int MaxWordsText = 20; // Max words that is stored in text
    private int MaxTextDetect = 100;
    private InetAddress group;
    private MulticastSocket socket;
    private int urlID;
    private String languageCurrent;

    private Queue<String> urlQueue = new LinkedList<>(); // Queue with URLs to be indexed

    private HashMap<String,Queue<String>> linkPointing = new HashMap<>(); // Store links pointing to an URL

    
    /** 
     * @param server
     * @param url
     * @param group
     * @param socket
     * @param urlID
     * @return
     */
    public WebCrawler(MulticastServer server, String url, InetAddress group, MulticastSocket socket, int urlID) {
        super();
        this.server = server;
        this.url = url;
        this.group = group;
        this.socket = socket;
        this.urlID = urlID;
    }

    public void run() {
        ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index = server.getIndex();
        CopyOnWriteArrayList<URL> urlList = server.getUrlList();
        recursiveIndex(index, urlList, url);
    }

    
    /** 
     * First it gets the URL that's pointing to this URL
     * Removes URL from Queue
     * Checks links pointing
     * Iterates over words of page and add to HashMap index
     * Add links in page to Queue
     * Call recursiveIndex again
     * @param index
     * @param urlList
     * @param url
     */
    private synchronized void recursiveIndex(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, String url) {

        String previousUrl = null;

        //Get URL pointing to this URL
        if(linkPointing.containsKey(url)){
            previousUrl = linkPointing.get(url).peek();
            linkPointing.get(url).remove();
        }
        String saveURL = url;

        //Remove URL from queue
        if (!urlQueue.isEmpty())
            urlQueue.remove();


        URL urlObject = new URL(url);

        // Add URL to URL list or changes if it is already there
        if (!urlList.contains(urlObject)) {

            if(previousUrl != null){
                //Creates new URL object and puts links pointing to 1
                urlObject = new URL(url,1);

                // Create new list with URLs pointing and adds new URL
                CopyOnWriteArraySet<String> urlPointingList = urlObject.getUrlPointingToMeList();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);

            } else{
                urlObject = new URL(url, 0);
            }

            // Add URL to URL list
            urlList.add(urlObject);
        } else {

            int indexOfUrl = urlList.indexOf(urlObject);
            urlObject = urlList.get(indexOfUrl);

            if(previousUrl != null){
                //Increment links pointing
                urlObject.setLinksCount(urlObject.getLinksCount() + 1);

                // Adds new URL pointing
                CopyOnWriteArraySet<String> urlPointingList = urlObject.getUrlPointingToMeList();
                if (urlPointingList == null)
                    urlPointingList = new CopyOnWriteArraySet<>();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);
            }
            
        }
    

        CopyOnWriteArraySet<String> indexURLs = new CopyOnWriteArraySet<>();

        // Index words in this URL
        try {
                Document doc = Jsoup.connect(url).get();
                urlObject.setTitle(doc.title()); // Save title
                StringTokenizer tokens = new StringTokenizer(doc.text());
                String currentToken;
                String text = ""; // Save text
                String textDetect = ""; //Text to send to RMI server to detect language
                int wordsCount = 0;
                while (tokens.hasMoreElements()) {
                    currentToken = tokens.nextToken();

                    indexURLs = index.get(currentToken.toLowerCase());

                    if (indexURLs == null) {
                        indexURLs = new CopyOnWriteArraySet<String>();
                        index.put(currentToken.toLowerCase(), indexURLs);
                    } 

                    indexURLs.add(url);

                    if (wordsCount <= MaxTextDetect) { //Only saves MaxWordsText
                        textDetect += currentToken + " ";
                        if(wordsCount <= MaxWordsText)
                            text += currentToken + " ";
                        wordsCount++;
                    }



                }

                sendTextToRMI(textDetect);


                while(languageCurrent == null)
                    synchronized (this){
                        this.wait();
                    }

                urlObject.setLang(languageCurrent.toUpperCase());

                languageCurrent = null;


                urlObject.setText(text);
                
                Elements links = doc.select("a[href]");
                // Add URLs to Queue
                for (Element link : links) {

                    url = (link.attr("abs:href"));

                    urlQueue.add(url);

                    //Update hashMap with links pointing
                    if(linkPointing.containsKey(url)){
                        linkPointing.get(url).add(saveURL);
                    } else{
                        Queue<String> tempQueue = new LinkedList<>();
                        tempQueue.add(saveURL);
                        linkPointing.put(url, tempQueue);
                    }
                }

            } catch (MalformedURLException InvalidUrl) {
                urlList.remove(urlObject);
                System.out.println("URL invalido. A continuar indexacao...");
            } catch (IllegalArgumentException EmptyUrl){
                urlList.remove(urlObject);
                System.out.println("URL invalido. A continuar indexacao...");
            } catch (HttpStatusException UnavailablePage){
                urlList.remove(urlObject);
                System.out.println("Pagina indisponivel. A continuar indexacao...");
            } catch(EOFException e){
                urlList.remove(urlObject);
                System.out.println("Aconteceu algum erro na leitura do url. A continuar indexacao...");
            } catch(OutOfMemoryError e){
                System.out.println("O HashMap nao tem mais espaço. A memoria acabou. O que fazemos neste caso?");
            } catch(ConnectException e){
                urlList.remove(urlObject);
                System.out.println("Erro de conexão. A continuar indexação...");
            } catch(UnknownHostException e){
                urlList.remove(urlObject);
                System.out.println("URL invalido. A continuar indexacao...");
            } catch(SSLHandshakeException e){
                urlList.remove(urlObject);
                System.out.println("Erro de conexão. A continuar indexação...");
            } catch (IOException e) {
                urlList.remove(urlObject);
                System.out.println("I/O error");
            } catch(Exception e){
                urlList.remove(urlObject);
                e.printStackTrace();
            }

            //Check if queue is empty and continue recursive index
            if (!urlQueue.isEmpty())
                recursiveIndex(index, urlList, urlQueue.peek());

        }

    private void sendTextToRMI(String textDetect) throws IOException {
        String message = "type|||detect;;serverNo|||" + server.getMulticastServerNo() + ";;urlID|||" + urlID + ";;text|||" + textDetect;


        byte[] buffer = message.getBytes();
        DatagramPacket packetSent = new DatagramPacket(buffer, buffer.length, group, server.getPORT());
        socket.send(packetSent);

    }

    public int getUrlID() {
        return urlID;
    }

    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }

    public String getLanguageCurrent() {
        return languageCurrent;
    }

    public void setLanguageCurrent(String languageCurrent) {
        this.languageCurrent = languageCurrent;
    }
}