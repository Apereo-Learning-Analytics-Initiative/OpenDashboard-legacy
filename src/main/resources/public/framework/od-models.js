var Constants = (function(window,undefined) {
	var INSTRUCTOR_ROLES = ['Instructor','TeachingAssistant','Teacher','Faculty'];
	var LEARNER_ROLES = ['Student','Learner','Guest'];
	
	return {
		INSTRUCTOR_ROLES : INSTRUCTOR_ROLES,
		LEARNER_ROLES : LEARNER_ROLES
	};
})(window);

var OpenDashboardModelFactory = (function(window,undefined){
	
	function createContextMappingInstance (options) {
		var contextMapping = new this.ContextMapping();
		if (options) {
			contextMapping.init(options);
		}
		return contextMapping;
	};
	
	function createCourseInstance (options) {
		var course = new this.Course();
		if (options) {
			course.init(options);
		}
		return course;
	};
	
	function createPersonInstance (options) {
		var person = new this.Person();
		if (options) {
			person.init(options);
		}
		return person;
	};
	
	function createMemberInstance (options) {
		var member = new this.Member();
		if (options) {
			member.init(options);
		}
		return member;
	};
	
	function createDemographicsInstance (options) {
		var demographics = new this.Demographics();
		if (options) {
			demographics.init(options);
		}
		return demographics;
	};
	
	function createEventInstance (options) {
		var event = new this.Event();
		if (options) {
			event.init(options);
		}
		return event;
	};
	
	return {
		createContextMappingInstance : createContextMappingInstance,
		createCourseInstance : createCourseInstance,
		createPersonInstance : createPersonInstance,
		createMemberInstance : createMemberInstance,
		createDemographicsInstance : createDemographicsInstance,
		createEventInstance : createEventInstance
	}
})(window);

(function(OpenDashboardModelFactory, undefined) {
  
	function ContextMapping () {
    
	    this.id = null;
	    this.key = null;
	    this.dashboards = null;
	    this.context = null;
	    this.modified = null;
	    
	    function init(options) {
		    this.id = options.id;
		    this.key = options.key;
		    this.dashboards = options.dashboards;
		    this.context = options.context;
		    this.modified = options.modified;
	    };
	    
	    return {
	    	init : init
	    }
	};
  
	OpenDashboardModelFactory.ContextMapping = ContextMapping;
  
})( OpenDashboardModelFactory );

(function(OpenDashboardModelFactory, _, undefined) {
  
	function Course () {
    
		this.id = null;
		this.title = null;		
		this.instructors = [];
		this.learners = [];
		this.events = [];
		this.events_median = null;

		function init(options) {
			this.id = options.id;
			this.title = options.title;		
			this.instructors = options.instructors || [];
			this.learners = options.learners || [];
			this.events = options.events || [];
			this.events_median = options.events_median;
	    };
	    
	    function fromLTI(lti_launch) {
			if (lti_launch) {
				this.instructors = [];
				this.learners = [];
				this.id = lti_launch.context_id;
				var context_title = lti_launch.context_title;
	            if (context_title) {
	                this.title = lti_launch.context_title;
	            }
	            else {
	                this.title = lti_launch.context_id;
	            }
	            
	            var member = OpenDashboardModelFactory.createMemberInstance();
	            member.fromLTI(lti_launch);
	            
	            if (member.isInstructor()) {
	            	this.instructors.push(member);
	            }
	            else {
	            	this.learners.push(member);
	            }
			}
	    };
	 
	    function buildRoster(members) {
			this.instructors = [];
			this.learners = [];
			_.forEach(members,function(member){
	            if (member.isInstructor()) {
	            	this.instructors.push(member);
	            }
	            else {
	            	this.learners.push(member);
	            }
			},this);
	    };
	    
	    return {
	    	init : init,
	    	fromLTI : fromLTI,
	    	buildRoster : buildRoster
	    }
	};
  
	OpenDashboardModelFactory.Course = Course;
  
})( OpenDashboardModelFactory, _ );

(function(OpenDashboardModelFactory, undefined) {
  
	function Person () {
    
		this.contact_email_primary = null;
		this.name_given = null;
		this.name_family = null;
		this.name_full = null;
		this.demographics = null;
	    
	    function init(options) {
	    	this.contact_email_primary = options.contact_email_primary;
	    	this.name_given = options.name_given;
	    	this.name_family = options.name_family;
	    	this.name_full = options.name_full;
	    	this.demographics = options.demographics;
	    };
	    
	    return {
	    	init : init
	    }
	};
  
	OpenDashboardModelFactory.Person = Person;
  
})( OpenDashboardModelFactory );

(function(OpenDashboardModelFactory, undefined) {
  
	function Demographics () {
    
		this.user_id = null;
		this.percentile = null;
		this.sat_verbal = null;
		this.sat_math = null;
		this.act_composite = null;
		this.age = null;
		this.race = null;
		this.gender = null;
		this.enrollment_status = null;
		this.earned_credit_hours = null;
		this.gpa_cumulative = null;
		this.gpa_semester = null;
		this.standing = null;
		this.pell_status = null;
		this.class_code = null;
	    
	    function init(options) {
	    	this.user_id = options.user_id;
	    	this.percentile = options.percentile;
	    	this.sat_verbal = options.sat_verbal;
	    	this.sat_math = options.sat_math;
	    	this.act_composite = options.act_composite;
	    	this.age = options.age;
	    	this.race = options.race;
	    	this.gender = options.gender;
	    	this.enrollment_status = options.enrollment_status;
	    	this.earned_credit_hours = options.earned_credit_hours;
	    	this.gpa_cumulative = options.gpa_cumulative;
	    	this.gpa_semester = options.gpa_semester;
	    	this.standing = options.standing;
	    	this.pell_status = options.pell_status;
	    	this.class_code = options.class_code;
	    };
	    
	    function fromService(demographic) {
	    	init(demographic);
	    };
	    
	    return {
	    	init : init,
	    	fromService : fromService
	    }
	};
  
	OpenDashboardModelFactory.Demographics = Demographics;
  
})( OpenDashboardModelFactory );

