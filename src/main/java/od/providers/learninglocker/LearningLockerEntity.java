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
  protected String institutionId;
  protected Date createdAt;
  protected Date updatedAt;
  
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
