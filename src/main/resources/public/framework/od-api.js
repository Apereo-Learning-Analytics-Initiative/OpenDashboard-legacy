var OD_API = function(inbound_lti_launch) {
	
	if (!inbound_lti_launch) {
        if(typeof(sessionStorage) !== "undefined") {
            this.inbound_lti_launch = JSON.parse(sessionStorage.getItem('inbound_lti_launch'));
        } 
	}
	else {
		this.inbound_lti_launch = inbound_lti_launch;
	}
    
    this.course = new Course();
    this.course.fromLTI(this.inbound_lti_launch);
    
    this.currentUser = new Member();
    this.currentUser.fromLTI(this.inbound_lti_launch);
};
OD_API.prototype = {
    getInbound_LTI_Launch: function () {        
        return this.inbound_lti_launch;
    },
    getCourse: function () {
    	return this.course;
    },
    getCurrentUser: function () {
    	return this.currentUser;
    }
};
