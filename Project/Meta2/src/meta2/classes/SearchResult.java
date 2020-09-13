package meta2.classes;

/**
 * Class with information about a specific result from a search
 */
public class SearchResult {
    private String title;
    private String url;
    private String text;
    private String lang;

    public SearchResult(String title, String url, String text, String lang) {
        this.title = title;
        this.url = url;
        this.text = text;
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
