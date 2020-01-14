package od.framework.api;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;
import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.PulseClassDetail;
import od.framework.model.PulseDateEventCount;
import od.framework.model.PulseDetail;
import od.framework.model.PulseStudentDetail;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.enrollment.EnrollmentProvider;
import od.providers.events.EventProvider;
import od.providers.lineitem.LineItemProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;
import od.repository.mongo.PulseCacheRepository;
import od.utils.PulseUtility;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.ModelOutput;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.emory.mathcs.backport.java.util.Arrays;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

@RestController
public class MailController {
  
  private static final Logger log = LoggerFactory.getLogger(MailController.class);
   
  //@Autowired
  //private JavaMailSender javaMailSender;  
  
  @CrossOrigin
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR","ROLE_STUDENT"})
  @RequestMapping(value = "/api/mail", method = RequestMethod.POST, 
      produces = "application/json;charset=utf-8")
  public String sendMail(@RequestParam("recipient") String recipient, @RequestParam("subject") String subject, @RequestParam("text") String text) {
	  
	  //check the email to make sure that it's valid	 
	  OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
	  
	  String senderEmail = authentication.getUserEmail();
	  
	  SimpleMailMessage msg = new SimpleMailMessage();
      msg.setTo(recipient);

      msg.setSubject(subject);
      msg.setText(text);

      //javaMailSender.send(msg);
	  
	  return "OK";
  }
  
}
