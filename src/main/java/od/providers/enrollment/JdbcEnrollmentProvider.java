/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

package od.providers.enrollment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
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

private Enrollment getEnrollmentFromRS(ResultSet Rs) throws SQLException
{
	Role r = getMathewsRoleFromString(Rs.getString("ROLENAME"));
	Status s = Rs.getString("ISACTIVE").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive;
	User u = new User.Builder()
			.withRole(r)
			.withFamilyName(Rs.getString("FAMILYNAME"))
			.withGivenName(Rs.getString("GIVENNAME"))
			.withStatus(s)
			.withSourcedId(Rs.getString("USERSOURCEDID"))
			.withUserId(Rs.getString("USERID"))
			.build();

	Map<String, String> metadataClass = new HashMap<>();
	metadataClass.put(Vocabulary.CLASS_START_DATE, Rs.getString("CLASS_START_DATE"));
	metadataClass.put(Vocabulary.CLASS_END_DATE, Rs.getString("CLASS_END_DATE"));
	metadataClass.put(Vocabulary.SOURCE_SYSTEM, Rs.getString("SOURCE_SYSTEM"));
	metadataClass.put(Vocabulary.CLASS_STATISTICS,  Rs.getString("CLASS_STATISTICS"));

	Class c = new Class.Builder()
			.withSourcedId(Rs.getString("CLASSSOURCEDID"))
			.withTitle(Rs.getString("TITLE"))
			.withStatus(s)
			.withMetadata(metadataClass)
			.build();

	Map<String, String> metadataEnrollment = new HashMap<>();
	metadataEnrollment.put(Vocabulary.CLASS_STATISTICS,  Rs.getString("ENROLLMENT_STATISTICS"));
	return new Enrollment.Builder()
		 	.withRole(r)
		 	.withStatus(s)
		 	.withUser(u)
		 	.withKlass(c)
		 	.withMetadata(metadataEnrollment)
		 	.build();
}

//***
// * In both the override calls below the returning RS must have both the classSourcedId and userSourcedId in the result set.
// * Both are used in the getEnrollmentFromRS method above
//**

@Override
 public Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, boolean activeOnly) throws ProviderException {
	 studentEnrollments = new HashSet<>();

    try {
		 JdbcClient client = new JdbcClient(providerData);
		 String SQL = "SELECT * FROM VW_OD_EN_FORCLASS WHERE CLASSSOURCEDID = ?";
		 // append to SQL "AND" if we only want active this reduces the record set being returned
		 // rather than filter after transfer (modest speed improvement depending on max row count)
		 // ** Attention should be paid to the query on the DB side to make sure the "WHERE" clauses
		 //    don't create a performance issue 
		 if (activeOnly){
			 SQL += " AND ISACTIVE = 'YES'";
		 }
	     try {
	        ResultSet Rs = client.getData(SQL, classSourcedId);
	    	while (Rs.next()){
	    		studentEnrollments.add(getEnrollmentFromRS(Rs));
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

    return studentEnrollments;

  }
 
  @Override
  public Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId, boolean activeOnly) throws ProviderException {
	 staffEnrollments = new HashSet<Enrollment>();

	 try {
		 JdbcClient client = new JdbcClient(providerData);
		 String SQL = "SELECT * FROM VW_OD_EN_FORUSER WHERE USERSOURCEDID = ?";
		 // see comment in getEnrollmentsForClass above, same applies here.
		 if (activeOnly){
			 SQL += " AND ISACTIVE = 'YES'";
		 }
	     try {
	    	 ResultSet Rs = client.getData(SQL, userSourcedId);	 
	    	 while (Rs.next()) {
	    		 staffEnrollments.add(getEnrollmentFromRS(Rs));
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
	 
	return staffEnrollments;
  }

  public List<String> getUniqueUsersWithRole(ProviderData providerData, String role) throws ProviderException {
    throw new ProviderException("getUniqueTeacherIds not implemented");
  }
}
