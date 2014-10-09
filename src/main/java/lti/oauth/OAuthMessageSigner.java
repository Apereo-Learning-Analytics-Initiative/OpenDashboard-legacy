/**
 * 
 */
package lti.oauth;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.SortedMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


/**
 * @author ggilbert
 *
 */
public class OAuthMessageSigner {
	
	private Logger log = Logger.getLogger(OAuthMessageSigner.class);

	/**
	 * This method double encodes the parameter keys and values.
	 * Thus, it expects the keys and values contained in the 'parameters' SortedMap
	 * NOT to be encoded.
	 * 
	 * @param secret
	 * @param algorithm
	 * @param method
	 * @param url
	 * @param parameters
	 * @return oauth signature
	 * @throws Exception
	 */
	public String sign(String secret, String algorithm, String method, 
				String url, SortedMap<String, String> parameters) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec((secret.concat(OAuthUtil.AMPERSAND)).getBytes(),algorithm);
        Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
        mac.init(secretKeySpec);
        
        StringBuilder signatureBase = new StringBuilder(OAuthUtil.percentEncode(method));
        signatureBase.append(OAuthUtil.AMPERSAND);
        
        signatureBase.append(OAuthUtil.percentEncode(url));
        signatureBase.append(OAuthUtil.AMPERSAND);
        
        int count = 0;
        for (String key : parameters.keySet()) {
        	count++;
           	signatureBase.append(OAuthUtil.percentEncode(OAuthUtil.percentEncode(key)));
        	signatureBase.append(URLEncoder.encode(OAuthUtil.EQUAL, OAuthUtil.ENCODING));
        	signatureBase.append(OAuthUtil.percentEncode(OAuthUtil.percentEncode(parameters.get(key))));
        	
        	if (count < parameters.size()) {
        		signatureBase.append(URLEncoder.encode(OAuthUtil.AMPERSAND, OAuthUtil.ENCODING));
        	}        	
        }

		if (log.isDebugEnabled()) {
			log.debug(signatureBase.toString());
		}
		
		byte[] bytes = mac.doFinal(signatureBase.toString().getBytes());
		byte[] encodedMacBytes = Base64.encodeBase64(bytes);
        
		return new String(encodedMacBytes);
	}	
	
	/**
	 * This method double encodes the parameter keys and values.
	 * Thus, it expects the keys and values contained in the 'parameters' SortedMap
	 * NOT to be encoded.
	 * This method also generates oauth_body_hash parameter and adds it to 
	 * 'parameters' SortedMap
	 * 
	 * @param secret
	 * @param algorithm
	 * @param method
	 * @param url
	 * @param parameters
	 * @param requestBody
	 * @return oauth signature
	 * @throws Exception
	 */
	public String signWithBodyHash(String secret, String algorithm, String method, 
				String url, SortedMap<String, String> parameters,
				String requestBody) throws Exception {
        
        byte[] bytes = requestBody.getBytes();
        
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.reset();
        sha.update(bytes);
		byte[] encodedRequestBytes = Base64.encodeBase64(sha.digest());
		
		String oauthBodyHash = new String(encodedRequestBytes);
		
		parameters.put(OAuthUtil.OAUTH_POST_BODY_PARAMETER, oauthBodyHash);
		
		return sign(secret, algorithm, method, url, parameters);
	}


}
