/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

package od.providers.course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;

import org.apereo.lai.Course;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Status;
import unicon.oneroster.Vocabulary;

@Component("course_jdbc")
public class JdbcCourseProvider extends JdbcProvider implements CourseProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcCourseProvider.class);

  private static final String KEY = "course_jdbc";
  private static final String BASE = "JDBC_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);

  @PostConstruct
  public void init() {
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
  public Course getContext(ProviderData providerData, String contextId) throws ProviderException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Course> getContexts(ProviderData providerData, String userId) throws ProviderException {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see od.providers.course.CourseProvider#getClassSourcedIdWithExternalId(od.framework.model.Tenant, java.lang.String)
   */
  @Override
  public String getClassSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {

	    String classSourcedId = null;
	    try {
		    JdbcClient client = new JdbcClient(tenant.findByKey(KEY)); // findByKey gets provider data
		    String SQL = "SELECT * FROM VW_OD_CO_CLASSSOURCEDIDWITHEXTERNALID WHERE EXTERNALID = ?";
		    try {
			    ResultSet Rs = client.getData(SQL, externalId);
				if (Rs.next()) {
					classSourcedId = Rs.getString("CLASSSOURCEDID");
				}
				if (!Rs.isClosed()){
					  Rs.close();
				}
		    } catch(SQLException e){
		    	log.error(e.getMessage());
		    }
	    	client.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

    return classSourcedId;
  }

  @Override
  public Class getClass(Tenant tenant, String classSourcedId) throws ProviderException {

      Class returnClass = null;
      try {
    	  JdbcClient client = new JdbcClient(tenant.findByKey(KEY)); // findByKey gets provider data
          String SQL = "SELECT * FROM VW_OD_CO_GETCLASS WHERE CLASSSOURCEDID = ?";
	      try {
	          ResultSet Rs = client.getData(SQL, classSourcedId);
	          if (Rs.next()){   
				Map<String, String> metadata = new HashMap<>();
				metadata.put(Vocabulary.CLASS_START_DATE, Rs.getString("CLASS_START_DATE"));
				metadata.put(Vocabulary.CLASS_END_DATE, Rs.getString("CLASS_END_DATE"));
				metadata.put(Vocabulary.SOURCE_SYSTEM, Rs.getString("SOURCE_SYSTEM"));
				metadata.put(Vocabulary.CLASS_STATISTICS,  Rs.getString("CLASS_STATISTICS"));
				returnClass = new Class.Builder()
									.withSourcedId(Rs.getString("CLASSSOURCEDID"))
									.withTitle(Rs.getString("TITLE"))
									.withMetadata(metadata)
									.withStatus(Rs.getString("ISACTIVE").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive)
									.build();
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
      return returnClass;
 }

}