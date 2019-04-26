/**
 * 
 */
package od.providers.learninglocker;

import java.util.Date;


/**
 * @author ggilbert
 *
 */
public abstract class LearningLockerEntity {
  protected String id;
  protected String institutionId;
  protected Date createdAt;
  protected Date updatedAt;
  
  public String getId() {
    return id;
  }
  public String getInstitutionId() {
    return institutionId;
  }
  public Date getCreatedAt() {
    return createdAt;
  }
  public Date getUpdatedAt() {
    return updatedAt;
  }

}
