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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class HomeController {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    CounterService counterService;

    @RequestMapping("/")
    public String home(HttpServletRequest req, Model model) {
        commonModelPopulate(req, model);
        model.addAttribute("name", "HOME");
        counterService.increment("home");
        return "home"; // name of the template
    }

    /**
     * Just populate some common model stuff for less repeating
     *
     * @param req   the request
     * @param model the model
     */
    void commonModelPopulate(HttpServletRequest req, Model model) {
        model.addAttribute("today", new Date());
        // TODO real key and secret?
        model.addAttribute("oauthKey", "key");
        model.addAttribute("oauthSecret", "secret");
        // a little extra request handling stuff
        model.addAttribute("req", req);
        model.addAttribute("reqURI", req.getMethod() + " " + req.getRequestURI());

    }

}