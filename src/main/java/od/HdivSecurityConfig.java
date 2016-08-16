package od;

import org.hdiv.config.annotation.EnableHdivWebSecurity;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHdivWebSecurity
public class HdivSecurityConfig extends HdivWebSecurityConfigurerAdapter {

    @Override
    public void configure(SecurityConfigBuilder builder) {

        // Configuration options
    }

}
