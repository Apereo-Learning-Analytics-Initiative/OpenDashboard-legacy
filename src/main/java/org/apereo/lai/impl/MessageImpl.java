/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package org.apereo.lai.impl;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Message;

/**
 * @author ggilbert
 *
 */
public class MessageImpl extends OpenDashboardModel implements Message {

  private static final long serialVersionUID = 1L;
  private String author;
  private String title;
  private String created;
  private String replyTo;
  
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getCreated() {
    return created;
  }
  public void setCreated(String created) {
    this.created = created;
  }
  public String getReplyTo() {
    return replyTo;
  }
  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

}
