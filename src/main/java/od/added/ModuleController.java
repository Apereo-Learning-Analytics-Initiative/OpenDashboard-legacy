package od.added;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.emory.mathcs.backport.java.util.Arrays;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

@RestController
public class ModuleController {
  
  private static final Logger log = LoggerFactory.getLogger(ModuleController.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Autowired private PulseCacheRepository pulseCacheRepository;

  
  
  
  
  @CrossOrigin
  @RequestMapping(value = "/api/tenants/{tenantId}/modules", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public String pulse(HttpServletResponse response, Authentication authentication, @PathVariable("tenantId") final String tenantId
      )  {
	  
	  return "{\n" + 
	  		"    \"Modules\": [\n" + 
	  		"        {\n" + 
	  		"            \"id\": \"module1\",\n" + 
	  		"            \"label\": \"Detail Activity\",\n" + 
	  		"            \"order\": 1,\n" + 
	  		"            \"configuration\": {\n" + 
	  		"                \"type\": \"Bar\",\n" + 
	  		"                \"table\": {\n" + 
	  		"                    \"inverse\": false,\n" + 
	  		"                    \"cornerHeader\": \"Date\"\n" + 
	  		"                },\n" + 
	  		"                \"options\": {\n" + 
	  		"\n" + 
	  		"                    \"responsive\": true,\n" + 
	  		"                    \"legend\": {\n" + 
	  		"                        \"display\": true\n" + 
	  		"                    },\n" + 
	  		"                    \"maintainAspectRatio\": true,\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"yAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"ticks\": {\n" + 
	  		"                                    \"beginAtZero\": true\n" + 
	  		"                                }\n" + 
	  		"                            }\n" + 
	  		"                        ],\n" + 
	  		"                        \"xAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"stacked\": true\n" + 
	  		"                            }\n" + 
	  		"                        ]\n" + 
	  		"                    }\n" + 
	  		"                }\n" + 
	  		"            },\n" + 
	  		"            \"api\": \"/json/module1.json\"\n" + 
	  		"        },\n" + 
	  		"        {\n" + 
	  		"            \"id\": \"module2\",\n" + 
	  		"            \"label\": \"Activity By Type Compared to Course Averages\",\n" + 
	  		"            \"order\": 2,\n" + 
	  		"            \"configuration\": {\n" + 
	  		"                \"type\": \"Bar\",\n" + 
	  		"                \"table\": {\n" + 
	  		"                    \"inverse\": true\n" + 
	  		"                },\n" + 
	  		"                \"options\": {\n" + 
	  		"                    \"responsive\": true,\n" + 
	  		"                    \"legend\": {\n" + 
	  		"                        \"display\": true,\n" + 
	  		"                        \"position\": \"right\"\n" + 
	  		"                    },\n" + 
	  		"                    \"maintainAspectRatio\": true,\n" + 
	  		"                    \"stacked\": false,\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"yAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"display\": false,\n" + 
	  		"                                \"ticks\": {\n" + 
	  		"                                    \"beginAtZero\": true\n" + 
	  		"                                }\n" + 
	  		"                            }\n" + 
	  		"                        ]\n" + 
	  		"                    }\n" + 
	  		"                }\n" + 
	  		"            },\n" + 
	  		"            \"api\": \"/json/module2.json\"\n" + 
	  		"        },\n" + 
	  		"        {\n" + 
	  		"            \"id\": \"module3\",\n" + 
	  		"            \"label\": \"Activity By Time of Day\",\n" + 
	  		"            \"order\": 3,\n" + 
	  		"            \"configuration\": {\n" + 
	  		"                \"type\": \"Line\",\n" + 
	  		"                \"table\": {\n" + 
	  		"                    \"inverse\": true,\n" + 
	  		"                    \"cornerHeader\": \"Hour\"\n" + 
	  		"                },\n" + 
	  		"                \"options\": {\n" + 
	  		"                    \"responsive\": true,\n" + 
	  		"                    \"legend\": {\n" + 
	  		"                        \"display\": true\n" + 
	  		"                    },\n" + 
	  		"                    \"maintainAspectRatio\": true,\n" + 
	  		"                    \"stacked\": true,\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"yAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"ticks\": {\n" + 
	  		"                                    \"beginAtZero\": true\n" + 
	  		"                                }\n" + 
	  		"                            }\n" + 
	  		"                        ]\n" + 
	  		"                    }\n" + 
	  		"                }\n" + 
	  		"            },\n" + 
	  		"            \"api\": \"/json/module3.json\"\n" + 
	  		"        },\n" + 
	  		"        {\n" + 
	  		"            \"id\": \"module4\",\n" + 
	  		"            \"label\": \"Activity By Day of Week\",\n" + 
	  		"            \"order\": 3,\n" + 
	  		"            \"configuration\": {\n" + 
	  		"                \"type\": \"Line\",\n" + 
	  		"                \"table\": {\n" + 
	  		"                    \"inverse\": true,\n" + 
	  		"                    \"cornerHeader\": \"Activity\"\n" + 
	  		"                },\n" + 
	  		"                \"options\": {\n" + 
	  		"                    \"responsive\": true,\n" + 
	  		"                    \"legend\": {\n" + 
	  		"                        \"display\": true\n" + 
	  		"                    },\n" + 
	  		"                    \"maintainAspectRatio\": true,\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"yAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"ticks\": {\n" + 
	  		"                                    \"beginAtZero\": true\n" + 
	  		"                                }\n" + 
	  		"                            }\n" + 
	  		"                        ],\n" + 
	  		"                        \"xAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"stacked\": true\n" + 
	  		"                            }\n" + 
	  		"                        ]\n" + 
	  		"                    }\n" + 
	  		"                }\n" + 
	  		"            },\n" + 
	  		"            \"api\": \"/json/module4.json\"\n" + 
	  		"        },\n" + 
	  		"        {\n" + 
	  		"            \"id\": \"module5\",\n" + 
	  		"            \"label\": \"Activity By Day of Week\",\n" + 
	  		"            \"order\": 3,\n" + 
	  		"            \"configuration\": {\n" + 
	  		"                \"type\": \"Line\",\n" + 
	  		"                \"table\": {\n" + 
	  		"                    \"inverse\": true,\n" + 
	  		"                    \"cornerHeader\": \"Activity\"\n" + 
	  		"                },\n" + 
	  		"                \"options\": {\n" + 
	  		"\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"xAxes\": [{ \"stacked\": true }],\n" + 
	  		"                        \"yAxes\": [{ \"stacked\": true }]\n" + 
	  		"                      },\n" + 
	  		"\n" + 
	  		"                    \"responsive\": true,\n" + 
	  		"                    \"legend\": {\n" + 
	  		"                        \"display\": true\n" + 
	  		"                    },\n" + 
	  		"                    \"maintainAspectRatio\": true,\n" + 
	  		"                    \"scales\": {\n" + 
	  		"                        \"yAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"ticks\": {\n" + 
	  		"                                    \"beginAtZero\": true\n" + 
	  		"                                }\n" + 
	  		"                            },\n" + 
	  		"                            {\n" + 
	  		"                                \"stacked\": true\n" + 
	  		"                            }\n" + 
	  		"                        ],\n" + 
	  		"                        \"xAxes\": [\n" + 
	  		"                            {\n" + 
	  		"                                \"stacked\": true\n" + 
	  		"                            }\n" + 
	  		"                        ]\n" + 
	  		"                    }\n" + 
	  		"                }\n" + 
	  		"            },\n" + 
	  		"            \"api\": \"/json/module4.json\"\n" + 
	  		"        }\n" + 
	  		"    ]\n" + 
	  		"}";
	}  
  
}
