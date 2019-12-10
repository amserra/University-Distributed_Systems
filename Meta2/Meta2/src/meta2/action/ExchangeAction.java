package meta2.action;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONObject;
import rmiserver.ServerInterface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ExchangeAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String url = null, uiMsg = null, notificationMsg = null, username = null, name = null;


    @Override
    public String execute() throws IOException{

        System.out.println("Let's trade code for token!");
        System.out.println(url);

        int clientNo = getHeyBean().getClientNo();

        ServerInterface server = getHeyBean().getServer();

        URL urlLink = new URL(url);

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = urlLink.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }

        String code = query_pairs.get("code");


        JSONObject json = server.exchangeCodeForToken(code, clientNo);
        System.out.println(json);

        String msg = (String) json.get("msg");
        username = (String) json.get("id");
        name = (String) json.get("name");

        if (msg != null) {

            String[] parameters = msg.split(";;");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
            String status = parameters[2].split("\\|\\|\\|")[1];

            if (clientNo == receivedClientNo) {

                String usr = parameters[3].split("\\|\\|\\|")[1];
                boolean isAdmin = Boolean.parseBoolean(parameters[4].split("\\|\\|\\|")[1]);
                if (isAdmin) {
                    this.getHeyBean().setTypeOfClient("admin");
                    session.put("typeOfClient","admin");
                }
                else {
                    this.getHeyBean().setTypeOfClient("user");
                    session.put("typeOfClient","user");
                }

                System.out.println("Login successful. Welcome " + usr + "\n");
                uiMsg = "Login successful. Welcome " + usr;
                boolean notification = Boolean.parseBoolean(parameters[5].split("\\|\\|\\|")[1]);
                if (notification) {
                    System.out.println("Notification: You have been promoted to admin!");
                    notificationMsg = "You have been promoted to admin!";
                }

                this.getHeyBean().setUsername(this.username);
                session.put("username", username);

                this.getHeyBean().setName(this.name);
                session.put("name", this.name);

                System.out.println(this.getHeyBean().getName());

            }
        } else {
            System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
        }



        return SUCCESS;

    }

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean"))
            this.setHeyBean(new HeyBean());
        return (HeyBean) session.get("heyBean");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}

