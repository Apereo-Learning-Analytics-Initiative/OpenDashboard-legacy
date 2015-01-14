var OD_API = function(inbound_lti_launch) {
	this.inbound_lti_launch = inbound_lti_launch;
};
OD_API.prototype = {
	getInbound_LTI_Launch: function () {
		if (!this.inbound_lti_launch) {
			if(typeof(sessionStorage) !== "undefined") {
			    this.inbound_lti_launch = JSON.parse(sessionStorage.getItem('inbound_lti_launch'));
			} 
			else {
			    if (console) {
			    	console.log('SessionStorage unavailable');
			    }
			}
		}
		
		return this.inbound_lti_launch;
	},
	
	getCourseName: function() {
		var name = '';
		
		if (this.getInbound_LTI_Launch()) {
			var context_title = this.getInbound_LTI_Launch().context_title;
			if (context_title) {
				name = context_title;
			}
			else {
				name = this.getInbound_LTI_Launch().context_id;
			}
		}

		return name;
	},
	
	getCourseId: function () {
		var courseId = null;
		if (this.getInbound_LTI_Launch()) {
			courseId = this.getInbound_LTI_Launch().context_id;
		}
		return courseId;
	},
	
	getUserId: function () {
		var userId = null;
		if (this.getInbound_LTI_Launch()) {
			userId = this.getInbound_LTI_Launch().user_id;
		}
		return userId;
	},
	
	isInstructor: function() {
		var isInstructor = false;
		var instructorRoles = ['Instructor','TeachingAssistant','Teacher','Faculty'];
		
		if (this.getInbound_LTI_Launch()) {
			var roles = this.getInbound_LTI_Launch().roles;
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
		
		if (this.getInbound_LTI_Launch()) {
			var roles = this.getInbound_LTI_Launch().roles;
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
		
		if (this.getInbound_LTI_Launch()) {
			var roles = this.getInbound_LTI_Launch().roles;
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
