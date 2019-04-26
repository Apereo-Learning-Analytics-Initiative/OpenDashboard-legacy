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
var Constants = (function(window,undefined) {
	var INSTRUCTOR_ROLES = ['Instructor','TeachingAssistant','Teacher','Faculty'];
	var LEARNER_ROLES = ['Student','Learner','Guest'];
	
	return {
		INSTRUCTOR_ROLES : INSTRUCTOR_ROLES,
		LEARNER_ROLES : LEARNER_ROLES
	};
})(window);

(function(OpenDashboardApi, undefined) {
  
	function ContextMapping (options) {
    
	    this.id = null;
	    this.tenantId = null;
	    this.dashboards = null;
	    this.context = null;
	    this.modified = null;
	    
	    if (options) {
	    	this.id = options.id;
	    	this.tenantId = options.tenantId;
	    	this.dashboards = options.dashboards;
	    	this.context = options.context;
	    	this.modified = options.modified;
	    };	    
	    
	    this.addDashboard = function addDashboard(dashboard) {
	    	if (!this.dashboards) {
	    		this.dashboards = [];
	    	}
	    	this.dashboards.push(dashboard);
	    };
	};
  
	OpenDashboardApi.ContextMapping = ContextMapping;
  
})( OpenDashboardApi );

(function(OpenDashboardApi, _, undefined) {
  
	function Course (options) {
    
		this.id = null;
		this.title = null;		
		this.instructors = [];
		this.learners = [];
		this.events = [];
		this.events_median = null;
		
		if (options) {
			this.id = options.id;
			this.title = options.title;		
			this.instructors = options.instructors || [];
			this.learners = options.learners || [];
			this.events = options.events || [];
			this.events_median = options.events_median;
		}
		
	    this.fromLTI = function fromLTI(lti_launch) {
			if (lti_launch) {
				this.instructors = [];
				this.learners = [];
	            if (lti_launch.context_title) {
	            	this.title = lti_launch.context_title;
	            }
	            else {
	            	this.title = lti_launch.context_id;
	            }
	            
	            var member = OpenDashboardApi.createMemberInstance();
	            member.fromLTI(lti_launch);
	            
	            if (member.isInstructor()) {
	            	this.instructors.push(member);
	            }
	            else {
	            	this.learners.push(member);
	            }
			}
	    };
	 
	    this.buildRoster = function buildRoster(members) {
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
	    
	    this.addEvent = function addEvent(event) {
	    	if (!this.events) {
	    		this.events = [];
	    	}
	    	this.events.push(event);
	    };
	    
	};
  
	OpenDashboardApi.Course = Course;
  
})( OpenDashboardApi, _ );

(function(OpenDashboardApi, undefined) {
  
	function Person (options) {
    
		this.contact_email_primary = null;
		this.name_given = null;
		this.name_family = null;
		this.name_full = null;
		this.demographics = null;
	    
	    if(options) {
	    	this.contact_email_primary = options.contact_email_primary;
	    	this.name_given = options.name_given;
	    	this.name_family = options.name_family;
	    	this.name_full = options.name_full;
	    	this.demographics = options.demographics;
	    };
	    
	};
  
	OpenDashboardApi.Person = Person;
  
})( OpenDashboardApi );

(function(OpenDashboardApi, _, undefined) {
  
	function Member (options) {
        this.tenant_id = null;
		this.user_id = null;
		this.user_image = null;
		this.role = null;
		this.roles = null;
		this.person = null;
		this.events = null;
		this.relative_activity_level = null;
		this.risk = null;
		this.last_activity = null;
	    
	    if (options) {
	    	this.tenant_id = options.tenant_id;
	    	this.user_id = options.user_id;
	    	this.user_image = options.user_image;
	    	this.role = options.role;
	    	this.roles = options.roles;
			if (!options.roles) {
				this.roles = options.role;
			}

	    	this.person = options.person;
	    	this.events = options.events;
	    	this.relative_activity_level = options.relative_activity_level;
	    	this.risk = options.risk;
	    	this.last_activity = options.last_activity;
	    };
	    
	    this.fromLTI = function fromLTI(lti_launch) {
			if (lti_launch) {
				this.user_id = lti_launch.user_id;
				this.user_image = lti_launch.user_image;
				this.roles = lti_launch.roles;
				
				var options = {};
				options.contact_email_primary = lti_launch.lis_person_contact_email_primary;
				options.name_given = lti_launch.lis_person_name_given;
				options.name_family = lti_launch.lis_person_name_family;
				options.name_full = lti_launch.lis_person_name_full;

				this.person = OpenDashboardApi.createPersonInstance(options);
			}
		};
		
		this.fromService = function fromService(member) {
			this.user_id = member.user_id;
			this.user_image = member.user_image;
			this.role = member.role;
			this.roles = member.roles;
			
			if (!member.roles) {
				this.roles = member.role;
			}
			
			if (member.person) {			
				var options = {};
				options.contact_email_primary = member.person.contact_email_primary;
				options.name_given = member.person.name_given;
				options.name_family = member.person.name_family;
				options.name_full = member.person.name_full;

				this.person = OpenDashboardApi.createPersonInstance(options);
			}
		};
	    
	    this.isInstructor = function isInstructor() {
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
	};
  
	OpenDashboardApi.Member = Member;
  
})( OpenDashboardApi, _ );

