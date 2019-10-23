import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler extends Thread {

    private MulticastServer server;
    private String url;
    private int HashSetInitialCapacity = 10000;
    private float HashSetLoadFactor = 0.75f;
    private int MaxWordsText = 20;

    private Queue<String> urlQueue = new LinkedList<>();

    private HashMap<String,Queue<String>> linkPointing = new HashMap<>();

    public WebCrawler(MulticastServer server, String url) {
        super();
        this.server = server;
        this.url = url;
    }

    public void run() {
        HashMap<String, HashSet<String>> index = server.getIndex();
        CopyOnWriteArrayList<URL> urlList = server.getUrlList();
        recursiveIndex(index, urlList, url);
    }

    private void recursiveIndex(HashMap<String, HashSet<String>> index, CopyOnWriteArrayList<URL> urlList, String url) {

        String previousUrl = null;

        if(linkPointing.containsKey(url)){
            previousUrl = linkPointing.get(url).peek();
            linkPointing.get(url).remove();
        }
        //System.out.println(previousUrl);

        String saveURL = url;

        //System.out.println(saveURL);

        if (!urlQueue.isEmpty())
            urlQueue.remove();

        // ------------------------------ Adiciona o Url à lista de URLs ou vai busca lo
        // a lista (se ja la estiver) ---------------------------------
        URL urlObject = new URL(url);
        if (!urlList.contains(urlObject)) {
            if(previousUrl != null){
                // Atualiza a contagem de links a apontar para 1, visto que é o primeiro
                urlObject.setLinksCount(1);

                // Cria uma nova Lista com os URLs a apontarem para este URL
                ArrayList<String> urlPointingList = new ArrayList<>();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);
            }

            // Adiciona o URL à lista de URLs
            urlList.add(urlObject);
        } else if (previousUrl != null){
            int indexOfUrl = urlList.indexOf(urlObject);
            urlObject = urlList.get(indexOfUrl);

            // Vê se tem a contagem de links a apontar
            Integer linksCount = urlObject.getLinksCount();
            if (linksCount == null)
                linksCount = 0;
            linksCount++;

            // Adiciona o URL para o qual aponta
            ArrayList<String> urlPointingList = urlObject.getUrlPointingToMeList();
            if (urlPointingList == null)
                urlPointingList = new ArrayList<>();
            urlPointingList.add(previousUrl);
            urlObject.setUrlPointingToMeList(urlPointingList);

            urlObject.setLinksCount(linksCount);
        }
        

        /*for(URL u: urlList)
            System.out.println(u.getUrl());*/

        HashSet<String> indexURLs = new HashSet<>();

        // --------------------- Indexar as palavras na pagina do url, fazendo as
        // verificacoes necessarias -------------------------------
        try {
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
                        indexURLs = new HashSet<String>(HashSetInitialCapacity, HashSetLoadFactor);
                        index.put(currentToken.toLowerCase(), indexURLs);
                    } 

                    indexURLs.add(url);

                    if (wordsCount <= MaxWordsText) {
                        text += currentToken + " ";
                        wordsCount++;
                    }
                }

                urlObject.setText(text);

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
            }

            //System.out.println("Vai ser vista a pagina: " + urlQueue.peek());

            if (!urlQueue.isEmpty())
                recursiveIndex(index, urlList, urlQueue.peek());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}