package meta2.action;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import rmiserver.ServerInterface;

import java.util.Map;


public class FacebookLoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String authorizationUrl;

    @Override
    public String execute() {

        int clientNo = getHeyBean().getClientNo();

        final String clientId = "1517623451722247";
        final String clientSecret = "6e77bf3115c0707179b8ec299ee6d7e4";
        final String secretState = String.valueOf(clientNo);

        System.out.println(secretState);

        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback("http://localhost:8080/Meta2/exchangeTokenForCode.jsp")
                .build(FacebookApi.instance());

        System.out.println("Fetching the Authorization URL...");
        authorizationUrl = service.getAuthorizationUrl(secretState);
        System.out.println("Got the Authorization URL!");
        System.out.println(authorizationUrl);
        System.out.println("Returning success from FacebookLoginAction");
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

