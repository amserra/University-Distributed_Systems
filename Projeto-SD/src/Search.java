public class Search implements Comparable<Search>{
    
    private String words;
    private int nSearches;

    public Search(String words, int nSearches) {
        this.words = words;
        this.nSearches = nSearches;
    }
    
    @Override
    public int compareTo(Search search){
        return search.getnSearches() - this.nSearches;
    }

	@Override
    public boolean equals(Object o){
        if (o instanceof Search) {
            Search p = (Search) o;
            return this.words.equals(p.getWords());
        } else if (o instanceof String){
            String p = (String) o;
            return this.words.equals(p);
        } else
            return false;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public int getnSearches() {
        return nSearches;
    }

    public void setnSearches(int nSearches) {
        this.nSearches = nSearches;
    }

    
}