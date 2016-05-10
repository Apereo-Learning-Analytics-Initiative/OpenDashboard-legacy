/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Target extends BaseEventComponent {
  
  private List<String> keywords;
  
  @JsonCreator
  public Target (@JsonProperty("@id") String id,
      @JsonProperty("@context") String context,
      @JsonProperty("@type") String type,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("extensions") Map<String,String> extensions,
      @JsonProperty("keywords") List<String> keywords) {
    this.id = id;
    this.context = context;
    this.type = type;
    this.name = name;
    this.description = description;
    this.extensions = extensions;
    this.keywords = keywords;
  }

  public List<String> getKeywords() {
    return keywords;
  }

  @Override
  public String toString() {
    return "Target [keywords=" + keywords + ", id=" + id + ", context=" + context + ", type=" + type + ", name=" + name + ", description="
        + description + ", extensions=" + extensions + "]";
  }

}
