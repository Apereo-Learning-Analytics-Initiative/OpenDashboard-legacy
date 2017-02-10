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
  
  private List<PulseClassDetail> pulseClassDetails;
  
  private PulseDetail() {}

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
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
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
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
    
    public Builder withPulseClassDetails(List<PulseClassDetail> pulseClassDetails) {
      _pulseDetail.pulseClassDetails = pulseClassDetails;
      return this;
    }
    
    public PulseDetail build() {
      return _pulseDetail;
    }
  }
}
