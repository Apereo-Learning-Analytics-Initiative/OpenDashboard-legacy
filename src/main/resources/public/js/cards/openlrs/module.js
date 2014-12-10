(function() {
	'use strict';
	
	angular
		.module('OpenLRSCard', ['angularCharts', 'underscore', 'OpenDashboardAPI'])
		.controller('OpenLRSCardController', function($scope, $http, _, OpenDashboard_API, OpenLRSService){		
	    	$scope.statements = null;
	    	$scope.data = {};
	    	$scope.subset = null;
	    	$scope.activeStatement = null;
	    	$scope.groupedBy = 'DATE';
	    	$scope.groupedByValue = null;
	    	$scope.courseName = OpenDashboard_API.getCourseName();
	        $scope.query = '';
	        
			$scope.config = {
			    tooltips: false,
			    labels: true,
			    mouseover: function() {},
			    mouseout: function() {},
			    click: function(event) {
			    	if (event && event.target) {
			    		var data = event.target.__data__;
			    		if (data) {
			    			var group = _.find($scope.data.data, function(g) {
			    				return g.x == data.x;
			    			});
			    			$scope.groupedByValue = group.x;
			    			$scope.subset =
			    				_.filter($scope.statements, function(statement){
			    					if ($scope.groupedBy == 'DATE') {
			    						var stored = statement.stored;
			    			    		var dateOnly = stored.slice(0, stored.indexOf("T"));
			    			    		return dateOnly == $scope.groupedByValue;
			    					}
			    					else {
			    						var actor = statement.actor;
			    						if (actor.mbox) {
			    							var mbox = actor.mbox;			    	
			    							return mbox == $scope.groupedByValue;
			    						}
			    					}
			    				});
			    		}
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
				user = OpenDashboard_API.getUserId();
			}
	
			OpenLRSService.getStatements($scope.contextMappingId,$scope.cardInstanceId,user)
				.then(function (statements) {
			    	$scope.statements = statements;
		    		$scope.data.data = OpenLRSService.groupByAndMap($scope.statements);
				});
			

			
			$scope.groupBy = function (groupedBy) {
				$scope.groupedBy = groupedBy;
				
				if ($scope.groupedBy == 'STUDENT') {
					var groupByStudent = function(statement) {
						var actor = statement.actor;
						if (actor.mbox) {
							var mbox = actor.mbox;							
							return mbox;
						}
					};

					$scope.data.data = OpenLRSService.groupByAndMap($scope.statements,groupByStudent);
				}
				else {
					$scope.data.data = OpenLRSService.groupByAndMap($scope.statements);
				}
				
			};
			
			$scope.getStatementUser = function(statement) {
				var actor = statement.actor;
				if (actor.name) {
					return actor.name;
				}
				else if (actor.mbox) {
					return actor.mbox;
				}
			};
	
	        $scope.expandStatement = function (statement) {
	        	$scope.activeStatement = JSON.stringify(statement,null,'\t');
	        };
	})
	.service('OpenLRSService', function($http, _) {
		return {
			getStatements: function(contextMappingId,cardInstanceId,user) {
				var url = '/api/'+contextMappingId+'/openlrs/statements';
				var params = {};
				
				if (user || cardInstanceId) {
					if (user) {params.user = user;};
					if (cardInstanceId) {params.cardInstanceId = cardInstanceId;};
				}
	
		    	var promise = $http({
		    		method  : 'GET',
		    		url     : url,
		    		headers : { 'Content-Type': 'application/json'},
		    		params  : params
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
			    		return response.data;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			},
			groupByAndMap: function(statements,groupByFunction,mapFunction) {
				
				if (!groupByFunction) {
					groupByFunction = function (statement) {
						//by default groupBy stored date
						var stored = statement.stored;
			    		return stored.slice(0, stored.indexOf("T"));
					};
				}
				
				if (!mapFunction) {
					mapFunction = function(value,key){
						// by default map to the number of statements for the value
	    				var numberOfStatements = 0;
	    				if (value && value != null) {
	    					numberOfStatements = value.length;
	    				}
	    				
	    				return {
	    					x:key,
	    					y: [numberOfStatements]
	    				};
	    			};
				}
				
				return _.chain(statements)
						.groupBy(groupByFunction)
						.map(mapFunction)
						.value();
			}
		}
	});
})();
