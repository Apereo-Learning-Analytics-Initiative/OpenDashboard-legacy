/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package od.model.tests;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.affirm.Affirm;

public class OpenDashModelTests extends ModelTests {
    // Configured for expectation, so we know when a class gets added or removed.
    private static final int EXPECTED_CLASS_COUNT = 14;

    // The package to test
    private static final String MODEL_PACKAGE = "org.apereo.lai";

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
