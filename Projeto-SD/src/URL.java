public class URL implements Comparable<URL>{

    private String url;
    private String title;
    private String text;
    private Integer linksCount;




    public URL(String url) {
        this.url = url;
	}

	@Override
    public boolean equals(Object o){
        if(o instanceof URL){
            URL p = (URL) o;
            return this.url.equals(p.getUrl());
        } else if(o instanceof String){
            String s = (String) o;
            return this.url.equals(s);
        } else
            return false;
    }

    @Override
    public int compareTo(URL url) {
        int compareLinks = url.getLinksCount();

        /* For Descending order do like this */
        return compareLinks-this.linksCount;
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

    public String toString(){
        return "URL: " + url + "\nTitle: " +title+ "\nText: " + text + "\nNumero de referencias: " + linksCount;
    }

    
}