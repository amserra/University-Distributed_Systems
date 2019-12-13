package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class RtsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    // To return to jsp
    private ArrayList<String> mostRelevant;
    private ArrayList<String> mostSearched;
    private HashMap<String,String> multicastServers;

    @Override
    public String execute() {
        System.out.println("RTS action");
        ServerInterface server = getHeyBean().getServer();
        int clientNo = getHeyBean().getClientNo();
        try {
            String msg = server.realTimeStatistics(clientNo);

            if (msg != null) {
                String[] parameters = msg.split(";;");
                int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
                System.out.println("Recieved client no: " + receivedClientNo);
                if (clientNo == receivedClientNo) {
                    System.out.println("Started receiving updates...");
                    arrangeInfo(parameters);
                }
            } else {
                System.out.println("TIMEOUT: Could not recieve info in 30s. Returning to main menu\n");
            }
        } catch (RemoteException e) {
            System.out.println("ERROR #9: Something went wrong. Returning to main menu");
            return ERROR;
        }
        System.out.println("Returning success from search history");
        return SUCCESS;
    }

    public void arrangeInfo(String[] parameters) {
        int cont = 1;
        for (int i = 2; i < parameters.length; i++, cont++) {
            if (i == 2) {
                cont = 1;
                System.out.println("\nTop 10 - Most relevant pages:\n");
            } else if (i == 12) {
                cont = 1;
                System.out.println("\nTop 10 - Most searched terms:\n");
            } else if (i == 22) {
                cont = 1;
                System.out.println("\nActive multicast servers:\n");
            }

            if (i < 22) {
                System.out.println(cont + ". " + parameters[i].split("\\|\\|\\|")[1]);
            } else {
                System.out.println(cont + ". Ip: " + parameters[i++].split("\\|\\|\\|")[1] + " Port: "
                        + parameters[i].split("\\|\\|\\|")[1]);
            }
            // novo

            if(i < 12) mostRelevant.add(parameters[i].split("\\|\\|\\|")[1]);
            else if(i < 22) mostSearched.add(parameters[i].split("\\|\\|\\|")[1]);
            else multicastServers.put(parameters[i].split("\\|\\|\\|")[1],parameters[i].split("\\|\\|\\|")[1]); // isto n ta bem
        }
    }

    public ArrayList<String> getMostRelevant() {
        return mostRelevant;
    }

    public void setMostRelevant(ArrayList<String> mostRelevant) {
        this.mostRelevant = mostRelevant;
    }

    public ArrayList<String> getMostSearched() {
        return mostSearched;
    }

    public void setMostSearched(ArrayList<String> mostSearched) {
        this.mostSearched = mostSearched;
    }

    public HashMap<String, String> getMulticastServers() {
        return multicastServers;
    }

    public void setMulticastServers(HashMap<String, String> multicastServers) {
        this.multicastServers = multicastServers;
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
