package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import rmiserver.ServerInterface;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Action to get Authorization URL from Facebook API to login
 */
public class FacebookLoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String authorizationUrl, isFacebookLogin;

    @Override
    public String execute() {

        int clientNo = getHeyBean().getClientNo();

        ServerInterface server = getHeyBean().getServer();

        final String secretState = String.valueOf(clientNo);

        System.out.println("Fetching the Authorization URL...");
        try {
            authorizationUrl = server.getAuthorizationUrl(secretState);
            System.out.println("Got the Authorization URL!");
            System.out.println(authorizationUrl);
            System.out.println("Returning success from FacebookLoginAction");
            return SUCCESS;
        } catch (RemoteException e){
            System.out.println("Error connecting to RMI server");
            return ERROR;
        }

    }


    public void setIsFacebookLogin(String isFacebookLogin) {
        this.isFacebookLogin = isFacebookLogin;
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

