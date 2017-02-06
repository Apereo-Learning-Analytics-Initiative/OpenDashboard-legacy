package od.framework.model;

import java.time.LocalDate;
import java.util.Map;

public class PulseClassDetail {
  private LocalDate startDate;
  private LocalDate endDate;
  
  private String classSourcedId;
  private String classTitle;
  
  private Integer totalEvents;
  private Integer totalStudentEnrollments;
  
  private Map<String, Long> eventCountGroupedByDate;
}
