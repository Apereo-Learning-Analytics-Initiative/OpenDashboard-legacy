package od.framework.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.oneroster.Enrollment;

@JsonDeserialize(builder = MuseNotify.Builder.class)
public class MuseNotify {

	public String to;
	public String from;
	public String cc;
	public String subject;
	public String body;
	public String attachments;
	public String status;

  

public MuseNotify(){}

  public MuseNotify(String to, String from, String cc, String subject, String body, String attachments, String status){
	  this.to = to;
	  this.from = from;
	  this.cc = cc;
	  this.subject = subject;
	  this.body = body;
	  this.attachments = attachments;
	  this.status=status;
  }
	
  public String getto() {
    return to;
  }
  
  public void setto(String to){
	  this.to = to;
  }
  
  public String getfrom() {
	    return from;
  }
	  
  public void setfrom(String from){
	  this.from = from;
  }
	  
  public String getcc() {
	  return cc;
  }
	  
  public void setcc(String cc){
	  this.cc = cc;
  }
	  
  public String getsubject() {
	  return subject;
  }
		  
  public void setsubject(String subject){
	  this.subject = subject;
  }
  public String getbody() {
	  return body;
  }
			  
  public void setbody(String body){
	  this.body = body;
  }	  
  
  public String getattachments(){
	  return attachments;
  }
  
  public void setattachments(String attachments){
	  this.attachments = attachments;
  }	   
  
  public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
  @Override
  public String toString() {
	  return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
	  return 31415;
  }

  @Override
  public boolean equals(Object obj) {
	  return true;
  }
  
  public static class Builder {
	private MuseNotify _MuseNotify = new MuseNotify();
	
	public Builder withto(String to) {
		_MuseNotify.to = to;
		 return this;
	}
	
	public Builder withfrom(String from) {
		_MuseNotify.from = from;
		 return this;
	}
	
	public Builder withcc(String cc) {
		_MuseNotify.cc = cc;
		 return this;
	}
	
	public Builder withsubject(String subject) {
		_MuseNotify.subject = subject;
		 return this;
	}
	
	public Builder withbody(String body) {
		_MuseNotify.body = body;
		 return this;
	}
	
	public Builder withattachments(String attachments) {
		_MuseNotify.attachments = attachments;
		 return this;
	}
	
	public Builder withstatus(String status) {
		_MuseNotify.status = status;
		 return this;
	}
	public MuseNotify build() {
	  return _MuseNotify;
	}
  }
 }

	    