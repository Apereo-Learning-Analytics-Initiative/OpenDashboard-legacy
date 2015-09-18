/**
 * 
 */
package od.providers;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProviderOptions implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String contextMappingId;
  private String dashboardId;
  private String cardId;
  
  private String courseId;
  private String userId;
  private String roles;
  
  private String strategy;
  private String strategyKey;
  private String strategyHost;
  
  public String getContextMappingId() {
    return contextMappingId;
  }
  public void setContextMappingId(String contextMappingId) {
    this.contextMappingId = contextMappingId;
  }
  public String getDashboardId() {
    return dashboardId;
  }
  public void setDashboardId(String dashboardId) {
    this.dashboardId = dashboardId;
  }
  public String getCardId() {
    return cardId;
  }
  public void setCardId(String cardId) {
    this.cardId = cardId;
  }
  public String getCourseId() {
    return courseId;
  }
  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getRoles() {
    return roles;
  }
  public void setRoles(String roles) {
    this.roles = roles;
  }
  public String getStrategy() {
    return strategy;
  }
  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }
  public String getStrategyHost() {
    return strategyHost;
  }
  public void setStrategyHost(String strategyHost) {
    this.strategyHost = strategyHost;
  }
  
  public String getStrategyKey() {
    return strategyKey;
  }
  public void setStrategyKey(String strategyKey) {
    this.strategyKey = strategyKey;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
