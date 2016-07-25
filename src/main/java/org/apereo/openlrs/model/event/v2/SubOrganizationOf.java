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
public class SubOrganizationOf extends BaseEventComponent {
  
  private String courseNumber;
  private String academicSession;
  private String category;
  private SubOrganizationOf subOrganizationOf;
  
  @JsonCreator
  public SubOrganizationOf (@JsonProperty("@id") String id,
      @JsonProperty("@context") String context,
      @JsonProperty("@type") String type,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("extensions") Map<String,String> extensions,
      @JsonProperty("subOrganizationOf") SubOrganizationOf subOrganizationOf,
      @JsonProperty("courseNumber") String courseNumber,
      @JsonProperty("academicSession") String academicSession,
      @JsonProperty("category") String category) {
    this.id = id;
    this.context = context;
    this.type = type;
    this.name = name;
    this.description = description;
    this.extensions = extensions;
    this.subOrganizationOf = subOrganizationOf;
    this.academicSession = academicSession;
    this.courseNumber = courseNumber;
    this.category = category;
  }

  public String getCourseNumber() {
    return courseNumber;
  }

  public String getAcademicSession() {
    return academicSession;
  }

  public String getCategory() {
    return category;
  }

  public SubOrganizationOf getSubOrganizationOf() {
    return subOrganizationOf;
  }

  @Override
  public String toString() {
    return "SubOrganizationOf [courseNumber=" + courseNumber + ", academicSession=" + academicSession + ", category=" + category
        + ", subOrganizationOf=" + subOrganizationOf + ", id=" + id + ", context=" + context + ", type=" + type + ", name=" + name + ", description="
        + description + ", extensions=" + extensions + "]";
  }

}
