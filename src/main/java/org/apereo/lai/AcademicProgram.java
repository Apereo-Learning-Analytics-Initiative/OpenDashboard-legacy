/**
 * 
 */
package org.apereo.lai;

import java.io.Serializable;

/**
 * @author ggilbert
 *
 */
public interface AcademicProgram extends Serializable {
  String getSubject();
  String getName();
}
