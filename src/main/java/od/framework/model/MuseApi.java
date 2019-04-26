package od.framework.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MuseApi.Builder.class)
public class MuseApi {

	public String data;
	public String status;

  

public MuseApi(){}

  public MuseApi(String data, String status){
	  this.data = data;
	  this.status=status;
  }
	
  public String getdata() {
    return data;
  }
  
  public void setdata(String data){
	  this.data = data;
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
	private MuseApi _MuseApi = new MuseApi();
	
	public Builder withdata(String data) {
		_MuseApi.data = data;
		 return this;
	}
	
	public Builder withstatus(String status) {
		_MuseApi.status = status;
		 return this;
	}
	public MuseApi build() {
	  return _MuseApi;
	}
  }
 }
