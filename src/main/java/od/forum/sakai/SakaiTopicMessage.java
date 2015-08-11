/**
 * 
 */
package od.forum.sakai;

import od.forum.Message;
import od.model.OpenDashboardModel;

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
  public Message toMessage() {
    Message message = new Message();
    message.setId(this.messageId);
    message.setAuthor(this.authoredBy);
    message.setCreated(this.createdOn);
    message.setTitle(this.title);
    message.setReplyTo(this.replyTo);
    
    return message;
  }
}
