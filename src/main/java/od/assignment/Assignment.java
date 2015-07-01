package od.assignment;

import od.model.OpenDashboardModel;

public class Assignment extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String status;
	private String instructions;
	private String context;
	
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
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
}
