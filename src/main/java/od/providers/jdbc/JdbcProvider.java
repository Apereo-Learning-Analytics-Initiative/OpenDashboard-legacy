/**
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.0.1
 * @since	2017-06-01
 */
package od.providers.jdbc;

import java.util.LinkedList;

import od.providers.Provider;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;

import unicon.matthews.oneroster.Role;

/**
 * This implementation is the base provider for connecting to a JDBC data source it follows the pattern
 * established for other providers in the open dashboard source tree.  At runtime it is assumed that the JDBC driver
 * defined during the configuration is in the resolution path for execution, this eliminates the need to specifically 
 * import the JDBC driver.
 * 
 * Each of the providers established use specific views that follow the following pattern:
 * VW_OD_XX_NNNNNNNNNNNNNNN
 * 
 * Where XX Is: (EN)rollment, (US)er, (CO)urse, (LI)ne Item, (EV)ents
 * And NNNNNNNNNNNNNNN: is the override name or an abbreviation as their may be a character limit on the view name
 * 
 * For example, the view for the course provider for the override getClassSourcedIdWithExternalId is:
 * VW_OD_CO_CLASSSOURCEDIDWITHEXTERNALID
 * 
 * During initial development the pattern "VW_" does not have to be a view and in some cases was actually a table, the "VW_"
 * prefix is intended to mean a "view into the data" and not necessarily a view implementation in the database.
 * 
 * The following views are needed:
 * Course: VW_OD_CO_CLASSSOURCEDIDWITHEXTERNALID,  VW_OD_CO_GETCLASS, 
 * Enrollment: VW_OD_EN_FORCLASS, VW_OD_EN_FORUSER
 * Event: VW_OD_EV_SUMMARYDATA, VW_OD_EV_COUNTBYDATE, VW_OD_EV_COUNTBYDATEBYSTUDENT
 *        VW_OD_EV_USER, VW_OD_EV_COURSE, VW_OD_EV_COURSEANDUSER
 * Line Item: VW_OD_LI_FORCLASS
 * User: VW_OD_US_USERBYSOURCEDID, VW_OD_US_USERSOURCEDIDWITHEXTERNALID
 * 
 */

public abstract class JdbcProvider implements Provider {
  protected ProviderConfiguration providerConfiguration;

  public ProviderConfiguration getDefaultJdbcConfiguration() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<>();
    ProviderConfigurationOption jdbcConnector = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCCONNECTOR", "Jdbc Connector",  true);
    ProviderConfigurationOption myDriver = new TranslatableKeyValueConfigurationOptions("myDriver", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCDRIVER", "Jdbc Driver (eg: org.netezza.Driver) ",  true);
    ProviderConfigurationOption server = new TranslatableKeyValueConfigurationOptions("server", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCSERVER", "Server",  true);
    ProviderConfigurationOption port = new TranslatableKeyValueConfigurationOptions("port", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCPORT", "Port",  true);
    ProviderConfigurationOption databaseName = new TranslatableKeyValueConfigurationOptions("databaseName", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCDATABASENAME", "Database Name",  true);
    ProviderConfigurationOption userName = new TranslatableKeyValueConfigurationOptions("userName", null, ProviderConfigurationOption.TEXT_TYPE, true, "LABEL_JDBCUSERNAME", "User Name",  true);
    ProviderConfigurationOption password = new TranslatableKeyValueConfigurationOptions("password", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "LABEL_JDBCPASSWORD", "Password", true);
    
    options.add(jdbcConnector);
    options.add(myDriver);
    options.add(server);
    options.add(port);
    options.add(databaseName);
    options.add(userName);
    options.add(password);
    
    return new DefaultProviderConfiguration(options);
  }

  protected Role getMathewsRoleFromString(String roleName){
	  Role retVal = Role.student;
	  
	  switch(roleName.toLowerCase()){
		  case "administrator":
			  retVal = Role.administrator;
			  break;
		  case "aide":
			  retVal = Role.aide;
			  break;
		  case "guardian":
			  retVal = Role.guardian;
			  break;
		  case "parent":
			  retVal = Role.parent;
			  break;
		  case "relative":
			  retVal = Role.relative;
			  break;
		  case "student":
			  retVal = Role.student;
			  break;
		  case "teacher":
			  retVal = Role.teacher;
			  break;
		  default:
		  		retVal = Role.student;
	  }
	  return retVal;
  }

  @Override
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

}
