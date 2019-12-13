package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class IndexNewUrlAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String indexUrl = null;
    // To return to jsp
    private String uiMsg = null;

    @Override
    public String execute() {
        System.out.println("URL:"+indexUrl);
        ServerInterface server = getHeyBean().getServer();
        int clientNo = getHeyBean().getClientNo();
        if(indexUrl != null && !indexUrl.equals("")) {
            try {
                String msg = server.indexNewURL(clientNo, indexUrl);
                if (msg != null) {
                    String[] parameters = msg.split(";;");
                    int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);

                    if (clientNo == receivedClientNo) {
                        System.out.println("Started indexing the requested url!");
                        uiMsg = "Started indexing "+indexUrl;
                    }
                } else {
                    System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                    uiMsg = "Could not index the url...";
                }
            } catch (RemoteException e) {
                System.out.println("ERROR #8: Something went wrong. Would you mind to try again? :)");
                return ERROR;
            }
        }
        System.out.println("Returning success from indexNewUrl");
        return SUCCESS;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    public String getUiMsg() {
        return uiMsg;
    }

    public void setUiMsg(String uiMsg) {
        this.uiMsg = uiMsg;
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
