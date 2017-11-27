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

import java.util.Date;
import java.util.Map;

import org.apereo.lai.ModelOutput;

/**
 * @author ggilbert
 *
 */
public class ModelOutputImpl implements ModelOutput {
  
  public ModelOutputImpl() {
    super();
  }
  
  public ModelOutputImpl(Map<String, Object> output, Date createdDate) {
    super();
    this.output = output;
    this.createdDate = createdDate;
  }
  
  private Map<String, Object> output;
  private Date createdDate;
  private String userSourcedId;
  
  public Map<String, Object> getOutput() {
    return output;
  }
  public void setOutput(Map<String, Object> output) {
    this.output = output;
  }
  public Date getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
  public String getUserSourcedId() {
    return userSourcedId;
  }

  public void setUserSourcedId(String userSourcedId) {
    this.userSourcedId = userSourcedId;
  }

  @Override
  public String toString() {
    return "ModelOutputImpl [output=" + output + ", createdDate=" + createdDate + "]";
  }

}
