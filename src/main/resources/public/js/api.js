var OD_API = function(inbound_lti_launch) {
	this.inbound_lti_launch = inbound_lti_launch;
};
OD_API.prototype = {
	getInbound_LTI_Launch: function () {
		var lti_launch;
		
		if (this.inbound_lti_launch) {
			lti_launch = this.inbound_lti_launch;
			
			if(typeof(sessionStorage) !== "undefined") {
			    sessionStorage.setItem('inbound_lti_launch',JSON.stringify(lti_launch));
			} 
		}
		else {
			if(typeof(sessionStorage) !== "undefined") {
			    lti_launch = JSON.parse(sessionStorage.getItem('inbound_lti_launch'));
			} 
			else {
			    if (console) {
			    	console.log('SessionStorage unavailable');
			    }
			}
		}
		
		return lti_launch;
	},
	
	getUserId: function () {
		var userId = null;
		if (this.inbound_lti_launch) {
			userId = this.inbound_lti_launch.user_id;
		}
		return userId;
	},
	
	isInstructor: function() {
		var isInstructor = false;
		var instructorRoles = ['Instructor','TeachingAssistant','Teacher','Faculty'];
		
		if (this.inbound_lti_launch) {
			var roles = this.inbound_lti_launch.roles;
			if (roles) {
				var roleArray = null;
				if (roles.indexOf(',') > -1) {
					roleArray = roles.split(',');
				}
				else {
					roleArray = [roles];
				}
				
				var intersection = _.intersection(instructorRoles,roleArray);
				if (intersection && intersection.length > 0) {
					isInstructor = true;
				}
			}
			
		}
		
		return isInstructor;
	},
	
	isNotStudent: function() {
		var isNotStudent = false;
		var studentRoles = ['Student','Learner','Guest'];
		
		if (this.inbound_lti_launch) {
			var roles = this.inbound_lti_launch.roles;
			if (roles) {
				var roleArray = null;
				if (roles.indexOf(',') > -1) {
					roleArray = roles.split(',');
				}
				else {
					roleArray = [roles];
				}
		
				var intersection = _.intersection(studentRoles,roleArray);
				if (!intersection || intersection.length == 0) {
					isNotStudent = true;
				}
			}
			
		}
		
		return isNotStudent;
	},
	
	isStudent: function() {
		var isStudent = false;
		var studentRoles = ['Student','Learner','Guest'];
		
		if (this.inbound_lti_launch) {
			var roles = this.inbound_lti_launch.roles;
			if (roles) {
				var roleArray = null;
				if (roles.indexOf(',') > -1) {
					roleArray = roles.split(',');
				}
				else {
					roleArray = [roles];
				}
				
				var intersection = _.intersection(studentRoles,roleArray);
				if (intersection && intersection.length > 0) {
					isStudent = true;
				}
			}
			
		}
		
		return isStudent;
	}

};
