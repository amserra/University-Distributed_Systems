import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

public class URL implements Comparable<URL>, Serializable {

    private static final long serialVersionUID = 1L;
    private String url;
    private String title;
    private String text;
    private Integer linksCount;
    private CopyOnWriteArraySet<String> urlPointingToMeList;

    public URL(String url) {
        this.url = url;
    }

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

    @Override
    public int compareTo(URL url) {
        if (url != null && url.getLinksCount() != null && this.linksCount != null) {
            int compareLinks = url.getLinksCount();

            /* For Descending order do like this */
            return compareLinks - this.linksCount;
        } else
            return 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLinksCount() {
        return linksCount;
    }

    public void setLinksCount(Integer linksCount) {
        this.linksCount = linksCount;
    }

    public CopyOnWriteArraySet<String> getUrlPointingToMeList() {
        return urlPointingToMeList;
    }

    public void setUrlPointingToMeList(CopyOnWriteArraySet<String> urlPointingToMeList) {
        this.urlPointingToMeList = urlPointingToMeList;
    }

    public String toString() {
        return "URL: " + url + "\nTitle: " + title + "\nText: " + text + "\nNumero de referencias: " + linksCount;
    }
}