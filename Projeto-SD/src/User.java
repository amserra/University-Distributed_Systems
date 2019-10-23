import java.util.ArrayList;

public class User {

    private String username;
    private String password;
    private boolean isAdmin;
    private ArrayList<String> searchHistory;

    private boolean isLoggedIn;
    private int clientNo;
    private boolean notification;

    public User(String username, String password, boolean isAdmin, boolean isLoggedIn, int clientNo, boolean notification) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isLoggedIn = isLoggedIn;
        this.clientNo = clientNo;
        this.notification = false;
        this.searchHistory = new ArrayList<>();
    }

    public User(String username) {
        this.username = username;
	}

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public int getClientNo() {
        return clientNo;
    }

    public void setClientNo(int clientNo) {
        this.clientNo = clientNo;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    
}