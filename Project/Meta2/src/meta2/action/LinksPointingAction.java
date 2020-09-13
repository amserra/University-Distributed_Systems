package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import rmiserver.ServerInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Action that gets links pointing to a given URL
 */
public class LinksPointingAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String url = null;
    // To return to jsp
    private ArrayList<String> linksPointing;
    private String uiMsg = null;

    @Override
    public String execute() {
        System.out.println("URL:"+url);
        ServerInterface server = getHeyBean().getServer();
        int clientNo = getHeyBean().getClientNo();
        if(url != null && !url.equals("")) {
            try {
                String msg = server.linksPointing(clientNo, url);

                if (msg != null) {
                    String[] parameters = msg.split(";;");
                    int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                    int numOfLinks = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);
                    if (clientNo == receivedClientNo && numOfLinks != 0) {
                        int count = 0;
                        int startIndex = 3;
                        // Starts at index 3
                        linksPointing = new ArrayList<>();
                        for (int i = startIndex; i < numOfLinks + startIndex; i++) {
                            count++;
                            System.out.println("Link " + count + ": " + parameters[i].split("\\|\\|\\|")[1]);
                            linksPointing.add(parameters[i].split("\\|\\|\\|")[1]);
                        }
                        uiMsg = "There are " + count + " links pointing to this url. Showing them all.";
                    } else if (clientNo == receivedClientNo && numOfLinks == 0) {
                        System.out.println("Link doesn't have any pages connected to it yet!");
                        uiMsg = "Link doesn't have any pages connected to it yet!";
                    }
                } else {
                    System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                }
            } catch (RemoteException e) {
                System.out.println("ERROR #7: Something went wrong. Would you mind to try again? :)");
                return ERROR;
            }
        }
        System.out.println("Returning success from search history");
        return SUCCESS;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUiMsg() {
        return uiMsg;
    }

    public void setUiMsg(String uiMsg) {
        this.uiMsg = uiMsg;
    }

    public ArrayList<String> getLinksPointing() {
        return linksPointing;
    }

    public void setLinksPointing(ArrayList<String> linksPointing) {
        this.linksPointing = linksPointing;
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
