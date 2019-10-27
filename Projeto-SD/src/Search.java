public class Search implements Comparable<Search>{
    
    private String words; // Search made by the user
    private int nSearches; // Number of time the search has been made

    
    /** 
     * @param words
     * @param nSearches
     * @return 
     */
    public Search(String words, int nSearches) {
        this.words = words;
        this.nSearches = nSearches;
    }
    
    
    /** 
     * @param search
     * @return int
     */
    @Override
    public int compareTo(Search search){
        return search.getnSearches() - this.nSearches;
    }

	
    /** 
     * @param o
     * @return boolean
     */
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

    
    /** 
     * @return String
     */
    public String getWords() {
        return words;
    }

    
    /** 
     * @param words
     */
    public void setWords(String words) {
        this.words = words;
    }

    
    /** 
     * @return int
     */
    public int getnSearches() {
        return nSearches;
    }

    
    /** 
     * @param nSearches
     */
    public void setnSearches(int nSearches) {
        this.nSearches = nSearches;
    }

    
}