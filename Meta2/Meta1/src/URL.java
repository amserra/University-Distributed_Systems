import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

public class URL implements Comparable<URL>, Serializable {

    private static final long serialVersionUID = 1L;
    private String url; // URL of the page
    private String title; // Title of the page
    private String text; // Part of the text of the page
    private String lang; //Language of url
    private Integer linksCount; // Number of links pointing to this page
    private CopyOnWriteArraySet<String> urlPointingToMeList; // Links pointing to this page

    
    /** 
     * @param url
     * @return 
     */
    public URL(String url) {
        this.url = url;
        urlPointingToMeList = new CopyOnWriteArraySet<>();
    }

    
    /** 
     * @param url
     * @param linksCount
     * @return 
     */
    public URL(String url, Integer linksCount) {
        this.url = url;
        this.linksCount = linksCount;
        urlPointingToMeList = new CopyOnWriteArraySet<>();
    }


    
    /** 
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof URL) {
            URL p = (URL) o;
            return this.url.equals(p.getUrl());
        } else if (o instanceof String) {
            String s = (String) o;
            return this.url.equals(s);
        } else
            return false;
    }

    
    /** 
     * @param url
     * @return int
     */
    @Override
    public int compareTo(URL url) {
        if (url != null && url.getLinksCount() != null && this.linksCount != null) {
            int compareLinks = url.getLinksCount();

            /* For Descending order do like this */
            return compareLinks - this.linksCount;
        } else
            return 0;
    }

    
    /** 
     * @return String
     */
    public String getUrl() {
        return url;
    }

    
    /** 
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    
    /** 
     * @return String
     */
    public String getTitle() {
        return title;
    }

    
    /** 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getLang() {
        return lang;
    }

    /**
     *
     * @param lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @return String
     */
    public String getText() {
        return text;
    }

    
    /** 
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    
    /** 
     * @return Integer
     */
    public Integer getLinksCount() {
        return linksCount;
    }

    
    /** 
     * @param linksCount
     */
    public void setLinksCount(Integer linksCount) {
        this.linksCount = linksCount;
    }

    
    /** 
     * @return CopyOnWriteArraySet<String>
     */
    public CopyOnWriteArraySet<String> getUrlPointingToMeList() {
        return urlPointingToMeList;
    }

    
    /** 
     * @param urlPointingToMeList
     */
    public void setUrlPointingToMeList(CopyOnWriteArraySet<String> urlPointingToMeList) {
        this.urlPointingToMeList = urlPointingToMeList;
    }

    
    /** 
     * @return String
     */
    public String toString() {
        return "URL: " + url + "\nTitle: " + title + "\nText: " + text + "\nNumero de referencias: " + linksCount;
    }
}