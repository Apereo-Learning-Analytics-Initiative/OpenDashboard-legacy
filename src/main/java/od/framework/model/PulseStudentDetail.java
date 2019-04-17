package od.framework.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PulseStudentDetail.Builder.class)
public class PulseStudentDetail {

  private String id;
  private String label;
  
  private String firstName;
  private String lastName;
  private String email;
  
  private String risk;
  private String grade;
  private Long activity;
  private Long daysSinceLogin;
  private boolean missingSubmission;
  
  private List<PulseDateEventCount> events;
  
  private PulseStudentDetail() {}

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getRisk() {
    return risk;
  }
  
  public double getRiskAsDouble() {
    try {      
      return Double.parseDouble(risk);      
    }
    catch (Exception e) {
      return Double.NaN;
    }
  }

  public String getGrade() {
    return grade;
  }

  public Long getActivity() {
    return activity;
  }

  public Long getDaysSinceLogin() {
    return daysSinceLogin;
  }

  public boolean isMissingSubmission() {
    return missingSubmission;
  }

  public List<PulseDateEventCount> getEvents() {
    return events;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((activity == null) ? 0 : activity.hashCode());
    result = prime * result + ((daysSinceLogin == null) ? 0 : daysSinceLogin.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((events == null) ? 0 : events.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((grade == null) ? 0 : grade.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + (missingSubmission ? 1231 : 1237);
    result = prime * result + ((risk == null) ? 0 : risk.hashCode());
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
    PulseStudentDetail other = (PulseStudentDetail) obj;
    if (activity == null) {
      if (other.activity != null)
        return false;
    } else if (!activity.equals(other.activity))
      return false;
    if (daysSinceLogin == null) {
      if (other.daysSinceLogin != null)
        return false;
    } else if (!daysSinceLogin.equals(other.daysSinceLogin))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (events == null) {
      if (other.events != null)
        return false;
    } else if (!events.equals(other.events))
      return false;
    if (firstName == null) {
      if (other.firstName != null)
        return false;
    } else if (!firstName.equals(other.firstName))
      return false;
    if (grade == null) {
      if (other.grade != null)
        return false;
    } else if (!grade.equals(other.grade))
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
    if (lastName == null) {
      if (other.lastName != null)
        return false;
    } else if (!lastName.equals(other.lastName))
      return false;
    if (missingSubmission != other.missingSubmission)
      return false;
    if (risk == null) {
      if (other.risk != null)
        return false;
    } else if (!risk.equals(other.risk))
      return false;
    return true;
  }

  public static class Builder {
    private PulseStudentDetail _pulseStudentDetail = new PulseStudentDetail();

    public Builder withId(String id) {
      _pulseStudentDetail.id = id;
      return this;
    }
    
    public Builder withLabel(String label) {
      _pulseStudentDetail.label = label;
      return this;
    }
    
    public Builder withFirstName(String firstName) {
      _pulseStudentDetail.firstName = firstName;
      return this;
    }
    
    public Builder withLastName(String lastName) {
      _pulseStudentDetail.lastName = lastName;
      return this;
    }
    
    public Builder withEmail(String email) {
      _pulseStudentDetail.email = email;
      return this;
    }
    
    public Builder withRisk(String risk) {
      _pulseStudentDetail.risk = risk;
      return this;
    }
   
    public Builder withGrade(String grade) {
      _pulseStudentDetail.grade = grade;
      return this;
    }
    
    public Builder withActivity(Long activity) {
      _pulseStudentDetail.activity = activity;
      return this;
    }
    
    public Builder withDaysSinceLogin(Long daysSinceLogin) {
      _pulseStudentDetail.daysSinceLogin = daysSinceLogin;
      return this;
    }
    
    public Builder withMissingSubmission(boolean missingSubmission) {
      _pulseStudentDetail.missingSubmission = missingSubmission;
      return this;
    }
    
    public Builder withEvents(List<PulseDateEventCount> events) {
      _pulseStudentDetail.events = events;
      return this;
    }
    
    public PulseStudentDetail build() {
      return _pulseStudentDetail;
    }
  }
}
