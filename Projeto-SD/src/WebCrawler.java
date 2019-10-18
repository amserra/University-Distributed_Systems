import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.io.IOException;

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

    public WebCrawler(MulticastServer server, String word, String url){
        super();
        this.server = server;
        this.word = word;
        this.url = url;
    }

    public void run() {
        HashMap<String,HashSet<String>> index = server.getIndex();
        recursiveIndex(index,word,url);
    }

    private void recursiveIndex(HashMap<String,HashSet<String>> index, String word,String url){
        boolean urlVerified = true;

        HashSet<String> indexURLs = new HashSet<>();

        if(word != null){
            indexURLs = index.get(word.toLowerCase());

            if(indexURLs == null){
                indexURLs = new HashSet<String>(HashSetInitialCapacity,HashSetLoadFactor);
                index.put(word.toLowerCase(),indexURLs);
                urlVerified = false;
            }
            else if(!indexURLs.contains(url))
                urlVerified = false;

            indexURLs.add(url);

            System.out.println("Word: " + word.toLowerCase() + " URL: " + url);
        }

        System.out.println(url);
        try { 
            Document doc = Jsoup.connect(url).get(); 
            //System.out.println("Title: " + doc.title()); ------------- GET TITLE DA PAGINA --------------
            StringTokenizer tokens = new StringTokenizer(doc.text()); 
            String currentToken;
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

                System.out.println("Word: " + currentToken.toLowerCase() + " URL: " + index.get(currentToken.toLowerCase()));

            }
            Elements links = doc.select("a[href]");
            if(word != null) 
                indexURLs = index.get(word.toLowerCase());
            for (Element link : links){ 
                System.out.println(link.attr("abs:href"));
                if(word != null)
                    indexURLs.add(link.attr("abs:href"));
                if(!urlVerified)
                    recursiveIndex(index,null,link.attr("abs:href"));
                //System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n"); 
                //System.out.println("Word: " + word.toLowerCase() + " URLs: " + index.get(word.toLowerCase()));
            }
            //System.out.println("Word: " + word.toLowerCase() + " URLs: " + index.get(word.toLowerCase()));
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
    }

}