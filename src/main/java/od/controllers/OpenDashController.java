/**
 * 
 */
package od.controllers;

import javax.servlet.http.HttpServletRequest;

import lti.LaunchRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ggilbert
 *
 */
@Controller
public class OpenDashController {
	
	@RequestMapping(value={"/", ""}, method=RequestMethod.POST)
    public String lti(HttpServletRequest request, Model model) {
		LaunchRequest launchRequest = new LaunchRequest(request.getParameterMap());
		model.addAttribute("inbound_lti_launch_request", launchRequest);
		return "openDashboard";
    }
	
	@RequestMapping(value={"/", "/context/**"}, method=RequestMethod.GET)
    public String routes() {
		return "openDashboard";
    }

}
