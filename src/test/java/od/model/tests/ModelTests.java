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
