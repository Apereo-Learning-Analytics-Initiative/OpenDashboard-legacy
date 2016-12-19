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
@JsonDeserialize(builder = ClassMapping.Builder.class)
public class ClassMapping implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String classSourcedId;
  private String classExternalId;
  private LocalDateTime dateLastModified;
  private String tenantId;
  private String organizationId;
  
  private ClassMapping() {}

  public String getClassSourcedId() {
    return classSourcedId;
  }

  public String getClassExternalId() {
    return classExternalId;
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
    result = prime * result + ((classExternalId == null) ? 0 : classExternalId.hashCode());
    result = prime * result + ((classSourcedId == null) ? 0 : classSourcedId.hashCode());
    result = prime * result + ((dateLastModified == null) ? 0 : dateLastModified.hashCode());
    result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
    ClassMapping other = (ClassMapping) obj;
    if (classExternalId == null) {
      if (other.classExternalId != null)
        return false;
    } else if (!classExternalId.equals(other.classExternalId))
      return false;
    if (classSourcedId == null) {
      if (other.classSourcedId != null)
        return false;
    } else if (!classSourcedId.equals(other.classSourcedId))
      return false;
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
    return true;
  }

  public static class Builder {
    private ClassMapping _classMapping = new ClassMapping();
    
    public Builder withClassSourcedId(String classSourcedId) {
      this._classMapping.classSourcedId = classSourcedId;
      return this;
    }
    
    public Builder withClassExternalId(String classExternalId) {
      this._classMapping.classExternalId = classExternalId;
      return this;
    }
    
    public Builder withDateLastModified(LocalDateTime dateLastModified) {
      this._classMapping.dateLastModified = dateLastModified;
      return this;
    }
    
    public Builder withTenantId(String tenantId) {
      this._classMapping.tenantId = tenantId;
      return this;
    }
    
    public Builder withOrganizationId(String organizationId) {
      this._classMapping.organizationId = organizationId;
      return this;
    }
    
    public ClassMapping build() {
      
      if (_classMapping.dateLastModified == null) {
        _classMapping.dateLastModified = LocalDateTime.now(ZoneId.of("UTC"));
      }
      
      return _classMapping;
    }
  }

}
