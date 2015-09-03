package org.apereo.lai.impl;

import org.apereo.lai.ForumTopic;

import od.framework.model.OpenDashboardModel;

public class ForumTopicImpl extends OpenDashboardModel implements ForumTopic {

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
