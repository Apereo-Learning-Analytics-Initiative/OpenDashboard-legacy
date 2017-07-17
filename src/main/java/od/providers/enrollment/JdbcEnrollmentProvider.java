/**
 *
 */
package od.providers.enrollment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.course.JdbcCourseProvider;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

import java.sql.*;

/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

@Component("roster_jdbc")
public class JdbcEnrollmentProvider extends JdbcProvider implements EnrollmentProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcEnrollmentProvider.class);

  private static final String KEY = "roster_jdbc";
  private static final String BASE = "JDBC_ROSTER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);

  private Set<Enrollment> studentEnrollments;
  private Set<Enrollment> staffEnrollments;

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

public Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, boolean activeOnly) throws ProviderException {
	  studentEnrollments = new HashSet<>();


	 JdbcClient client = new JdbcClient(providerData);
	 String SQL = "select * from VW_OD_EN_ForClass where CLASSSOURCEID = '" + classSourcedId + "'";
     ResultSet Rs = client.getData(SQL);
     try {
         while (Rs.next())
        	 {
        	 studentEnrollments.add(toEnrollment(Role.student
			            		  				, toUser(Role.student
			            		  						,Rs.getString("LAST_NAME")
			            		  						,Rs.getString("FIRST_NAME")
			            		  						,Rs.getString("USER_ID")
			            		  						,Rs.getString("USER_ID"))
			            		  				, toClass(Rs.getString("CLASSSOURCEID"), Rs.getString("TITLE"))));
             }
		} catch (SQLException e) {
			e.printStackTrace();
		}

     return studentEnrollments;

  }

  @Override
  public Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId, boolean activeOnly) throws ProviderException {
	  staffEnrollments = new HashSet<Enrollment>();

	 JdbcClient client = new JdbcClient(providerData);
	 String SQL = "select * from VW_OD_EN_FORUSER where CLASSSOURCEID = 'MATH_115L_118_201440'";
     ResultSet Rs = client.getData(SQL);
     try {
    	 while (Rs.next())
    	 {
    		 staffEnrollments.add(toEnrollment(Role.teacher,
        		  				toUser(Role.teacher
        		  						,Rs.getString("LAST_NAME")
        		  						,Rs.getString("FIRST_NAME")
        		  						,Rs.getString("USER_ID")
        		  						,Rs.getString("USER_ID"))
        		  				,toClass(Rs.getString("CLASSSOURCEID"), Rs.getString("TITLE"))));
         }

		} catch (SQLException e) {
			e.printStackTrace();
		}
	return staffEnrollments;
  }




}
