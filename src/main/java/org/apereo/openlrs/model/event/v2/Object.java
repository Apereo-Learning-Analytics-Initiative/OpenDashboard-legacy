/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Object extends BaseEventComponent {
  @JsonCreator
  public Object (@JsonProperty("@id") String id,
      @JsonProperty("@context") String context,
      @JsonProperty("@type") String type,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("extensions") Map<String,String> extensions) {
    this.id = id;
    this.context = context;
    this.type = type;
    this.name = name;
    this.description = description;
    this.extensions = extensions;
  }

  @Override
  public String toString() {
    return "Object [id=" + id + ", context=" + context + ", type=" + type + ", name=" + name + ", description=" + description + ", extensions="
        + extensions + "]";
  }

}
