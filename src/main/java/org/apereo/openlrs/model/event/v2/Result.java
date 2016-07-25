/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Result extends Generated {

  @JsonCreator
  public Result (@JsonProperty("@id") String id,
      @JsonProperty("@context") String context,
      @JsonProperty("@type") String type,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("extensions") Map<String,String> extensions,
      @JsonProperty("actor") Actor actor,
      @JsonProperty("startedAtTime") DateTime startedAtTime,
      @JsonProperty("endedAtTime") DateTime endedAtTime,
      @JsonProperty("duration") Duration duration) {
    super(id, context, type, name, description, extensions, actor, startedAtTime, endedAtTime, duration);
  }

}
