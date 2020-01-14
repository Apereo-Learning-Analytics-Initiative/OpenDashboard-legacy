/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package org.apereo.lai.impl;

import od.framework.model.OpenDashboardModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apereo.lai.Event;

/**
 * @author ggilbert
 *
 */
public class EventImpl extends OpenDashboardModel implements Event  {
  private static final long serialVersionUID = 1L;
  private String sourcedId;
  private String actor;
  private String verb;
  private String object;
  private String objectType;
  private String context;
  private String organization;
  private String timestamp;
  private String eventFormatType;
  private String hourOfDay;
  private String raw;
  
  public String getSourcedId() {
    return sourcedId;
  }
  public void setSourcedId(String sourceId) {
    this.sourcedId = sourceId;
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
  
  public String getDayOfWeek() {
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  Date date;
	try {
		date = sdf.parse(this.timestamp);
	} catch (ParseException e) {
		return "NA";
	}
	  SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
	  return simpleDateformat.format(date);
  }
  
  public String getShortVerb() {
	  return verb.substring(verb.indexOf("#")+1);
  }
  
  public String getHourOfDay() {
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  Date date;
	try {
		date = sdf.parse(this.timestamp);
	} catch (ParseException e) {
		return "NA";
	}
	  SimpleDateFormat simpleDateformat = new SimpleDateFormat("HH"); // the hour of the day
	  return simpleDateformat.format(date);
  }
  
  public Date getDate() {
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  Date date;
	try {
		date = sdf.parse(this.timestamp);
	} catch (ParseException e) {
		return null;		
	}
	return date;
  }
}
