/**
 *
 */
package od.providers.user;

import java.sql.*;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import unicon.matthews.oneroster.User;

/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

@Component("user_jdbc")
public class JdbcUserProvider extends JdbcProvider implements UserProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcUserProvider.class);

  private static final String KEY = "user_jdbc";
  private static final String BASE = "JDBC_USER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);

private User User;
  

  @PostConstruct
  public void init() throws ProviderException {
    providerConfiguration = getDefaultJdbcConfiguration();
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }


  
  @Override
  public User getUserBySourcedId(ProviderData providerData, String userSourcedId) {
	  
	JdbcClient client = new JdbcClient(providerData);
	  String SQL = "SELECT * FROM VW_OD_US_STUDENTENROLLMENTS";
	  ResultSet Rs = client.getData(SQL);
             try {
                 while (Rs.next()) 
                 {
                     }
     		} catch (SQLException e) {
     			log.error(e.getMessage());
     		} 
    return User;
  }
  
  @Override
  public String getUserSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
	  return null;
  }

  
}

