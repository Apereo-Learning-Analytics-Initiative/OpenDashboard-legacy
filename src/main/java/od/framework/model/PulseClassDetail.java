package od.framework.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import unicon.matthews.oneroster.LineItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PulseClassDetail.Builder.class)
public class PulseClassDetail {
  
  private String id;
  private String label;
  private LocalDate startdate;
  private LocalDate enddate;
  private Integer studentEventMax;
  private Long studentEventTotalMax;
  private Integer meanStudentEvents;
  private Long medianStudentEvents;
  private Double meanPassPercent;
  private Double medianPassPercent;
  private Integer totalNumberOfEvents;
  private Integer classEventMax;
  
  private boolean hasRisk;
  private boolean hasGrade;
  private boolean hasAssignments;
  private boolean hasMissingSubmissions;
  
  private List<PulseDateEventCount> events;
  private List<PulseStudentDetail> students;
  private List<LineItem> assignments;
  public Integer studentsWithEvents;
  public Map<String, Long> eventTypeTotals;
  public Map<String, Double> eventTypeAverages;
  
  private PulseClassDetail() {}

  public void setId(String str) {
	    id= str;
	  }
  
  public String getId() {
    return id;
  }

  
  public Double getMedianPassPercent() {
    return medianPassPercent;
  }
  
  public Long getMedianStudentEvents() {
    return medianStudentEvents;
  }

  public Integer getStudentsWithEvents() {
    return studentsWithEvents;
  }
  
  public Integer getClassEventMax() {
	    return classEventMax;
	  }

  public Map<String, Long> getEventTypeTotals() {
    return eventTypeTotals;
  }

  public Map<String, Double> getEventTypeAverages() {
    return eventTypeAverages;
  }

  public Double getMeanPassPercent() {
    return meanPassPercent;
  }

  public String getLabel() {
    return label;
  }

  public LocalDate getStartdate() {
    return startdate;
  }

  public LocalDate getEnddate() {
    return enddate;
  }

  public Integer getMeanStudentEvents() {
    return meanStudentEvents;
  }
  
  public Integer getStudentEventMax() {
    return studentEventMax;
  }
  
  public Long getStudentEventTotalMax() {
    return studentEventTotalMax;
  }

  public Integer getTotalNumberOfEvents() {
    return totalNumberOfEvents;
  }

  public boolean hasRisk() {
    return hasRisk;
  }

  public boolean hasGrade() {
    return hasGrade;
  }

  public boolean hasAssignments() {
    return hasAssignments;
  }

  public boolean hasMissingSubmissions() {
    return hasMissingSubmissions;
  }

  public List<PulseDateEventCount> getEvents() {
    return events;
  }

  public List<PulseStudentDetail> getStudents() {
    return students;
  }

  public List<LineItem> getAssignments() {
    return assignments;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((assignments == null) ? 0 : assignments.hashCode());
    result = prime * result + ((enddate == null) ? 0 : enddate.hashCode());
    result = prime * result + ((events == null) ? 0 : events.hashCode());
    result = prime * result + (hasAssignments ? 1231 : 1237);
    result = prime * result + (hasGrade ? 1231 : 1237);
    result = prime * result + (hasMissingSubmissions ? 1231 : 1237);
    result = prime * result + (hasRisk ? 1231 : 1237);
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((startdate == null) ? 0 : startdate.hashCode());
    result = prime * result + ((studentEventTotalMax == null) ? 0 : studentEventTotalMax.hashCode());
    result = prime * result + ((studentEventMax == null) ? 0 : studentEventMax.hashCode());
    result = prime * result + ((students == null) ? 0 : students.hashCode());
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
    PulseClassDetail other = (PulseClassDetail) obj;
    if (assignments == null) {
      if (other.assignments != null)
        return false;
    } else if (!assignments.equals(other.assignments))
      return false;
    if (enddate == null) {
      if (other.enddate != null)
        return false;
    } else if (!enddate.equals(other.enddate))
      return false;
    if (events == null) {
      if (other.events != null)
        return false;
    } else if (!events.equals(other.events))
      return false;
    if (hasAssignments != other.hasAssignments)
      return false;
    if (hasGrade != other.hasGrade)
      return false;
    if (hasMissingSubmissions != other.hasMissingSubmissions)
      return false;
    if (hasRisk != other.hasRisk)
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    if (startdate == null) {
      if (other.startdate != null)
        return false;
    } else if (!startdate.equals(other.startdate))
      return false;
    if (studentEventTotalMax == null) {
      if (other.studentEventTotalMax != null)
        return false;
    } else if (!studentEventTotalMax.equals(other.studentEventTotalMax))
      return false;
    if (studentEventMax == null) {
      if (other.studentEventMax != null)
        return false;
    } else if (!studentEventMax.equals(other.studentEventMax))
      return false;
    if (students == null) {
      if (other.students != null)
        return false;
    } else if (!students.equals(other.students))
      return false;
    return true;
  }

