/**
 *
 */
package od.providers.jdbc;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import od.providers.Provider;
import od.providers.ProviderData;
import od.providers.course.JdbcCourseProvider;

import java.sql.*;

/**
 * The JDBC client is used in each of the individual providers to create the DB connection and provide a simple
 * "getData" method that results a result set.
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

public class JdbcClient {

  private static final Logger log = LoggerFactory.getLogger(JdbcClient.class);
  
  private String jdbcConnector;
  private String myDriver;
  private String server;
  private String port;
  private String databaseName;
  private String userName;
  private String password;
  private Connection Conn;

  public JdbcClient(ProviderData providerData) {
		
	this.jdbcConnector = providerData.findValueForKey("key");
	this.myDriver = providerData.findValueForKey("myDriver");
	this.server = providerData.findValueForKey("server");
	this.port = providerData.findValueForKey("port");
	this.databaseName = providerData.findValueForKey("databaseName");
	this.userName = providerData.findValueForKey("userName");
	this.password = providerData.findValueForKey("password");

	try {
		Class.forName (this.myDriver);
		this.Conn = DriverManager.getConnection("jdbc:" + this.jdbcConnector.trim()
													+ "://" + this.server.trim()
													+ ":" + this.port.trim()
													+ "/" + this.databaseName.trim()
												, this.userName.trim()
												, this.password.trim());
	} catch (Exception e) {
		log.error(e.getMessage());
	}

  }
  
  public void close(){
	try{
		if (this.Conn != null && !this.Conn.isClosed()){
			this.Conn.close();
		}
	}
	catch(Exception e){
		log.error(e.getMessage());
	}
  }
  
  // variable arguments for prepared statement (may be more than one "where" condition in the SQL
  public ResultSet getData(String SQL, Object... sqlparam) {
	  ResultSet retVal = null;
	  
	  try {
			PreparedStatement stmt = this.Conn.prepareStatement(SQL);
			// iterate params, set stmt param to correct type, supported types: string, integer (long), float (double)
		    // (defaults to string if the type is not one of the above) 
			for (int i = 0; i < sqlparam.length; ++i) {
				String paramType = (sqlparam[i].getClass().getSimpleName()).toLowerCase();
				switch (paramType){ // set functions take the parameter number not the index
					case "string":
						stmt.setString(i+1, (String)sqlparam[i]);
						break;
					case "integer":
						stmt.setInt(i+1, (Integer)sqlparam[i]);
						break;
					case "long":
						stmt.setLong(i+1, (Long)sqlparam[i]);
						break;
					case "float":
						stmt.setFloat(i+1, (Float)sqlparam[i]);
						break;
					case "double":
						stmt.setDouble(i+1, (Double)sqlparam[i]);
						break;
					default:
						stmt.setString(i+1, (String)sqlparam[i]);
				}
			}
			// make sure we have a result set (we should only be doing selects)
			if (stmt.execute()){
				retVal = stmt.getResultSet();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	  return retVal;
  }
}
