/**
 *
 */
package od.controllers;

import java.util.Calendar;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import lti.LaunchRequest;
import od.model.Session;
import od.repository.SessionRepositoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ggilbert
 *
 */
@Controller
public class OpenDashController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(OpenDashController.class);
    @Autowired private SessionRepositoryInterface sessionRepository;

    @RequestMapping(value={"/"}, method=RequestMethod.POST)
    public String lti(HttpServletRequest request, Model model) {
        LaunchRequest launchRequest = new LaunchRequest(request.getParameterMap());
        model.addAttribute("inbound_lti_launch_request", launchRequest);

        Session session = new Session();
        session.setData(Collections.singletonMap("lti", launchRequest));
        session.setTimestamp(Calendar.getInstance().getTimeInMillis());

        Session s = sessionRepository.save(session);
        model.addAttribute("token", s.getId());

        return "od";
    }

    @RequestMapping(value={"/", "/cm/**"}, method=RequestMethod.GET)
    public String routes(Model model) {
        return "od";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value={"/error"})
    public String error(HttpServletRequest request) {
        return "error";
    }
}
