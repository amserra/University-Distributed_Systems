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

public class AuthAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null, type = null;

    @Override
    public String execute() {
        System.out.println("type:"+type);
        System.out.println("username:"+username);
        System.out.println("password:"+password);

        if(this.username != null && !username.equals("")) {
            ServerInterface server = getHeyBean().getServer();
            int clientNo = getHeyBean().getClientNo();
            boolean isLogin = type.equals("login");
            try {
                String msg = server.authentication(clientNo,isLogin,username,password);
                if (msg != null) {

                    String[] parameters = msg.split(";;");
                    int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                    String status = parameters[2].split("\\|\\|\\|")[1];

                    if (clientNo == receivedClientNo) {
                        if (status.equals("valid")) {
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

                            if (isLogin) {
                                System.out.println("Login successful. Welcome " + usr + "\n");
                                boolean notification = Boolean.parseBoolean(parameters[5].split("\\|\\|\\|")[1]);
                                if (notification)
                                    System.out.println("Notification: You have been promoted to admin!");
                            } else {
                                System.out.println("Register successful. Welcome " + usr + "\n");
                            }

                            //userUI.mainMenu();
                            this.getHeyBean().setUsername(this.username);
                            session.put("username", username);



                        } else if (status.equals("invalid")) {
                            if (isLogin) {
                                System.out.println("Login failed. Try again.\n");
                                //userUI.login();
                            } else {
                                System.out.println("Register failed. Try again.\n");
                                //userUI.register();
                            }
                        }
                    }
                } else {
                    System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
                    //userUI.mainMenu();
                }
            } catch (RemoteException e) {
                return ERROR;
            }

            System.out.println("Returning success on AuthAction");
            return SUCCESS;
        }
        else {
            System.out.println("Going to "+type+" again...");
            return NONE;
        }
    }

    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
    }

    public void setType(String type) {
        this.type = type;
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
