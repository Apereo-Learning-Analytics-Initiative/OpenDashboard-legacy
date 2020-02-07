package od.framework.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PulseDateEventCount.Builder.class)
public class PulseDateEventCount {
  private LocalDate date;
  private Integer eventCount;
  
  private PulseDateEventCount() {}

  public LocalDate getDate() {
    return date;
  }

  public Integer getEventCount() {
    return eventCount;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((eventCount == null) ? 0 : eventCount.hashCode());
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
    PulseDateEventCount other = (PulseDateEventCount) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (eventCount == null) {
      if (other.eventCount != null)
        return false;
    } else if (!eventCount.equals(other.eventCount))
      return false;
    return true;
  }

  public static class Builder {
    private PulseDateEventCount _pulseDateEventCount = new PulseDateEventCount();
    
    public Builder withDate(LocalDate date) {
      _pulseDateEventCount.date = date;
      return this;
    }
    
    public Builder withEventCount(Integer eventCount) {
      _pulseDateEventCount.eventCount = eventCount;
      return this;
    }
    
    public PulseDateEventCount build() {
      return _pulseDateEventCount;
    }
  }
}
