
/**
 * OpenDashboard API Module
 */
var OpenDashboardApi = ( function( window, JSON, undefined ) {
	
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
			this.course = new this.Course();
			this.course.fromLTI(this.inbound_lti_launch);
		}
		return this.course;
	};
	
	/**
	 * Roster
	 */
	
	function createMemberInstance (options) {
		return new this.Member(options);
	};
	
	function createPersonInstance (options) {
		return new this.Person(options);
	};
	
	function createDemographicsInstance (options) {
		return new this.Demographics(options);
	};
	
	function getCurrentUser() {
		if (!this.currentUser && this.inbound_lti_launch) {
			this.currentUser = new this.Member()
			this.currentUser.fromLTI(this.inbound_lti_launch);
		}
		return this.currentUser;
	};
	
	/**
	 * Events
	 */
		
	 function createEventInstance (options) {
		return new this.Event(options);
	};

	/**
	 * Framework
	 */
	
	function createContextMappingInstance (options) {
		return new this.ContextMapping(options);
	};
	
	
	return {
		setInbound_LTI_Launch : setLTIToolLaunch,
		getInbound_LTI_Launch : getLTIToolLaunch,
		getCourse : getCourse,
		getCurrentUser : getCurrentUser,
		createContextMappingInstance : createContextMappingInstance,
		createMemberInstance : createMemberInstance,
		createPersonInstance : createPersonInstance,
		createEventInstance : createEventInstance,
		createDemographicsInstance : createDemographicsInstance
	};
  
} )( window, JSON );