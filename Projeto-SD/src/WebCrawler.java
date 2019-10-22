import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler extends Thread{

    private MulticastServer server;
    private String url;
    private int HashSetInitialCapacity = 10000;
    private float HashSetLoadFactor = 0.75f;
    private int MaxWordsText = 20;

    public WebCrawler(MulticastServer server,  String url){
        super();
        this.server = server;
        this.url = url;
    }

    public void run() {
        HashMap<String,HashSet<String>> index = server.getIndex();
        ArrayList<URL> urlList = server.getUrlList();
        recursiveIndex(index, urlList, url, null);
    }

    private void recursiveIndex(HashMap<String,HashSet<String>> index, ArrayList<URL> urlList,String url, String previousUrl){
        boolean urlVerified = true;

        String saveURL = url;


        //Adiciona o Url à lista de URLs ou vai busca lo a lista (se ja la estiver)
        URL urlObject = new URL(url);
        if(previousUrl != null){
            if(!urlList.contains(urlObject)){
                //Atualiza a contagem de links a apontar para 1, visto que é o primeiro
                urlObject.setLinksCount(1);

                //Cria uma nova Lista com os URLs a apontarem para este URL
                ArrayList<String> urlPointingList = new ArrayList<>();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);

                //Adiciona o URL à lista de URLs
                urlList.add(urlObject);
            }
            else{
                int indexOfUrl = urlList.indexOf(urlObject);
                urlObject = urlList.get(indexOfUrl);

                //Vê se tem a contagem de links a apontar
                Integer linksCount = urlObject.getLinksCount();
                if(linksCount == null)
                    linksCount = 0;
                linksCount++;

                //Adiciona o URL para o qual aponta
                ArrayList<String> urlPointingList = urlObject.getUrlPointingToMeList();
                if(urlPointingList == null)
                    urlPointingList = new ArrayList<>();
                urlPointingList.add(previousUrl);
                urlObject.setUrlPointingToMeList(urlPointingList);

                urlObject.setLinksCount(linksCount);
            }
        }

        HashSet<String> indexURLs = new HashSet<>();

        //Indexar as palavras na pagina do url, fazendo as verificacoes necessarias
        try { 
            Document doc = Jsoup.connect(url).get(); 
            urlObject.setTitle(doc.title());  //Guardar o titulo da pagina
            StringTokenizer tokens = new StringTokenizer(doc.text()); 
            String currentToken;
            String text = ""; //Vai guardar um excerto de texto da pagina
            int wordsCount = 0;
            while (tokens.hasMoreElements()) {
                currentToken = tokens.nextToken();

                indexURLs = index.get(currentToken.toLowerCase());

                if(indexURLs == null){
                    indexURLs = new HashSet<String>(HashSetInitialCapacity,HashSetLoadFactor);
                    index.put(currentToken.toLowerCase(), indexURLs);
                    urlVerified = false;
                }
                else if(!indexURLs.contains(url))
                    urlVerified = false;
                    
                indexURLs.add(url);

                if(wordsCount <= MaxWordsText){
                    text += currentToken + " ";
                    wordsCount++;
                }
            }

            urlObject.setText(text);


            Elements links = doc.select("a[href]");
            for (Element link : links){ 
                url = (link.attr("abs:href"));

                if(!urlVerified)
                    recursiveIndex(index,urlList,url,saveURL);
            }
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
    }
}