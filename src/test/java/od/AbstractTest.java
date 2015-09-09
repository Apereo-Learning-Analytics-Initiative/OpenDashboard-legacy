package od;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenDashboard.class, SecurityConfig.class})
@WebAppConfiguration
public abstract class AbstractTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Little helper method to log the JSON of an object in pretty-print for debug/test purposes
     * @param obj
     * @throws IOException
     */
    protected void asJson(Object obj) throws IOException {
        logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
    }

    // There isn't an anyClass implementation so these methods provide that functionality
    protected static class AnyClassMatcher extends ArgumentMatcher<Class<?>> {
        @Override
        public boolean matches(final Object argument) {
            return true;
        }
    }

    protected Class<?> anyClass() {
        return Mockito.argThat(new AnyClassMatcher());
    }
}
