package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import rmiserver.ServerInterface;

import java.rmi.RemoteException;
import java.util.Map;


public class FacebookShareAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String authorizationUrl = null;

    @Override
    public String execute() {

        String searchTerms = (String) session.get("search");

        System.out.println(searchTerms);

        String link = "https://local.ucbusca.com:8443/Meta2/redirectSearch.jsp?search=" + searchTerms;

        System.out.println(link);

        authorizationUrl = "https://www.facebook.com/dialog/feed?app_id=1517623451722247&display=popup";

        authorizationUrl += "&link="  + link + "&redirect_uri=" + link;

        return SUCCESS;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
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

