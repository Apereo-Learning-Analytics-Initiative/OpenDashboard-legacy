package od.framework.model;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PulseDetail.Builder.class)
public class PulseDetail {
  private LocalDate startDate;
  private LocalDate endDate;
  
  private boolean hasRisk;
  private boolean hasGrade;
  private boolean hasEmail;
  private boolean hasMissingSubmissions;
  private boolean hasLastLogin;
  
  private Integer classesMaxEvents;
  
  private List<PulseClassDetail> pulseClassDetails;
  
  private PulseDetail() {}

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public Integer getClassesMaxEvents() {
    return classesMaxEvents;
  }

  public boolean isHasRisk() {
    return hasRisk;
  }

  public boolean isHasGrade() {
    return hasGrade;
  }

  public boolean isHasEmail() {
    return hasEmail;
  }

  public boolean isHasMissingSubmissions() {
    return hasMissingSubmissions;
  }

  public boolean isHasLastLogin() {
    return hasLastLogin;
  }

  public List<PulseClassDetail> getPulseClassDetails() {
    return pulseClassDetails;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((classesMaxEvents == null) ? 0 : classesMaxEvents.hashCode());
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + (hasEmail ? 1231 : 1237);
    result = prime * result + (hasGrade ? 1231 : 1237);
    result = prime * result + (hasLastLogin ? 1231 : 1237);
    result = prime * result + (hasMissingSubmissions ? 1231 : 1237);
    result = prime * result + (hasRisk ? 1231 : 1237);
    result = prime * result + ((pulseClassDetails == null) ? 0 : pulseClassDetails.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
    PulseDetail other = (PulseDetail) obj;
    if (classesMaxEvents == null) {
      if (other.classesMaxEvents != null)
        return false;
    } else if (!classesMaxEvents.equals(other.classesMaxEvents))
      return false;
    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
      return false;
    if (hasEmail != other.hasEmail)
      return false;
    if (hasGrade != other.hasGrade)
      return false;
    if (hasLastLogin != other.hasLastLogin)
      return false;
    if (hasMissingSubmissions != other.hasMissingSubmissions)
      return false;
    if (hasRisk != other.hasRisk)
      return false;
    if (pulseClassDetails == null) {
      if (other.pulseClassDetails != null)
        return false;
    } else if (!pulseClassDetails.equals(other.pulseClassDetails))
      return false;
    if (startDate == null) {
      if (other.startDate != null)
        return false;
    } else if (!startDate.equals(other.startDate))
      return false;
    return true;
  }

  public static class Builder {
    private PulseDetail _pulseDetail = new PulseDetail();
    
    public Builder withEndDate(LocalDate endDate) {
      _pulseDetail.endDate = endDate;
      return this;
    }
    
    public Builder withStartDate(LocalDate startDate) {
      _pulseDetail.startDate = startDate;
      return this;
    }
    
    public Builder withClassesMaxEvents(Integer classesMaxEvents) {
      _pulseDetail.classesMaxEvents = classesMaxEvents;
      return this;
    }
    
    public Builder withHasRisk(boolean hasRisk) {
      _pulseDetail.hasRisk = hasRisk;
      return this;
    }
    
    public Builder withHasGrade(boolean hasGrade) {
      _pulseDetail.hasGrade = hasGrade;
      return this;
    }
    
    public Builder withHasEmail(boolean hasEmail) {
      _pulseDetail.hasEmail = hasEmail;
      return this;
    }
    
    public Builder withHasMissingSubmissions(boolean hasMissingSubmissions) {
      _pulseDetail.hasMissingSubmissions = hasMissingSubmissions;
      return this;
    }

    public Builder withHasLastLogin(boolean hasLastLogin) {
      _pulseDetail.hasLastLogin = hasLastLogin;
      return this;
    }
    
    public Builder withPulseClassDetails(List<PulseClassDetail> pulseClassDetails) {
      _pulseDetail.pulseClassDetails = pulseClassDetails;
      return this;
    }
    
    public PulseDetail build() {
      return _pulseDetail;
    }
  }
}
