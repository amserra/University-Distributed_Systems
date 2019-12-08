/**
 * Raul Barbosa 2014-11-07
 */
package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import meta2.model.HeyBean;

public class SearchAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String searchTerms = null, password = null;

	@Override
	public String execute() {
		// any username is accepted without confirmation (should check using RMI)
		if(this.searchTerms != null && !searchTerms.equals("")) {
			this.getHeyBean().setUsername(this.searchTerms);
			this.getHeyBean().setPassword(this.password);
			System.out.println("Putting in session");
			session.put("username", searchTerms);
			session.put("loggedin", true); // this marks the user as logged in
			System.out.println("Returning success");
			return SUCCESS;
		}
		else {
			System.out.println("Going to login again...");
			return LOGIN;
		}
	}
	
	public void setSearchTerms(String username) {
		this.searchTerms = username; // will you sanitize this input? maybe use a prepared statement?
		System.out.println("Setting username...");
	}

	public void setPassword(String password) {
		this.password = password; // what about this input?
		System.out.println("Setting password...");
	}
	
	public HeyBean getHeyBean() {
		if(!session.containsKey("heyBean"))
			this.setHeyBean(new HeyBean());
		System.out.println("didn't create bean");
		return (HeyBean) session.get("heyBean");
	}

	public void setHeyBean(HeyBean heyBean) {
		this.session.put("heyBean", heyBean);
		System.out.println("Created bean!");
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
