package od.model.tests;

import od.AbstractTest;
import od.test.groups.ModelUnitTests;

import org.junit.Before;
import org.junit.experimental.categories.Category;

import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

@Category(ModelUnitTests.class)
public abstract class ModelTests extends AbstractTest {

    protected PojoValidator modelValidator;

    @Before
    public void setup() {
        modelValidator = new PojoValidator();

        // Create Rules to validate structure for MODEL_PACKAGE
        modelValidator.addRule(new NoPublicFieldsRule());
        modelValidator.addRule(new NoStaticExceptFinalRule());
        modelValidator.addRule(new GetterMustExistRule());
        modelValidator.addRule(new SetterMustExistRule());

        // Create Testers to validate behavior for MODEL_PACKAGE
        modelValidator.addTester(new SetterTester());
        modelValidator.addTester(new GetterTester());
    }

}
