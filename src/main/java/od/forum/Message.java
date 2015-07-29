/**
 * 
 */
package od.forum;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Message extends OpenDashboardModel {

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
