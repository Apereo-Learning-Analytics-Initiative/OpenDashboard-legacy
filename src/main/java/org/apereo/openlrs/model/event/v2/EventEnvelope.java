/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventEnvelope implements Serializable {

  private static final long serialVersionUID = -7989678487994117406L;
  
  private String sensor;
  private DateTime sendTime;
  private List<Event> data;
  
  @JsonCreator
  public EventEnvelope(
      @JsonProperty("sensor") String sensor, 
      @JsonProperty("sendTime") DateTime sendTime, 
      @JsonProperty("data") List<Event> data) {
    super();
    this.sensor = sensor;
    this.sendTime = sendTime;
    this.data = data;
  }

  public String getSensor() {
    return sensor;
  }

  public DateTime getSendTime() {
    return sendTime;
  }

  public List<Event> getData() {
    return data;
  }

  @Override
  public String toString() {
    return "EventEnvelope [sensor=" + sensor + ", sendTime=" + sendTime + ", data=" + data + "]";
  }

}
