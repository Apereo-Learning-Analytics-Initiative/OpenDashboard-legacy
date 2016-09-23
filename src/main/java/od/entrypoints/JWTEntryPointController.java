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
package od.entrypoints;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ggilbert
 *
 */
@Controller
public class JWTEntryPointController {
  
  @Autowired private EntryPointProcessor entryPointProcessor;
  
  @RequestMapping(value = "/jwtlogin", method = RequestMethod.POST)
  public String post(HttpServletRequest request, @RequestParam(value="tenantId", required=true) final String tenantId)  {
    return entryPointProcessor.post(request, tenantId);
  }
  
  @RequestMapping(value = "/jwtlogin/{tenantId}", method = RequestMethod.GET)
  public String get(HttpServletRequest request, @PathVariable(value = "tenantId") final String tenantId) {
    return entryPointProcessor.get(request, tenantId);
  }
  
}
