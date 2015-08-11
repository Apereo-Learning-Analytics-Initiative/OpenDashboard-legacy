package od.repository.mongo.tests;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import od.Application;
import od.repository.mongo.MongoMultiTenantConfiguration;
import od.test.groups.MongoUnitTests;

@ActiveProfiles("mongo-multitenant")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, MongoMultiTenantConfiguration.class})
@Category(MongoUnitTests.class)
public abstract class MongoTests {

    @Before
    public void setup() {
        //Keeping with test setup.
        //Leave here for future unit tests.
    }

}
