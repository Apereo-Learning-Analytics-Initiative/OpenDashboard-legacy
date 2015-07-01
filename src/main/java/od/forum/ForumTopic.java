package od.forum;

import od.model.OpenDashboardModel;

public class ForumTopic extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String id;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
