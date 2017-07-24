/**
 *
 */
package od.providers.lineitem;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.oneroster.Vocabulary;

/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

@Component("lineitem_jdbc")
public class JdbcLineItemProvider extends JdbcProvider implements LineItemProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcLineItemProvider.class);

  private static final String KEY = "lineitem_jdbc";
  private static final String BASE = "JDBC_LINEITEMS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);

  private Set<LineItem> LineItem;
  
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
  public Set<LineItem> getLineItemsForClass(ProviderData providerData, String classSourcedId) throws ProviderException {
	  LineItem  = new HashSet<>();


	 JdbcClient client = new JdbcClient(providerData);
	 String SQL = "SELECT * FROM VW_OD_LI_FORCLASS WHERE CLASSSOURCEDID = ?";
     ResultSet Rs = client.getData(SQL, classSourcedId);
     try {
         while (Rs.next())
        	 {	     
        	 
        	 LineItem.add(toLineItem(Rs.getString("ITEMSOURCEDID")
        			 				,Status.active
        			 				,Rs.getString("ITEMTITLE")
        			 				,Rs.getString("ITEMDESCRIPTION")
        			 				,LocalDateTime.parse(Rs.getString("ASSIGNDATE"), DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm"))
        			 				,LocalDateTime.parse(Rs.getString("DUEDATE"), DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm"))
        			 	,toClass(Rs.getString("CLASSSOURCEID"), Rs.getString("CLASSTITLE"))	
        			 	,toLineItemCategory(Rs.getString("CATEGORYSOURCEID")
        			 				,Status.active
        			 				,Rs.getString("CATEGORYTITLE"))));

        	 }
		} catch (SQLException e) {
			e.printStackTrace();
		}

     return LineItem;
  }



private LineItem toLineItem(String classSourceId, Status active, String title, String description,
		LocalDateTime assignDate, LocalDateTime dueDate,unicon.matthews.oneroster.Class Klass, unicon.matthews.oneroster.LineItemCategory lineItemCategory) {
		
		unicon.matthews.oneroster.LineItem lineitem 
						= new unicon.matthews.oneroster.LineItem.Builder()
								.withSourcedId(classSourceId)
								.withStatus(Status.active)
								.withTitle(title)
								.withDescription(description)
								.withAssignDate(assignDate)
								.withDueDate(dueDate)
								.withClass(Klass)
								.withCategory(lineItemCategory)
								.build(); 
		
		return lineitem;
	} 

private LineItemCategory toLineItemCategory(String classSourceId, Status active, String title) {
	
	   unicon.matthews.oneroster.LineItemCategory lineitemcategory
	   				= new unicon.matthews.oneroster.LineItemCategory.Builder()
	   							.withSourcedId(classSourceId)
	   							.withStatus(Status.active)
	   							.withTitle(title)
	   							.build();

	return lineitemcategory;
}



}
