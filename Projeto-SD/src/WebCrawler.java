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
    private String word;
    private String url;
    private int HashSetInitialCapacity = 10000;
    private float HashSetLoadFactor = 0.75f;
    private int MaxWordsText = 20;

    public WebCrawler(MulticastServer server, String word, String url){
        super();
        this.server = server;
        this.word = word;
        this.url = url;
    }

    public void run() {
        HashMap<String,HashSet<String>> index = server.getIndex();
        ArrayList<URL> urlLinksCount = server.getUrlLinksCount();
        recursiveIndexWithWord(index, urlLinksCount, word,url);
    }

    private void recursiveIndexWithWord(HashMap<String,HashSet<String>> index, ArrayList<URL> urlLinksCount, String word,String url){
        //Verifica se esta pagina ja foi visitada ou houve alguma atualizacao na pagina
        boolean urlVerified = true;
        

        //Adiciona o Url à lista de URLs
        URL urlObject = new URL(url);
        if(!urlLinksCount.contains(urlObject)){
            urlObject.setLinksCount(0);
            urlLinksCount.add(urlObject);
        }

        //Indexa o url dado pelo administrador, fazendo as verificacoes necessarias
        HashSet<String> indexURLs = new HashSet<>(HashSetInitialCapacity,HashSetLoadFactor);

        indexURLs = index.get(word.toLowerCase());

        if(indexURLs == null){
            indexURLs = new HashSet<String>(HashSetInitialCapacity,HashSetLoadFactor);
            index.put(word.toLowerCase(),indexURLs);
            urlVerified = false;
        }
        else if(!indexURLs.contains(url))
            urlVerified = false;

        indexURLs.add(url);


        //Indexar as palavras na pagina o url, fazendo as verificacoes necessarias
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
            indexURLs = index.get(word.toLowerCase());
            for (Element link : links){ 
                url = (link.attr("abs:href"));
                indexURLs.add(url);

                if(!urlVerified)
                    recursiveIndex(index,urlLinksCount,url);
            }

            } catch (IOException e) { 
                e.printStackTrace(); 
            }
    }

    private void recursiveIndex(HashMap<String,HashSet<String>> index, ArrayList<URL> urlLinksCount,String url){
        boolean urlVerified = true;

        //Adiciona o Url à lista de URLs ou vai busca lo a lista (se ja la estiver)
        URL urlObject = new URL(url);
        if(!urlLinksCount.contains(urlObject)){
            urlObject.setLinksCount(1);
            urlLinksCount.add(urlObject);
        }
        else{
            int indexOfUrl = urlLinksCount.indexOf(urlObject);
            urlObject = urlLinksCount.get(indexOfUrl);
            Integer linksCount = urlObject.getLinksCount();
            if(linksCount == null)
                linksCount = 0;
            linksCount++;
            urlObject.setLinksCount(linksCount);
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
                    recursiveIndex(index,urlLinksCount,url);
            }
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
    }
}