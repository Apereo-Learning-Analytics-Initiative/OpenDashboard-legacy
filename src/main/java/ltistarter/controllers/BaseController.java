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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.ui.Model;

/**
 * Common controller methods
 */
public class BaseController {

    final static Logger log = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    CounterService counterService;

    /**
     * Just populate some common model stuff for less repeating
     *
     * @param req       the request
     * @param principal the current security principal (if there is one)
     * @param model     the model
     */
    void commonModelPopulate(HttpServletRequest req, Principal principal, Model model) {
//        model.addAttribute("today", new Date());
//        // TODO real user and pass
//        model.addAttribute("basicUser", "admin");
//        model.addAttribute("basicPass", "admin");
//        // TODO real key and secret?
//        model.addAttribute("oauthKey", "key");
//        model.addAttribute("oauthSecret", "secret");
//        // a little extra request handling stuff
//        model.addAttribute("req", req);
//        model.addAttribute("reqURI", req.getMethod() + " " + req.getRequestURI());
//        // current user
//        model.addAttribute("username", principal != null ? principal.getName() : "ANONYMOUS");
    }

}