/**
 * 
 */
package od.model.outcomes;

import java.util.Set;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class LineItem extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String type;
	private String title;
	private String context;
	private Integer maximumScore;	
	private Set<Result> results;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Integer getMaximumScore() {
		return maximumScore;
	}
	public void setMaximumScore(Integer maximumScore) {
		this.maximumScore = maximumScore;
	}
	public Set<Result> getResults() {
		return results;
	}
	public void setResults(Set<Result> results) {
		this.results = results;
	}

}
