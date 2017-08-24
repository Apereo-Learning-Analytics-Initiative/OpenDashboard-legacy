/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

package od.providers.lineitem;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Status;

@Component("lineitem_jdbc")
public class JdbcLineItemProvider extends JdbcProvider implements LineItemProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcLineItemProvider.class);

  private static final String KEY = "lineitem_jdbc";
  private static final String BASE = "JDBC_LINEITEMS";
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
  public Set<LineItem> getLineItemsForClass(ProviderData providerData, String classSourcedId) throws ProviderException {

	Set<LineItem>lineItems = new HashSet<LineItem>();
	try {
		JdbcClient client = new JdbcClient(providerData);
		String SQL = "SELECT * FROM VW_OD_LI_FORCLASS WHERE CLASSSOURCEDID = ?";
		try {
			ResultSet Rs = client.getData(SQL, classSourcedId);
			while (Rs.next()){	     
				Status s = Rs.getString("ISACTIVECLASS").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive;
				Class c = new Class.Builder()
							.withSourcedId(Rs.getString("CLASSSOURCEDID"))
							.withTitle(Rs.getString("CLASSTITLE"))
							.withStatus(s)
							.build();
				s = Rs.getString("ISACTIVECATEGORY").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive;
				LineItemCategory lc = new LineItemCategory.Builder()
										.withSourcedId(Rs.getString("LINEITEMCATEGORYSOURCEDID"))
										.withStatus(s)
										.withTitle(Rs.getString("LINEITEMCATEGORYTITLE"))
										.build();
				// Since Line Item is the primary the names are not qualified as is the same for the other providers
				// The two objects are ancillary to the line item and have qualified names (eg: CLASSSOURCEDID)
				s = Rs.getString("ISACTIVE").compareToIgnoreCase("YES") == 0 ? Status.active : Status.inactive;
				LineItem l = new LineItem.Builder()
								.withSourcedId(Rs.getString("SOURCEDID"))
								.withStatus(s)
								.withTitle(Rs.getString("TITLE"))
								.withDescription(Rs.getString("DESCRIPTION"))
								.withAssignDate(LocalDateTime.parse(Rs.getString("ASSIGNDATE"), DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm")))
								.withDueDate(LocalDateTime.parse(Rs.getString("DUEDATE"), DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm")))
								.withClass(c)
								.withCategory(lc)
								.build();
				lineItems.add(l);
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

     return lineItems;
  }

}
