/**
 * Raul Barbosa 2014-11-07
 */
package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import meta2.model.HeyBean;
import org.json.simple.parser.ParseException;
import rmiserver.ServerInterface;

public class TranslateAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    // To return to jsp
    private ArrayList<SearchResult> searchResults;
    private String text = null, title = null;

    @Override
    public String execute() throws IOException, ParseException {
        System.out.println(text);
        System.out.println(title);

        ServerInterface server = getHeyBean().getServer();

        String textTranslate = server.translateText(text);

        String titleTranslate = server.translateText(title);

        System.out.println(textTranslate);

        System.out.println(titleTranslate);

       return SUCCESS;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean"))
            this.setHeyBean(new HeyBean());
        return (HeyBean) session.get("heyBean");
    }

    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
