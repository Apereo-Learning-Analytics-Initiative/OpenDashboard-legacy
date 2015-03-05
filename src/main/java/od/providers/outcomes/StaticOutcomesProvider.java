/**
 * 
 */
package od.providers.outcomes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import od.model.outcomes.LineItem;
import od.model.outcomes.Result;
import od.providers.ProviderException;
import od.providers.roster.RosterProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component
public class StaticOutcomesProvider implements OutcomesProvider {
	
	@Autowired private RosterProvider rosterProvider;
	
	/* (non-Javadoc)
	 * @see od.providers.outcomes.OutcomesProvider#getOutcomesForCourse(java.util.Map, java.lang.String)
	 */
	@Override
	public Set<LineItem> getOutcomesForCourse(Map<String, String> options,
			String courseId) throws ProviderException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,List<String>> all = null;
		try {
			all = objectMapper.readValue(this.getClass().getResourceAsStream("/rosters.json"),HashMap.class);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		List<String> members = all.get(courseId);
		Set<LineItem> lineItems = new HashSet<LineItem>();
		
		for (int i = 0; i < 10; i++) {
			LineItem lineItem = new LineItem();
			lineItem.setId("li-"+courseId+"-"+i);
			lineItem.setContext(courseId);
			lineItem.setMaximumScore(randInt(0, 100));
			lineItem.setTitle("li-"+i);
			lineItem.setType("Assignment");
			lineItems.add(lineItem);
		}

		for (LineItem lineItem : lineItems) {
			Set<Result> results = new HashSet<Result>();
			for (String member: members) {
				Result result = new Result();
				result.setId(member+"-"+lineItem.getId());
				result.setUser_id(member);
				result.setScore(randomInRange(0, lineItem.getMaximumScore()));
				results.add(result);
			}
			lineItem.setResults(results);
		}
		
		return lineItems;
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