  public static class Builder {
    
    private PulseClassDetail _pulseClassDetail = new PulseClassDetail();
    
    public Builder withId(String id) {
      _pulseClassDetail.id = id;
      return this;
    }
    
    public Builder withLabel(String label) {
      _pulseClassDetail.label = label;
      return this;
    }
    
    public Builder withStartdate(LocalDate startdate) {
      _pulseClassDetail.startdate = startdate;
      return this;
    }
    
    public Builder withEnddate(LocalDate enddate) {
      _pulseClassDetail.enddate = enddate;
      return this;
    }

    public Builder withStudentEventMax(Integer studentEventMax) {
      _pulseClassDetail.studentEventMax = studentEventMax;
      return this;
    }
    
    public Builder withStudentEventTotalMax(Long studentEventTotalMax) {
      _pulseClassDetail.studentEventTotalMax = studentEventTotalMax;
      return this;
    }
    
    public Builder withHasRisk(boolean hasRisk) {
      _pulseClassDetail.hasRisk = hasRisk;
      return this;
    }
    
    public Builder withHasGrade(boolean hasGrade) {
      _pulseClassDetail.hasGrade = hasGrade;
      return this;
    }
    
    public Builder withHasAssignments(boolean hasAssignments) {
      _pulseClassDetail.hasAssignments = hasAssignments;
      return this;
    }
    
    public Builder withHasMissingSubmissions(boolean hasMissingSubmissions) {
      _pulseClassDetail.hasMissingSubmissions = hasMissingSubmissions;
      return this;
    }
    
    public Builder withEvents(List<PulseDateEventCount> events) {
      _pulseClassDetail.events = events;
      return this;
    }
    
    public Builder withStudents(List<PulseStudentDetail> students) {
      _pulseClassDetail.students = students;
      return this;
    }
    
    public Builder withAssignments(List<LineItem> assignments) {
      _pulseClassDetail.assignments = assignments;
      return this;
    }
    
    public Builder withMeanStudentEvents(Integer meanStudentEvents) {
      _pulseClassDetail.meanStudentEvents = meanStudentEvents;
      return this;
    }
    
    public Builder withMedianStudentEvents(Long medianStudentEvents) {
      _pulseClassDetail.medianStudentEvents = medianStudentEvents;
      return this;
    }
    
    public Builder withMeanPassPercent(Double meanPassPercent) {
      _pulseClassDetail.meanPassPercent = meanPassPercent;
      return this;
    }
    
    public Builder withMedianPassPercent(Double medianPassPercent) {
      _pulseClassDetail.medianPassPercent = medianPassPercent;
      return this;
    }
    
    public Builder withTotalNumberOfEvents(Integer totalNumberOfEvents) {
      _pulseClassDetail.totalNumberOfEvents = totalNumberOfEvents;
      return this;
    }

    public Builder withStudentsWithEvents(Integer studentsWithEvents) {
      _pulseClassDetail.studentsWithEvents = studentsWithEvents;
      return this;
    }
    
    public Builder withClassEventMax(Integer classEventMax) {
        _pulseClassDetail.classEventMax = classEventMax;
        return this;
      }

    public Builder withEventTypeTotals(Map<String, Long> eventTypeTotals) {
      _pulseClassDetail.eventTypeTotals = eventTypeTotals;
      return this;
    }

    public Builder withEventTypeAverages(Map<String, Double> eventTypeAverages) {
      _pulseClassDetail.eventTypeAverages = eventTypeAverages;
      return this;
    }    

    public PulseClassDetail build() {
      return _pulseClassDetail;
    }
  }
}
