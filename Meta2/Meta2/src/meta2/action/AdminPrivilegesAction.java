package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class AdminPrivilegesAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String userToAdmin = null;
    // To return to jsp
    private String uiMsg = null;

    @Override
    public String execute() {
        System.out.println("User to admin:" + userToAdmin);
        ServerInterface server = getHeyBean().getServer();
        int clientNo = getHeyBean().getClientNo();
        if(userToAdmin != null && !userToAdmin.equals("")) {
            try {
                String msg = server.grantPrivileges(clientNo, userToAdmin);

                if (msg != null) {
                    String[] parameters = msg.split(";;");
                    int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                    System.out.println("Recieved client no: " + receivedClientNo);
                    String status = parameters[2].split("\\|\\|\\|")[1];
                    if (clientNo == receivedClientNo) {
                        if (status.equals("valid")) {
                            System.out.println("Conceded admin privileges to " + userToAdmin + " successfully");
                            uiMsg = "Conceded admin privileges to " + userToAdmin + " successfully.";
                        } else if (status.equals("invalid")) {
                            String errorMsg = parameters[3].split("\\|\\|\\|")[1];
                            System.out.println("ERROR: " + errorMsg + "\nTry again.");
                            uiMsg = errorMsg;
                        }
                    }
                } else {
                    System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                    uiMsg = "Could not grant admin privileges...";
                }
            } catch (RemoteException|NotBoundException|MalformedURLException e) {
                System.out.println("ERROR #10: Something went wrong. Would you mind to try again? :)");
                return ERROR;
            }
        }
        System.out.println("Returning success from adminPrivileges");
        return SUCCESS;
    }

    public String getUserToAdmin() {
        return userToAdmin;
    }

    public void setUserToAdmin(String userToAdmin) {
        this.userToAdmin = userToAdmin;
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
