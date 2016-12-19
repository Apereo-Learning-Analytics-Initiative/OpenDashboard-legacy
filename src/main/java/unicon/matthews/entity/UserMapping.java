/**
 * 
 */
package unicon.matthews.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author ggilbert
 *
 */
@JsonDeserialize(builder = UserMapping.Builder.class)
public class UserMapping implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String userSourcedId;
  private String userExternalId;
  private String tenantId;
  private String organizationId;
  private LocalDateTime dateLastModified;
  
  private UserMapping() {}

  public String getUserSourcedId() {
    return userSourcedId;
  }

  public String getUserExternalId() {
    return userExternalId;
  }
  
  public LocalDateTime getDateLastModified() {
    return dateLastModified;
  }

  public String getTenantId() {
    return tenantId;
  }

  public String getOrganizationId() {
    return organizationId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateLastModified == null) ? 0 : dateLastModified.hashCode());
    result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    result = prime * result + ((userExternalId == null) ? 0 : userExternalId.hashCode());
    result = prime * result + ((userSourcedId == null) ? 0 : userSourcedId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserMapping other = (UserMapping) obj;
    if (dateLastModified == null) {
      if (other.dateLastModified != null)
        return false;
    } else if (!dateLastModified.equals(other.dateLastModified))
      return false;
    if (organizationId == null) {
      if (other.organizationId != null)
        return false;
    } else if (!organizationId.equals(other.organizationId))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    if (userExternalId == null) {
      if (other.userExternalId != null)
        return false;
    } else if (!userExternalId.equals(other.userExternalId))
      return false;
    if (userSourcedId == null) {
      if (other.userSourcedId != null)
        return false;
    } else if (!userSourcedId.equals(other.userSourcedId))
      return false;
    return true;
  }

  public static class Builder {
    private UserMapping _userMapping = new UserMapping();
    
    public Builder withUserSourcedId(String userSourcedId) {
      this._userMapping.userSourcedId = userSourcedId;
      return this;
    }
    
    public Builder withUserExternalId(String userExternalId) {
      this._userMapping.userExternalId = userExternalId;
      return this;
    }
    
    public Builder withDateLastModified(LocalDateTime dateLastModified) {
      this._userMapping.dateLastModified = dateLastModified;
      return this;
    }
    
    public Builder withTenantId(String tenantId) {
      this._userMapping.tenantId = tenantId;
      return this;
    }
    
    public Builder withOrganizationId(String organizationId) {
      this._userMapping.organizationId = organizationId;
      return this;
    }
    
    public UserMapping build() {
      
      if (_userMapping.dateLastModified == null) {
        _userMapping.dateLastModified = LocalDateTime.now(ZoneId.of("UTC"));
      }
      
      return _userMapping;
    }
  }

}
