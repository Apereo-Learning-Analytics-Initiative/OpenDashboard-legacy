/**
 *
 */
package od.providers.course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.entity.ClassMapping;
import unicon.matthews.entity.UserMapping;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Status;
import unicon.oneroster.Vocabulary;

/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.0.1
 * @since	2017-06-01
 */

@Component("course_jdbc")
public class JdbcCourseProvider extends JdbcProvider implements CourseProvider {

  // private static final Logger log = LoggerFactory.getLogger(JdbcEventProvider.class);

  private static final String KEY = "course_jdbc";
  private static final String BASE = "JDBC_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  @Autowired private MongoTenantRepository mongoTenantRepository;
  

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
	    ProviderData providerData = tenant.findByKey(KEY);

	    String classSourcedId = null;
	    try {
		    JdbcClient client = new JdbcClient(providerData);
		    String SQL = "SELECT * FROM VW_OD_CO_CLASSSOURCEDIDWITHEXTERNALID WHERE EXTERNALID = '" + externalId  + "'";
		    ResultSet Rs = client.getData(SQL);
			if (Rs.next()) {
				classSourcedId = Rs.getString("CLASSSOURCEDID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

    return classSourcedId;
  }

  @Override
  public Class getClass(Tenant tenant, String classSourcedId) throws ProviderException {
	  ProviderData providerData = tenant.findByKey(KEY);
	  Map<String, unicon.matthews.oneroster.Class> classes = new HashMap<>();
	  
	   JdbcClient client = new JdbcClient(providerData);
      String SQL = "SELECT * FROM VW_OD_CO_GETCLASS WHERE CLASSSOURCEDID = '" + classSourcedId + "'";
      ResultSet Rs = client.getData(SQL);
      try {   
      while (Rs.next()) {   
  
    	  	Map<String, String> metadata = new HashMap<>();
        	  		metadata.put(Vocabulary.CLASS_START_DATE, Rs.getString("CLASS_START_DATE"));
					metadata.put(Vocabulary.CLASS_END_DATE, Rs.getString("CLASS_END_DATE"));
					metadata.put(Vocabulary.SOURCE_SYSTEM, Rs.getString("SOURCE_SYSTEM"));

					unicon.matthews.oneroster.Class class1
					= new unicon.matthews.oneroster.Class.Builder()
						.withSourcedId(Rs.getString("CLASSSOURCEDID"))
						.withTitle(Rs.getString("TITLE"))
						.withMetadata(metadata)
						.withStatus(Status.active)
						.build();
					
					classes.put(Rs.getString("CLASSSOURCEID"), class1);
      }
      }
      catch (SQLException e) {
			e.printStackTrace();
		}
		return classes.get(classSourcedId);
}

}