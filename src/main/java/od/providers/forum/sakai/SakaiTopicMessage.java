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
package od.providers.forum.sakai;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.MessageImpl;

/**
 * @author ggilbert
 *
 */
public class SakaiTopicMessage extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;

  private String authoredBy;
  private String messageId;
  private String createdOn;
  private String title;
  private String replyTo;
  
  public String getAuthoredBy() {
    return authoredBy;
  }
  public void setAuthoredBy(String authoredBy) {
    this.authoredBy = authoredBy;
  }
  public String getMessageId() {
    return messageId;
  }
  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
  public String getCreatedOn() {
    return createdOn;
  }
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getReplyTo() {
    return replyTo;
  }
  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }
  public MessageImpl toMessage() {
    MessageImpl message = new MessageImpl();
    message.setId(this.messageId);
    message.setAuthor(this.authoredBy);
    message.setCreated(this.createdOn);
    message.setTitle(this.title);
    message.setReplyTo(this.replyTo);
    
    return message;
  }
}
