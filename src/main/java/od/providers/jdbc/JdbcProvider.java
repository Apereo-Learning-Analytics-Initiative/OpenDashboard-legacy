/**
 *
 */
package od.providers.jdbc;

import java.util.LinkedList;

import od.providers.Provider;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

/**
 * This implementation is the base provider for connecting to a JDBC data source it follows the pattern
 * established for other providers in the open dashboard source tree.  At runtime it is assumed that the JDBC provider
 * defined during the configuration is in the resolution path for execution, this eliminates the need to specifically 
 * import any one specific JDBC provider.
 * 
 * Each of the providers established use specific views that follow the following pattern:
 * VW_OD_XX_NNNNNNNNNNNNNNN
 * 
 * Where XX Is: (EN)rollment, (US)er, (CO)urse, (LI)ne Item, (EV)ents
 * And NNNNNNNNNNNNNNN: is the override name )or an abbreviation as their may be a character limit on the view name
 * 
 * For example, the view for the course provider for the override getClassSourcedIdWithExternalId is:
 * VW_OD_CO_CLASSSOURCEDIDWITHEXTERNALID
 * 
 * During initial development the pattern "VW_" does not have to be a view and in some cases was actually a table, the "VW_"
 * prefix is intended to mean a "view into the data" and not necessarily a view implementation in the database. 
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
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

  protected unicon.matthews.oneroster.Class toClass(String classSourcedId, String title) {
		
	    unicon.matthews.oneroster.Class klass 
	    = new unicon.matthews.oneroster.Class.Builder()
	      .withSourcedId(classSourcedId)
	      .withTitle(title)
	      .withStatus(Status.active)
	      .build();

		return klass;
	}

  protected User toUser(Role role, String familyname , String givenname, String userID, String userId) {
		
		User user
			= new User.Builder()
			.withRole(role)
			.withFamilyName(familyname)
			.withGivenName(givenname)
			.withStatus(Status.active)
			.withSourcedId(userID)
			.withUserId(userId)
			.build();

		return user;
	}

  protected	Enrollment toEnrollment(Role role,User user,unicon.matthews.oneroster.Class Klass) {
		
		Enrollment enrollment
		 = new Enrollment.Builder()
		 	.withRole(role)
		 	.withStatus(Status.active)
		 	.withUser(user)
		 	.withKlass(Klass)
		 	.build();
		
		return enrollment;
	}
  
  
  @Override
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

}
