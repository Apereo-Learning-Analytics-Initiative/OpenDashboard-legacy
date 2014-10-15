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
	}

};
