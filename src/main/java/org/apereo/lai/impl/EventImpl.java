/**
 * 
 */
package org.apereo.lai.impl;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Event;

/**
 * @author ggilbert
 *
 */
public class EventImpl extends OpenDashboardModel implements Event  {
  private static final long serialVersionUID = 1L;
  private String sourceId;
  private String actor;
  private String verb;
  private String object;
  private String objectType;
  private String context;
  private String organization;
  private String timestamp;
  private String eventFormatType;
  private String raw;
  
  public String getSourceId() {
    return sourceId;
  }
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  public String getActor() {
    return actor;
  }
  public void setActor(String actor) {
    this.actor = actor;
  }
  public String getVerb() {
    return verb;
  }
  public void setVerb(String verb) {
    this.verb = verb;
  }
  public String getObject() {
    return object;
  }
  public void setObject(String object) {
    this.object = object;
  }
  public String getObjectType() {
    return objectType;
  }
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }
  public String getContext() {
    return context;
  }
  public void setContext(String context) {
    this.context = context;
  }
  public String getOrganization() {
    return organization;
  }
  public void setOrganization(String organization) {
    this.organization = organization;
  }
  public String getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
  public String getEventFormatType() {
    return eventFormatType;
  }
  public void setEventFormatType(String eventFormatType) {
    this.eventFormatType = eventFormatType;
  }
  public String getRaw() {
    return raw;
  }
  public void setRaw(String raw) {
    this.raw = raw;
  }

}
