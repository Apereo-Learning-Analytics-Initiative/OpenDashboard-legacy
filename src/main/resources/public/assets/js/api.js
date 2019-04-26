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
			var ltiJson = window.sessionStorage.getItem('inbound_lti_launch');
			if (ltiJson && ltiJson !== 'undefined') {
			  this.inbound_lti_launch = JSON.parse(ltiJson);
			}
		}
		return this.inbound_lti_launch;
	};
	
	/**
	 * Course
	 */
	
	function getCourse() {
		if (!this.course) {
		  if (this.inbound_lti_launch) {
		    this.course = new this.Course();
		    this.course.fromLTI(this.inbound_lti_launch);
		  }
		  else if (window.sessionStorage) {
		    var courseJson = window.sessionStorage.getItem('od_current_course');	
			if (courseJson && courseJson !== 'undefined') {
			  this.course = JSON.parse(courseJson);
			}
		  }		  
		}
		
		return this.course;
	};
	
	function setCourse(options) {
	  this.course = new this.Course(options);
      if (window.sessionStorage) {
        window.sessionStorage.setItem('od_current_course',JSON.stringify(this.course));
      }
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
	
	function getCurrentUser() {
		if (!this.currentUser) {
		  if (this.inbound_lti_launch) {
			this.currentUser = new this.Member()
			this.currentUser.fromLTI(this.inbound_lti_launch);
		  }
		  else if (window.sessionStorage) {
		    var userJson = window.sessionStorage.getItem('od_current_user');	
		    if (userJson && userJson !== 'undefined') {
		      this.currentUser = JSON.parse(userJson);
		    }
		  }		  
		}
		return this.currentUser;
	};
	
	function setCurrentUser(options) {
	  this.currentUser = new this.Member(options);
      if (window.sessionStorage) {
        window.sessionStorage.setItem('od_current_user',JSON.stringify(this.currentUser));
      }
	}
	
	
	return {
		setInbound_LTI_Launch : setLTIToolLaunch,
		getInbound_LTI_Launch : getLTIToolLaunch,
		getCourse : getCourse,
		setCourse : setCourse,
		getCurrentUser : getCurrentUser,
		setCurrentUser : setCurrentUser,
		createMemberInstance : createMemberInstance,
		createPersonInstance : createPersonInstance
	};
  
} )( window, JSON );