(function(OpenDashboardModelFactory, _, undefined) {
  
	function Member () {
    
		this.user_id = null;
		this.user_image = null;
		this.role = null;
		this.roles = null;
		this.person = null;
		this.events = null;
		this.relative_activity_level = null;
		this.risk = null;
		this.last_activity = null;
	    
	    function init(options) {
	    	this.user_id = options.user_id;
	    	this.user_image = options.user_image;
	    	this.role = options.role;
	    	this.roles = options.roles;
	    	this.person = options.person;
	    	this.events = options.events;
	    	this.relative_activity_level = options.relative_activity_level;
	    	this.risk = options.risk;
	    	this.last_activity = options.last_activity;
	    };
	    
	    function fromLTI(lti_launch) {
			if (lti_launch) {
				this.user_id = lti_launch.user_id;
				this.user_image = lti_launch.user_image;
				this.roles = lti_launch.roles;
				
				var options = {};
				options.contact_email_primary = lti_launch.lis_person_contact_email_primary;
				options.name_given = lti_launch.lis_person_name_given;
				options.name_family = lti_launch.lis_person_name_family;
				options.name_full = lti_launch.lis_person_name_full;

				this.person = OpenDashboardModelFactory.createPersonInstance(options);
			}
		};
		
		function fromService(member) {
			this.user_id = member.user_id;
			this.user_image = member.user_image;
			this.role = member.role;
			this.roles = member.roles;
			
			if (member.person) {			
				var options = {};
				options.contact_email_primary = member.person.contact_email_primary;
				options.name_given = member.person.name_given;
				options.name_family = member.person.name_family;
				options.name_full = member.person.name_full;

				this.person = OpenDashboardModelFactory.createPersonInstance(options);
			}
		};
	    
	    function isInstructor() {
	        var isInstructor = false;
	        
	        if (this.roles) {
	            var roleArray = null;
	            if (this.roles.indexOf(',') > -1) {
	                roleArray = this.roles.split(',');
	            }
	            else {
	                roleArray = [this.roles];
	            }
	            
	            var intersection = _.intersection(Constants.INSTRUCTOR_ROLES,roleArray);
	            if (intersection && intersection.length > 0) {
	                isInstructor = true;
	            }
	        }
	        
	        return isInstructor;
	    };
	    
	    function isStudent() {
	        var isStudent = false;
	        
	        if (this.roles) {
	            var roleArray = null;
	            if (this.roles.indexOf(',') > -1) {
	                roleArray = this.roles.split(',');
	            }
	            else {
	                roleArray = [this.roles];
	            }
	            
	            var intersection = _.intersection(Constants.LEARNER_ROLES,roleArray);
	            if (intersection && intersection.length > 0) {
	                isStudent = true;
	            }
	        }
	        
	        return isStudent;
	    };

	    
	    return {
	    	init : init,
	    	fromLTI : fromLTI,
	    	fromService : fromService,
	    	isInstructor : isInstructor,
	    	isStudent : isStudent
	    }
	};
  
	OpenDashboardModelFactory.Member = Member;
  
})( OpenDashboardModelFactory, _ );

(function(OpenDashboardModelFactory, S, undefined) {
  
	function Event () {
    
		var XAPI = 'XAPI';
		var CALIPER = 'CALIPER';
		
		this.user_id = null;
		this.context_id = null;
		this.organization_id = null;
		this.name_full = null;
		this.type = null;
		this.action = null;
		this.object = null;
		this.raw = null;
		this.timestamp = null;
	    
	    function init(options) {
			this.user_id = options.user_id;
			this.context_id = options.context_id;
			this.organization_id = options.organization_id;
			this.name_full = options.name_full;
			this.type = options.type;
			this.action = options.action;
			this.object = options.object;
			this.raw = options.raw;
			this.timestamp = options.timestamp;
	    };
	    
	    function fromXAPI(xapi) {
			this.type = this.XAPI;
			this.raw = xapi;
			this.timestamp = xapi.timestamp;
			
			if (xapi.actor) {
				var mbox = xapi.actor.mbox;
				if (mbox) {
					var uid = mbox;
					if (S(uid).contains(':')) {
						uid = uid.split(':')[1];
					}
					
					if (S(uid).contains('@')) {
						uid = uid.split('@')[0];
					}
					
					this.user_id = uid;
				}
				
				this.name_full = xapi.actor.name;
			}
			
			if (xapi.verb) {
				var xapiAction = xapi.verb.id;
				var lastSlash = xapiAction.lastIndexOf('/');
				this.action = xapiAction.substr(lastSlash + 1);
			}
			
			if (xapi.object) {
				var xapiObject = xapi.object.definition.type;
				var lastSlash = xapiObject.lastIndexOf('/');
				this.object = xapiObject.substr(lastSlash + 1);
			}
		};

	    return {
	    	init : init,
	    	fromXAPI : fromXAPI
	    }
	};
  
	OpenDashboardModelFactory.Event = Event;
  
})( OpenDashboardModelFactory, S );