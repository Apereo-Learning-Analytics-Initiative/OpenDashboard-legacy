(function(angular){
	'use strict';
	
	angular
	.module('OpenDashboard')
	.directive('loadingWidget', function ($timeout, requestNotification) {
		return {
		    restrict: "AC",
		    link: function (scope, element) {
		        // hide the element initially
		        element.hide();
		
		        var promise = null;
		        
		        //subscribe to listen when a request starts
		        requestNotification.subscribeOnRequestStarted(function () {
		            // show the spinner after a delay to avoid screen flashing
		        	if (! promise) {
		        		promise = $timeout(function() {element.show();}, 500);
		        	}
		        });
		
		        requestNotification.subscribeOnRequestEnded(function () {
		            // hide the spinner if there are no more pending requests
		            if (requestNotification.getRequestCount() === 0) {
		            	if (promise) {
		            		$timeout.cancel(promise);
		            		promise = null;
		            	}
		            	element.hide();
		            }
		        });
		    }
		};
	});
})(angular);