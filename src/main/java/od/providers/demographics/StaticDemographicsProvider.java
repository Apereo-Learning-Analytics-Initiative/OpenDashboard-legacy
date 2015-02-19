/**
 * 
 */
package od.providers.demographics;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import od.model.roster.Demographic;
import od.providers.ProviderException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component
public class StaticDemographicsProvider implements DemographicsProvider {
	
	private Map<String, Demographic> allDemographicsByUserId = null;
	
	private String [] classCodes = {"FR","SO","JR","SR"};
	private String [] enrollmentStatuses = {"P", "F"};
	private String [] pellStatuses = {"Y", "N"};
	private int [] standings = {0,1,2};
	
	public StaticDemographicsProvider() {
		allDemographicsByUserId = new HashMap<String, Demographic>();
	
		ObjectMapper objectMapper = new ObjectMapper();
		String[][] all = null;
		try {
			all = objectMapper.readValue(this.getClass().getResourceAsStream("/students.json"),String[][].class);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (all != null) {
			for (String [] a : all) {
				Demographic demographic = new Demographic();
				demographic.setAct_composite(randInt(20, 30));
				demographic.setAge(randInt(18, 25));
				
				String classCode = classCodes[randInt(0, 3)];
				int earnedCredits = 0;
				if ("FR".equals(classCode)) {
					earnedCredits = randInt(0,30);
				}
				else if ("SO".equals(classCode)) {
					earnedCredits = randInt(30,60);
				}
				else if ("JR".equals(classCode)) {
					earnedCredits = randInt(60,90);
				}
				else if ("SR".equals(classCode)) {
					earnedCredits = randInt(90, 120);
				}
				
				demographic.setClass_code(classCode);
				demographic.setEarned_credit_hours(earnedCredits);
				demographic.setEnrollment_status(enrollmentStatuses[randInt(0, 1)]);
				demographic.setGender(a[0]);
				demographic.setGpa_cumulative(randomInRange(2.0, 4.0));
				demographic.setGpa_semester(randomInRange(2.0, 4.0));
				demographic.setPell_status(pellStatuses[randInt(0, 1)]);
				demographic.setPercentile(randInt(25, 95));
				demographic.setSat_math(randInt(400,680));
				demographic.setSat_verbal(randInt(400,680));
				demographic.setStanding(standings[randInt(0, 2)]);
				demographic.setUser_id(a[1]);
				
				allDemographicsByUserId.put(demographic.getUser_id(), demographic);
			}
		}
	}

	/* (non-Javadoc)
	 * @see od.providers.demographics.DemographicsProvider#getDemographics(java.util.Map)
	 */
	@Override
	public Set<Demographic> getDemographics(Map<String, String> options)
			throws ProviderException {
		return new HashSet<Demographic>(allDemographicsByUserId.values());
	}

	@Override
	public Demographic getDemographicsForUser(Map<String, String> options,
			String userId) throws ProviderException {
		return allDemographicsByUserId.get(userId);
	}

	private double randomInRange(double min, double max) {
		double range = max - min;
		double scaled = new Random() .nextDouble() * range;
		double shifted = scaled + min;
		return new BigDecimal(shifted).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); // == (rand.nextDouble() * (max-min)) + min;
	}
	
	private int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}
