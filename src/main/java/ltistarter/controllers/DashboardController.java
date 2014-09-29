/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This is the default home (i.e. root or "/") controller which should be wide open
 * (no security)
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @RequestMapping(method = RequestMethod.GET)
    @Secured("ROLE_LTI")
    public String index(HttpServletRequest req, Principal principal, Model model) {
        return "dashboard"; // name of the template
    }

}