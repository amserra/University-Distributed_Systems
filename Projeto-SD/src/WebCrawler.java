import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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

public class WebCrawler extends Thread {

    private MulticastServer server;
    private String url;
    private int MaxWordsText = 20; // Max words that is stored in text

    private Queue<String> urlQueue = new LinkedList<>(); // Queue with URLs to be indexed

    private HashMap<String,Queue<String>> linkPointing = new HashMap<>(); // Store links pointing to an URL

    
    /** 
     * @param server
     * @param url
     * @return 
     */
    public WebCrawler(MulticastServer server, String url) {
        super();
        this.server = server;
        this.url = url;
    }

    public void run() {
        ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index = server.getIndex();
        CopyOnWriteArrayList<URL> urlList = server.getUrlList();
        recursiveIndex(index, urlList, url);
    }

    
    /** 
     * @param index
     * @param urlList
     * @param url
     * 
     * First it gets the URL that's pointing to this URL
     * Removes URL from Queue
     * Checks links pointing
     * Iterates over words of page and add to HashMap index
     * Add links in page to Queue
     * Call recursiveIndex again
     */
    private void recursiveIndex(ConcurrentHashMap<String, CopyOnWriteArraySet<String>> index, CopyOnWriteArrayList<URL> urlList, String url) {

        String previousUrl = null;

        if(linkPointing.containsKey(url)){
            previousUrl = linkPointing.get(url).peek();
            linkPointing.get(url).remove();
        }
        String saveURL = url;

        if (!urlQueue.isEmpty())
            urlQueue.remove();

        // ------------------------------ Adiciona o Url à lista de URLs ou vai busca lo
        // a lista (se ja la estiver) ---------------------------------

        URL urlObject = new URL(url);
        if (!urlList.contains(urlObject)) {

            if(previousUrl != null){
                //Cria um novo objeto url. Coloca o url e o link count a 1
                urlObject = new URL(url,1);

                // Cria uma nova Lista com os URLs a apontarem para este URL
                CopyOnWriteArraySet<String> urlPointingList = urlObject.getUrlPointingToMeList();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);

            } else{
                urlObject = new URL(url, 0);
            }

            // Adiciona o URL à lista de URLs
            urlList.add(urlObject);
        } else {

            int indexOfUrl = urlList.indexOf(urlObject);
            urlObject = urlList.get(indexOfUrl);

            if(previousUrl != null){
                //Incrementar os links a apontar para este url
                urlObject.setLinksCount(urlObject.getLinksCount() + 1);

                // Adiciona o URL para o qual aponta
                CopyOnWriteArraySet<String> urlPointingList = urlObject.getUrlPointingToMeList();
                if (urlPointingList == null)
                    urlPointingList = new CopyOnWriteArraySet<>();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);
            }
            
        }
    

        /*for(URL u: urlList)
            System.out.println(u.getUrl());*/

        CopyOnWriteArraySet<String> indexURLs = new CopyOnWriteArraySet<>();

        // --------------------- Indexar as palavras na pagina do url, fazendo as
        // verificacoes necessarias -------------------------------
        try {
                Document doc = Jsoup.connect(url).get();
                urlObject.setTitle(doc.title()); // Guardar o titulo da pagina
                StringTokenizer tokens = new StringTokenizer(doc.text());
                String currentToken;
                String text = ""; // Vai guardar um excerto de texto da pagina
                int wordsCount = 0;
                while (tokens.hasMoreElements()) {
                    currentToken = tokens.nextToken();

                    indexURLs = index.get(currentToken.toLowerCase());

                    if (indexURLs == null) {
                        indexURLs = new CopyOnWriteArraySet<String>();
                        index.put(currentToken.toLowerCase(), indexURLs);
                    } 

                    indexURLs.add(url);

                    if (wordsCount <= MaxWordsText) {
                        text += currentToken + " ";
                        wordsCount++;
                    }
                }

                urlObject.setText(text);

               // System.out.println(urlObject);

                // ------------------------------ Verificar os links na pagina
                // ----------------------------------
                
                Elements links = doc.select("a[href]");
                for (Element link : links) {

                    url = (link.attr("abs:href"));

                    //System.out.println("APONTA PARA O LINK: " + url);

                    urlQueue.add(url);

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

            //System.out.println("Vai ser vista a pagina: " + urlQueue.peek());

            if (!urlQueue.isEmpty())
                recursiveIndex(index, urlList, urlQueue.peek());

        }
    }