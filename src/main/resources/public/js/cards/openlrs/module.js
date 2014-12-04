var OpenLRSCard = angular.module('OpenLRSCard', ['angularCharts']);

OpenLRSCard.controller('OpenLRSCardController', ['$scope', '$http', '$window', 'OpenLRSService', function($scope, $http, $window, OpenLRSService){
    	$scope.statements = null;
    	$scope.data = {};
    	$scope.activeStatement = null;
        $scope.query = '';
		$scope.config = {
		    tooltips: false,
		    labels: true,
		    mouseover: function() {},
		    mouseout: function() {},
		    click: function(d) {
		    	if (d) {
		    		$scope.query = d.data.x;
		    	}    	
		    },
		    legend: {
		      display: false,
		      //could be 'left, right'
		      position: 'left'
		    }
		  };
		
		var user = null;
		if ($scope.isStudent) {
			user = $window.OpenDashboard_API.getUserId();
		}

		OpenLRSService.getStatements($scope.selectedCard,user)
			.then(function (statements) {
		    	$scope.statements = statements;
	    		$scope.data.data = _.chain(statements)
    			.groupBy(function(value){
    				var student = 'unknown';
    				if (value.actor) {
    					if (value.actor.mbox) {
    						student = value.actor.mbox;
    					}
    				}
    				
    				return student;
    			})
    			.map(function(value,key){
    				var numberOfStatements = 0;
    				if (value && value != null) {
    					numberOfStatements = value.length;
    				}
    				
    				return {
    					x:key,
    					y: [numberOfStatements]
    				}
    			})
    			.value();
			});

        $scope.reset = function () {
        	$scope.query = '';
        	$scope.activeStatement = null;
        };
        
        $scope.expandStatement = function (statement) {
        	$scope.activeStatement = statement;
        };
}]);

OpenLRSCard.service('OpenLRSService', function($http, $window) {
	return {
		getStatements: function(cardInstance,user) {
			var url = '/api/openlrs/'+cardInstance.id+'/statements';
			if (user) {
				url = url + '?user='+user;
			}

	    	var promise = $http({
	    		method  : 'GET',
	    		url     : url,
	    		headers : { 'Content-Type': 'application/json'}
	    	})
	    	.then(function (response) {
	    		if (response.data) {
		    		return response.data;
	    		}
	    		
	    		return null;
	    	});
			return promise;
		}
	}
});


