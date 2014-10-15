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
		// TODO context, user
		OpenLRSService.getStatements($scope.card)
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
		getStatements: function(cardInstance,context,user) {
			var token = $window.btoa(cardInstance.config.key+':'+cardInstance.config.secret);
			var url = cardInstance.config.url + '?callback=JSON_CALLBACK'
	    	var promise = $http({
	    		method  : 'GET',
	    		url     : '/api/openlrs/'+cardInstance.id+'/statements',
	    		headers : { 'Content-Type': 'application/json'}
	    	})
	    	.then(function (response) {
	    		if (response.data && response.data.statements) {
		    		return response.data.statements;
	    		}
	    		
	    		return null;
	    	});
			return promise;
		}
	}
});


