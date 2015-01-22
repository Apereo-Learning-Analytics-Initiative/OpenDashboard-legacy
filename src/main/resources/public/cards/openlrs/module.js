(function() {
'use strict';
    
angular
.module('od.cards.openlrs', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('openlrs',{
        title: 'OpenLRS',
        description: 'Use this card to view your course activity data available in OpenLRS',
        imgUrl: '/cards/openlrs/openlrs.png',
        cardType: 'openlrs',
        styleClasses: 'od-card col-xs-12',
        config: [
          {field:'url',fieldName:'URL',fieldType:'url',required:true},
          {field:'key',fieldName:'Key',fieldType:'text',required:true},
          {field:'secret',fieldName:'Secret',fieldType:'text',required:true}
        ]
    });
 })
.controller('OpenLRSCardController', function($scope, $http, _, OpenDashboard_API, OpenLRSService) {
    $scope.statements = null;
    $scope.data = {};
    $scope.subset = null;
    $scope.activeStatement = null;
    $scope.groupedBy = 'DATE';
    $scope.groupedByValue = null;
    $scope.courseName = OpenDashboard_API.getCourseName();
    $scope.queryString = null;
    
	$scope.config = {
	    tooltips: false,
	    labels: false,
	    mouseover: function() {},
	    mouseout: function() {},
	    click: function(event) {
	    	if (event && event.target) {
	    		$scope.queryString = null;
	    		$scope.activeStatement = null;
	    		
	    		var data = event.target.__data__;
	    		if (data) {
	    			var group = _.find($scope.data.data, function(g) {
	    				return g.x == data.x;
	    			});
	    			if (group.u) {
	    				$scope.groupedByValue = group.u;
	    			}
	    			else {
	    				$scope.groupedByValue = group.x;
	    			}
	    			
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
	if (OpenDashboard_API.isStudent()) {
		user = OpenDashboard_API.getUserId();
	}
	
	var handleStatementResponse = function (statements) {
    	$scope.statements = statements;
		$scope.data.data = _.sortBy(OpenLRSService.groupByAndMap($scope.statements), function(obj){return obj.x;});
	};
	
	if (user) {
		OpenLRSService.getStatementsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,user)
		.then(handleStatementResponse);
	}
	else {
		OpenLRSService.getStatements($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
		.then(handleStatementResponse);
	}
		
	$scope.reset = function () {
		$scope.queryString = null;
		$scope.activeStatement = null;
	};
	
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

            var sorted = _.sortBy(OpenLRSService.groupByAndMap($scope.statements,groupByStudent), function(obj){return obj.x;});
            angular.forEach(sorted, function(value,key){
                value.u = value.x;
                var x = value.x.split(':')[1];
                value.x = x.split('@')[0];                       
            });
            $scope.data.data = sorted;
        }
        else {
            $scope.data.data = _.sortBy(OpenLRSService.groupByAndMap($scope.statements), function(obj){return obj.x;});
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
        $scope.activeStatement = JSON.stringify(statement,undefined,2);
    };
});
})();
