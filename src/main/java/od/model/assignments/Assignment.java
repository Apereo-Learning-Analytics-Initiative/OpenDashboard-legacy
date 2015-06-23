package od.model.assignments;

import od.model.OpenDashboardModel;

public class Assignment extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String status;
	private String id;
	private String instructions;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
}
