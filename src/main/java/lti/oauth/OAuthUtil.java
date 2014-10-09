package lti.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ggilbert
 *
 */
public class OAuthUtil {
	public static final String CONSUMER_KEY_PARAM = "oauth_consumer_key";
	public static final String TOKEN_PARAM = "oauth_token";
	public static final String SIGNATURE_METHOD_PARAM = "oauth_signature_method";
	public static final String TIMESTAMP_PARAM = "oauth_timestamp";
	public static final String NONCE_PARAM = "oauth_nonce";
	public static final String VERSION_PARAM = "oauth_version";
	public static final String SIGNATURE_PARAM = "oauth_signature";
	public static final String BODY_HASH_PARAM = "oauth_body_hash";

	public static final String AUTH_SCHEME = "OAuth";
	public static final String ENCODING = "UTF-8";
	public static final String AMPERSAND = "&";
	public static final String EQUAL = "=";
	public static final String [] REQUIRED_OAUTH_PARAMETERS = {"oauth_consumer_key",
		"oauth_signature_method", "oauth_signature", "oauth_timestamp", "oauth_nonce", "oauth_version"};
	public static final String OAUTH_POST_BODY_PARAMETER = "oauth_body_hash";

	private static final Pattern AUTHORIZATION = Pattern.compile("\\s*(\\w*)\\s+(.*)");
	private static final Pattern KEYVALUEPAIR = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");

	private static final Map<String, String> algorithms;
	
	static {
		algorithms = new HashMap<String, String>();
		algorithms.put("HMAC-SHA1", "HmacSHA1");
	}

	public static Map<String, String> decodeAuthorization(String authorization) {
		Map<String, String> oauthParameters = new HashMap<String, String>();
		if (authorization != null) {
			Matcher m = AUTHORIZATION.matcher(authorization);
			if (m.matches()) {
				if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
					for (String keyValuePair : m.group(2).split("\\s*,\\s*")) {
						m = KEYVALUEPAIR.matcher(keyValuePair);
						if (m.matches()) {
							String key = OAuthUtil.decodePercent(m.group(1));
							String value = OAuthUtil.decodePercent(m.group(2));
							oauthParameters.put(key, value);
						}
					}
				}
			}
		}
		
		return oauthParameters;
	}
	
	public static String constructAuthorizationHeader(String realm, Map<String, String> parameters) {
		StringBuilder header = new StringBuilder();
		if (realm != null) {
			header.append(" realm=\"").append(OAuthUtil.percentEncode(realm)).append('"');
		}
		
		if (parameters != null && !parameters.isEmpty()) {
			for (String key : parameters.keySet()) {
				if (key.startsWith("oauth_")) {
					String value = parameters.get(key);
					if (value == null) value = "";
					if (header.length() > 0) header.append(",");
					header.append(" ");
					header.append(OAuthUtil.percentEncode(key)).append("=\"");
					header.append(OAuthUtil.percentEncode(value)).append('"');
				}
			}
		}
		
		return AUTH_SCHEME + header.toString();
	}
	
    public static String decodePercent(String s) {
    	try {
    		return URLDecoder.decode(s, ENCODING);
    		// This implements http://oauth.pbwiki.com/FlexibleDecoding
    	} catch (java.io.UnsupportedEncodingException wow) {
    		throw new RuntimeException(wow.getMessage(), wow);
    	}
    }

	public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, ENCODING)
                    // OAuth encodes some characters differently:
                    .replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee.getMessage(), uee);
        }
	}
	
	public static String generateNonce() throws Exception {
		Random rand = SecureRandom.getInstance ("SHA1PRNG");
		return String.valueOf(rand.nextLong());
	}
	
	public static final String mapToJava(String name) {
		String algorithm = algorithms.get(name);
		if (algorithm == null) {
			throw new UnsupportedOperationException("Signature algorithm of " + name + " is unsupported.");
		}
		return algorithm;		
	}
}
