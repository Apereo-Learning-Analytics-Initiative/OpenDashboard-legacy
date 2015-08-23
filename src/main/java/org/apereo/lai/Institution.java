/**
 * 
 */
package org.apereo.lai;

import java.io.Serializable;

/**
 * @author ggilbert
 *
 */
public interface Institution extends Serializable {

  String getName();
  String getKey();
  String getSecret();
}
