package od.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PulseUtility {
	public static Map<String, String> ESCAPE_CODES = new HashMap<>();
	
	static {
		ESCAPE_CODES.put(":", "_semi_");
		ESCAPE_CODES.put("@", "_ampr_");
		ESCAPE_CODES.put(".", "_peri_");		
	}
	
	public static String escapeForPulse(String t) {		
		String returnVal = t;
        for (Map.Entry<String, String> pair: ESCAPE_CODES.entrySet()) {
        	returnVal = StringUtils.replace(returnVal,pair.getKey(),pair.getValue());            
        }
		return returnVal;
	}
	
	public static String cleanFromPulse(String t) {
		String returnVal = t;
        for (Map.Entry<String, String> pair: ESCAPE_CODES.entrySet()) {
        	returnVal = StringUtils.replace(returnVal,pair.getValue(),pair.getKey());            
        }
		return returnVal;
		
	}
}
