package od.model.tests;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.affirm.Affirm;

public class RedisModelTests extends ModelTests {
    // Configured for expectation, so we know when a class gets added or removed.
    private static final int EXPECTED_CLASS_COUNT = 2;

    // The package to test
    private static final String MODEL_PACKAGE = "od.repository.redis.model";

    private List<PojoClass> modelClasses;

    @Override
    @Before
    public void setup() {
        super.setup();
        modelClasses = PojoClassFactory.getPojoClasses(MODEL_PACKAGE);
    }


    @Test
    public void ensureExpectedModelCount() {
        Affirm.affirmEquals("Classes added / removed?", EXPECTED_CLASS_COUNT, modelClasses.size());
    }

    @Test
    public void testModelStructureAndBehavior() {
        for (PojoClass pojoClass : modelClasses) {
            if(!pojoClass.getName().endsWith("List")) {
                modelValidator.runValidation(pojoClass);
            }
        }
    }
}
