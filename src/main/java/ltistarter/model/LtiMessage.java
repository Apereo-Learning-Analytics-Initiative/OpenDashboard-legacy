/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.model;


public abstract class LtiMessage {
	//@Required(message="Missing required parameter: lti_message_type") 
	protected String lti_message_type;
	//@Required(message="Missing required parameter: lti_version") 
	protected String lti_version;
	
	/* OAuth Parameters */
	//@Required(message="Missing required parameter: oauth_consumer_key") 
	protected String oauth_consumer_key;
	//@Required(message="Missing required parameter: oauth_signature_method") 
	protected String oauth_signature_method;
	protected String oauth_timestamp;
	protected String oauth_nonce;
	protected String oauth_version;
	//@Required(message="Missing required parameter: oauth_signature") 
	protected String oauth_signature;
	protected String oauth_callback;
	
	public LtiMessage() {}

	public LtiMessage(String lti_message_type, String lti_version,
			String oauth_consumer_key, String oauth_signature_method,
			String oauth_timestamp, String oauth_nonce, String oauth_version,
			String oauth_signature, String oauth_callback) {
		this.lti_message_type = lti_message_type;
		this.lti_version = lti_version;
		this.oauth_consumer_key = oauth_consumer_key;
		this.oauth_signature_method = oauth_signature_method;
		this.oauth_timestamp = oauth_timestamp;
		this.oauth_nonce = oauth_nonce;
		this.oauth_version = oauth_version;
		this.oauth_signature = oauth_signature;
		this.oauth_callback = oauth_callback;
	}
	public String getLti_message_type() {
		return lti_message_type;
	}
	public String getLti_version() {
		return lti_version;
	}
	public String getOauth_consumer_key() {
		return oauth_consumer_key;
	}
	public String getOauth_signature_method() {
		return oauth_signature_method;
	}
	public String getOauth_timestamp() {
		return oauth_timestamp;
	}
	public String getOauth_nonce() {
		return oauth_nonce;
	}
	public String getOauth_version() {
		return oauth_version;
	}
	public String getOauth_signature() {
		return oauth_signature;
	}
	public String getOauth_callback() {
		return oauth_callback;
	}
}
