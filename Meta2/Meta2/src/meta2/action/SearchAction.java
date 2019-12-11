/**
 * Raul Barbosa 2014-11-07
 */
package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

public class SearchAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String searchTerms = null;
	// To return to jsp
	private ArrayList<SearchResult> searchResults;
	private String uiMsg = null;

	@Override
	public String execute() {
		System.out.println("SearchTerms:"+searchTerms);
		if(this.searchTerms != null && !searchTerms.equals("")) {
			ServerInterface server = getHeyBean().getServer();
			int clientNo = getHeyBean().getClientNo();
			String username = getHeyBean().getUsername();
			try {
				String result = server.search(clientNo,username,String.join(" ", searchTerms));
				if (result != null) {
					String[] parameters = result.split(";;");
					int receivedClientNo = Integer.parseInt(parameters[1].split("\\|\\|\\|")[1]);
					int numOfURLs = Integer.parseInt(parameters[2].split("\\|\\|\\|")[1]);

					if (clientNo == receivedClientNo) {
						if (numOfURLs == 0) {
							System.out.println("Nothing came up! Ask an admin to index more pages.");
							uiMsg = "Nothing came up! Ask an admin to index more pages.";
						} else {
							int count = 0;
							// Starts at index 3
							int j = 3;
							int max;
							if (numOfURLs < 100)
								max = numOfURLs;
							else
								max = 100;

							System.out.println("Found " + numOfURLs + " results. Showing the first " + max);
							uiMsg = "Found " + numOfURLs + " results. Showing the first " + max;
							searchResults = new ArrayList<SearchResult>();
							for (int i = 0; i < max; i++) {
								count++;
								System.out.println("\n" + count + "\n");
								String title = parameters[j++].split("\\|\\|\\|")[1];
								String url = parameters[j++].split("\\|\\|\\|")[1];
								String text = parameters[j++].split("\\|\\|\\|")[1];
								// Lang aqui
								System.out.println("Title: " + title);
								System.out.println("Url: " + url);
								System.out.println("Text: " + text);
								// Lang aqui
								searchResults.add(new SearchResult(title,url,text,"PT"));
							}
						}
					}
				} else {
					System.out.println("TIMEOUT: Could not recieve info in 30s. Try again\n");
				}
			} catch (RemoteException e) {
				return ERROR;
			}
			System.out.println("Returning success");
			return SUCCESS;
		}
		else {
			System.out.println("Going to index...");
			return NONE;
		}
	}

	public String getSearchTerms() {
		return searchTerms;
	}

	public String getUiMsg() {
		return uiMsg;
	}

	public void setUiMsg(String uiMsg) {
		this.uiMsg = uiMsg;
	}

	public void setSearchTerms(String username) { this.searchTerms = username; }

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
