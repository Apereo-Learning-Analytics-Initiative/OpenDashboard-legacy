/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.1
 * @since	2017-06-01
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

import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;

@Component("user_jdbc")
public class JdbcUserProvider extends JdbcProvider implements UserProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcUserProvider.class);

  private static final String KEY = "user_jdbc";
  private static final String BASE = "JDBC_USER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);

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
  
	User user = null;
	try {
		JdbcClient client = new JdbcClient(providerData);
		String SQL = "SELECT * FROM VW_OD_US_USERBYSOURCEDID WHERE USERSOURCEDID = ?";
		try {
			ResultSet Rs = client.getData(SQL, userSourcedId);
			if (Rs.next()){
				Role r = getMathewsRoleFromString(Rs.getString("ROLENAME"));
				Status s = Rs.getString("ISACTIVE").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive;
			    user = new User.Builder()
				        .withSourcedId(Rs.getString("USERSOURCEDID"))
				        .withRole(r)
				        .withFamilyName(Rs.getString("FAMILYNAME"))
				        .withGivenName(Rs.getString("GIVENNAME"))
				        .withUserId(Rs.getString("USERID"))
				        .withStatus(s)
				        .build();
			}
			if (!Rs.isClosed()){
				  Rs.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		client.close();
	} catch (Exception e){
		log.error(e.getMessage());
	}
	return user;
  }
  
  @Override
  public String getUserSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
	
	String userSourcedId = null;
	JdbcClient client = new JdbcClient(tenant.findByKey(KEY)); // findByKey gets provider data
	String SQL = "SELECT * FROM VW_OD_US_USERSOURCEDIDWITHEXTERNALID WHERE EXTERNALID = ?";
	try {
		ResultSet Rs = client.getData(SQL, externalId);
		try {
			if (Rs.next()){
				userSourcedId = Rs.getString("USERSOURCEDID");
			}
			if (!Rs.isClosed()){
				  Rs.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		} 
		client.close();
	} catch(Exception e){
		log.error(e.getMessage());
	}
  return userSourcedId;
  }

  
}

