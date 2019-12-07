import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private boolean isAdmin; // Check if User is Admin
    private ArrayList<String> searchHistory; // List with searches that the user has made

    private boolean isLoggedIn; // Check if user is logged in
    private int clientNo; // Client Number
    private boolean notification; // Check if user needs to be notified that he's been promoted to admin

    
    /** 
     * @param username
     * @param password
     * @param isAdmin
     * @param isLoggedIn
     * @param clientNo
     * @param notification
     * @return 
     */
    public User(String username, String password, boolean isAdmin, boolean isLoggedIn, int clientNo, boolean notification) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isLoggedIn = isLoggedIn;
        this.clientNo = clientNo;
        this.notification = false;
        this.searchHistory = new ArrayList<>();
    }

    
    /** 
     * @param username
     * @return 
     */
    public User(String username) {
        this.username = username;
	}

	
    /** 
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof User){
            User p = (User) o;
            return this.username.equals(p.getUsername());
        } else if(o instanceof String){
            String s = (String) o;
            return this.username.equals(s);
        } else
            return false;
    }

    
    /** 
     * @return String
     */
    public String getUsername() {
        return username;
    }

    
    /** 
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    
    /** 
     * @return String
     */
    public String getPassword() {
        return password;
    }

    
    /** 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    /** 
     * @return boolean
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    
    /** 
     * @param isAdmin
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    
    /** 
     * @return ArrayList<String>
     */
    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }

    
    /** 
     * @param searchHistory
     */
    public void setSearchHistory(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    
    /** 
     * @return boolean
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    
    /** 
     * @param isLoggedIn
     */
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    
    /** 
     * @return int
     */
    public int getClientNo() {
        return clientNo;
    }

    
    /** 
     * @param clientNo
     */
    public void setClientNo(int clientNo) {
        this.clientNo = clientNo;
    }

    
    /** 
     * @return boolean
     */
    public boolean isNotification() {
        return notification;
    }

    
    /** 
     * @param notification
     */
    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    
}