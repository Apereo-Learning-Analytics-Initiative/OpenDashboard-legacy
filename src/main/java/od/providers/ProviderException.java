/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.providers;

/**
 * @author ggilbert
 *
 */
public class ProviderException extends Exception {

	private static final long serialVersionUID = 1L;
	private String errorCode;
	
	public static final String MISSING_PROVIDER_DATA = "ERROR_PROVIDER_0";
	
	public static final String NO_STAFF_ENTRY_ERROR_CODE = "ERROR_PROVIDER_100";
	public static final String TOO_MANY_STAFF_ENTRIES_ERROR_CODE = "ERROR_PROVIDER_101";
	
	public static final String NO_MODULE_INSTANCES_ERROR_CODE = "ERROR_PROVIDER_200";
	
	public static final String TOO_MANY_VLE_MODULE_MAPS_ERROR_CODE = "ERROR_PROVIDER_300";
	public static final String NO_VLE_MODULE_MAPS_ERROR_CODE = "ERROR_PROVIDER_301";
	
	public static final String NO_STUDENT_MODULE_INSTANCE_ENTRIES_ERROR_CODE = "ERROR_PROVIDER_400";

  public ProviderException() {
    super();
  }

  public ProviderException(String message) {
    super(message);
  }
  
  public ProviderException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

}
