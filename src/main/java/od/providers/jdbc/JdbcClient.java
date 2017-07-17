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

import od.providers.Provider;
import od.providers.ProviderData;

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
		this.Conn = DriverManager.
				getConnection
						("jdbc:" + this.jdbcConnector.trim()
								+ "://" + this.server.trim()
								+ ":" + this.port.trim()
								+ "/" + this.databaseName.trim()
							, this.userName.trim()
							, this.password.trim());
	} catch (Exception e) {
		
		e.printStackTrace();
	}

  }
  
  public ResultSet getData(String sqlString){
	  ResultSet retVal = null;
	  try {
			Statement Stmt = this.Conn.createStatement();
			retVal = Stmt.executeQuery(sqlString);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	  return retVal;
  }
}
