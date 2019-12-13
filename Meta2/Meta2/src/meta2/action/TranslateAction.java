package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import meta2.model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import rmiserver.ServerInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TranslateAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    // To return to jsp
    private ArrayList<SearchResult> searchResults;
    private String text = null, title = null, result = null, textTranslate = null, titleTranslate = null;

    @Override
    public String execute() throws IOException, ParseException {

        ServerInterface server = getHeyBean().getServer();

        textTranslate = server.translateText(text);

        titleTranslate = server.translateText(title);

        System.out.println(textTranslate);

        System.out.println(titleTranslate);

        JSONObject json = new JSONObject();
        json.put("title", titleTranslate);
        json.put("text", textTranslate);

        result = json.toString();

       return SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
