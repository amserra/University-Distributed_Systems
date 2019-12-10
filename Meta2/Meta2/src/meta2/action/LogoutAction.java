/**
 * Raul Barbosa 2014-11-07
 */
package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class LogoutAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() {
        ServerInterface server = getHeyBean().getServer();
        try {
            server.logout(getHeyBean().getClientNo(),getHeyBean().getUsername());
            this.getHeyBean().setTypeOfClient("anonymous");
            this.getHeyBean().setUsername("");
            session.put("typeOfClient","anonymous");
            session.put("username", "");
        } catch (RemoteException e) {
            return ERROR;
        }
        System.out.println("Returning success from LogoutAction");
        return SUCCESS;

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
