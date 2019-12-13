package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import rmiserver.ServerInterface;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletRequest;


public class ExchangeAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String uiMsg = null, notificationMsg = null, username = null, name = null;


    @Override
    public String execute() throws IOException, InterruptedException, ExecutionException, ParseException {

        System.out.println("Let's trade code for token!");

        HttpServletRequest request = ServletActionContext.getRequest();
        String url = request.getHeader("referer");

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

        System.out.println(getHeyBean().getUsername());

        JSONObject json = server.exchangeCodeForToken(code, clientNo, getHeyBean().getUsername());
        System.out.println(json);

        String msg = (String) json.get("msg");
        username = (String) json.get("id");
        name = (String) json.get("name");

        if (msg != null) {

            String[] parameters = msg.split(";;");
            int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);

            if (clientNo == receivedClientNo) {

                if(getHeyBean().getUsername() == null || getHeyBean().getUsername().equals("")) {

                    String usr = parameters[3].split("\\|\\|\\|")[1];
                    boolean isAdmin = Boolean.parseBoolean(parameters[4].split("\\|\\|\\|")[1]);
                    if (isAdmin) {
                        this.getHeyBean().setTypeOfClient("admin");
                        session.put("typeOfClient", "admin");
                    } else {
                        this.getHeyBean().setTypeOfClient("user");
                        session.put("typeOfClient", "user");
                    }

                    System.out.println("Login successful. Welcome " + name + "\n");

                    uiMsg = "Login successful. Welcome " + name;
                    boolean notification = Boolean.parseBoolean(parameters[5].split("\\|\\|\\|")[1]);
                    if (notification) {
                        System.out.println("Notification: You have been promoted to admin!");
                        notificationMsg = "You have been promoted to admin!";
                    }

                    this.getHeyBean().setUsername(this.username);
                    session.put("username", username);

                    this.getHeyBean().setName(this.name);
                    session.put("name", this.name);

                    return SUCCESS;
                } else{

                    String status = parameters[2].split("\\|\\|\\|")[1];

                    if(status.equals("valid")){

                        uiMsg = "Facebook associated successfully!";

                        this.getHeyBean().setUsername(this.username);
                        session.put("username", username);

                        this.getHeyBean().setName(this.name);
                        session.put("name", this.name);
                    } else{
                        uiMsg = "That Facebook account is already linked to other account.";
                    }

                    return LOGIN;
                }

            }
        } else {
            System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");

            return ERROR;
        }

        return SUCCESS;

    }

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean"))
            this.setHeyBean(new HeyBean());
        return (HeyBean) session.get("heyBean");
    }

    public String getUiMsg() {
        return uiMsg;
    }

    public void setUiMsg(String uiMsg) {
        this.uiMsg = uiMsg;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String notificationMsg) {
        this.notificationMsg = notificationMsg;
    }


    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}

