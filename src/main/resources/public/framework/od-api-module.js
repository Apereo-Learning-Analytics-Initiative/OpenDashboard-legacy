
/**
 * OpenDashboard API Module
 */
var OpenDashboardApi = ( function( window, JSON, OpenDashboardModelFactory, undefined ) {
	
	this.inbound_lti_launch = null;
	this.course = null;
	this.currentUser = null;
	
	/**
	 * LTI
	 */
  
	function setLTIToolLaunch(inbound_lti_launch) {
		if (window.sessionStorage) {
			window.sessionStorage.setItem('inbound_lti_launch',JSON.stringify(inbound_lti_launch));
		}
		this.inbound_lti_launch = inbound_lti_launch;
	};
	
	function getLTIToolLaunch() {
		if (!this.inbound_lti_launch) {
			this.inbound_lti_launch = JSON.parse(window.sessionStorage.getItem('inbound_lti_launch'));
		}
		return this.inbound_lti_launch;
	};
	
	/**
	 * Course
	 */
	
	function getCourse() {
		if (!this.course && this.inbound_lti_launch) {
			this.course = OpenDashboardModelFactory.createCourseInstance();
			this.course.fromLTI(this.inbound_lti_launch);
		}
		return this.course;
	};
	
	/**
	 * Roster
	 */
	
	function createMemberInstance (options) {
		return OpenDashboardModelFactory.createMemberInstance(options);
	};
	
	function getCurrentUser() {
		if (!this.currentUser && this.inbound_lti_launch) {
			this.currentUser = OpenDashboardModelFactory.createMemberInstance();
			this.currentUser.fromLTI(this.inbound_lti_launch);
		}
		return this.currentUser;
	};
	
	/**
	 * Events
	 */
		
	 function createEventInstance (options) {
		return OpenDashboardModelFactory.createEventInstance(options);
	};

	/**
	 * Framework
	 */
	
	function createContextMappingInstance (options) {
		return OpenDashboardModelFactory.createContextMappingInstance(options);
	};
	
	
	return {
		setInbound_LTI_Launch : setLTIToolLaunch,
		getInbound_LTI_Launch : getLTIToolLaunch,
		getCourse : getCourse,
		getCurrentUser : getCurrentUser,
		createContextMappingInstance : createContextMappingInstance,
		createMemberInstance : createMemberInstance,
		createEventInstance : createEventInstance
	};
  
} )( window, JSON, OpenDashboardModelFactory );