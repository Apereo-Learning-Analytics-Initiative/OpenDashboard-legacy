/**
 * 
 */
package org.apereo.lai;

import java.io.Serializable;

/**
 * @author ggilbert
 *
 */
public interface Event extends Serializable {
  String getActor();
  String getVerb();
  String getObject();
}
