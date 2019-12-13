package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class SearchHistoryAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    // To return to jsp
    private ArrayList<String> searchHistory;
    private String uiMsg = null;

    @Override
    public String execute() {
            ServerInterface server = getHeyBean().getServer();
            int clientNo = getHeyBean().getClientNo();
            String username = getHeyBean().getUsername();
        try {
            String msg = server.searchHistory(clientNo, username);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                int searchCount = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);

                if (clientNo == receivedClientNo && searchCount != 0) {
                    int count = 0;
                    int startIndex = 3;
                    // Starts at index 3
                    searchHistory = new ArrayList<String>();
                    for (int i = startIndex; i < searchCount + startIndex; i++) {
                        count++;
                        System.out.println("Url " + count + ": " + parameters[i].split("\\|\\|\\|")[1]);
                        searchHistory.add(parameters[i].split("\\|\\|\\|")[1]);
                    }
                    uiMsg = "You have made " + count + " searches. Showing them all.";
                } else if (clientNo == receivedClientNo && searchCount == 0) {
                    System.out.println("You haven't searched for anything yet!");
                    uiMsg = "You haven't searched for anything yet!";
                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
            }
        } catch (RemoteException e) {
            return ERROR;
        }
        System.out.println("Returning success from search history");
        return SUCCESS;
    }

    public String getUiMsg() {
        return uiMsg;
    }

    public void setUiMsg(String uiMsg) {
        this.uiMsg = uiMsg;
    }

    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean"))
            this.setHeyBean(new HeyBean());
        return (HeyBean) session.get("heyBean");
    }

    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
