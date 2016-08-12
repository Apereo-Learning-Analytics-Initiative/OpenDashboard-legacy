package od.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="lrs_options")
//@PropertySource(value = "classpath:application.yml")
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
public class LRS_Options {
	
	private String group_id_prependString;

	public String getGroup_id_prependString() {
		return group_id_prependString;
	}

	public void setGroup_id_prependString(String group_id_prependString) {
		this.group_id_prependString = group_id_prependString;
	}

}
