/**
 * 
 */
package org.apereo.lai;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ggilbert
 *
 */
public interface AcademicSession extends Serializable {
  Date getStartDate();
  Date getEndDate();
}
