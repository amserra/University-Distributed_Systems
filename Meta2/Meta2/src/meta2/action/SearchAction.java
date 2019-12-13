package meta2.action;

import com.opensymphony.xwork2.ActionSupport;
import meta2.classes.SearchResult;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import meta2.model.HeyBean;
import rmiserver.ServerInterface;

import javax.servlet.http.HttpServletRequest;

public class SearchAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String searchTerms = null;
	// To return to jsp
	private ArrayList<SearchResult> searchResults;
	private String uiMsg = null, redirect = null;

	@Override
	public String execute() throws MalformedURLException, UnsupportedEncodingException {
		if(redirect.equals("true")){
			HttpServletRequest request = ServletActionContext.getRequest();
			String url = request.getHeader("referer");

			URL urlLink = new URL(url);

			Map<String, String> query_pairs = new LinkedHashMap<String, String>();
			String query = urlLink.getQuery();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
			searchTerms = query_pairs.get("search");
		}

		System.out.println("SearchTerms:"+searchTerms);
		if(this.searchTerms != null && !searchTerms.equals("")) {
			ServerInterface server = getHeyBean().getServer();
			int clientNo = getHeyBean().getClientNo();
			String username = getHeyBean().getUsername();
			try {
				String result = server.search(clientNo,username,String.join(" ", searchTerms));
				if (result != null) {

					if(getHeyBean().getName() != null)
						session.put("search",searchTerms);
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
								String lang = parameters[j++].split("\\|\\|\\|")[1];

								System.out.println("Title: " + title);
								System.out.println("Url: " + url);
								System.out.println("Text: " + text);
								System.out.println("Lang: " + lang);

								searchResults.add(new SearchResult(title,url,text,lang));
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

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
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
