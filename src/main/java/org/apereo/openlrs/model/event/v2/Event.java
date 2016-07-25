/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
//import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
//import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.annotations.SerializedName;

/**
 * @author ggilbert
 *
 */
//@Document(indexName="openlrs_events_v2")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Event implements Serializable {

  private static final long serialVersionUID = 76908698006094727L;
  @Transient private Logger log = Logger.getLogger(Event.class);
  
  @Id private String id;
  
  @JsonProperty("@context")
  @SerializedName("@context")
  private String context;
  
  @JsonProperty("@type")
  @SerializedName("@type")
  private String type;
  
  private DateTime eventTime;
  private DateTime storedTime;
  private Actor actor;
  private String action;
  private Object object;
  private Target target;
  private Generated generated;
  private Group group;

  @JsonCreator
  public Event(@JsonProperty("id") String id,
      @JsonProperty("@context") String context, 
      @JsonProperty("@type") String type, 
      @JsonProperty("eventTime") DateTime eventTime, 
      @JsonProperty("storedTime") DateTime storedTime, 
      @JsonProperty("actor") Actor actor, 
      @JsonProperty("action") String action, 
      @JsonProperty("object") Object object, 
      @JsonProperty("target") Target target, 
      @JsonProperty("group") Group group,
      @JsonProperty("generated") Generated generated) {
    super();
    this.id = id;
    this.context = context;
    this.type = type;
    this.eventTime = eventTime;
    this.storedTime = storedTime;
    this.actor = actor;
    this.action = action;
    this.object = object;
    this.target = target;
    this.generated = generated;
    this.group = group;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContext() {
    return context;
  }

  public String getType() {
    return type;
  }

  public DateTime getEventTime() {
    return eventTime;
  }
  
  public DateTime getStoredTime() {
    return storedTime;
  }

  public Actor getActor() {
    return actor;
  }

  public String getAction() {
    return action;
  }

  public Object getObject() {
    return object;
  }

  public Target getTarget() {
    return target;
  }

  public Generated getGenerated() {
    return generated;
  }

  public Group getGroup() {
    return group;
  }

  @Override
  public String toString() {
    return "Event [context=" + context + ", type=" + type + ", eventTime=" + eventTime + ", storedTime=" + storedTime + ", actor=" + actor + ", action=" + action + ", object="
        + object + ", target=" + target + ", generated=" + generated + ", group=" + group  + "]";
  }

  @JsonIgnore
  public String toJSON() {
    ObjectMapper om = new ObjectMapper();
    om.setDateFormat(new ISO8601DateFormat());
    //om.setSerializationInclusion(include);
    //om.registerModule(new JodaModule());
    
    String rawJson = null;
    try {
      rawJson = om.writer().writeValueAsString(this);
    } 
    catch (JsonProcessingException e) {
      log.error(e.getMessage(), e); 
    }
    return rawJson;
  }

}
