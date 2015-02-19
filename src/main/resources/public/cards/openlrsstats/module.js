(function() {
'use strict';
    
angular
.module('od.cards.openlrsstats', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('openlrsstats',{
        title: 'OpenLRS Stats',
        description: 'Use this card to view your course activity stats from OpenLRS',
        imgUrl: '/cards/openlrs/openlrs.png',
        cardType: 'openlrsstats',
        styleClasses: 'od-card col-xs-12',
        config: [
          {field:'url',fieldName:'URL',fieldType:'url',required:true},
          {field:'key',fieldName:'Key',fieldType:'text',required:true},
          {field:'secret',fieldName:'Secret',fieldType:'text',required:true}
        ]
    });
 })
.controller('OpenLRSStatsCardController', function($scope, $http, _, OpenDashboard_API, OpenLRSService) {
	$scope.isStudent = OpenDashboard_API.getCurrentUser().isStudent();
	$scope.total = null;
	$scope.thisWeek = null;
	$scope.today = null;
	$scope.topActivity = null;
	$scope.topUser = null;
	
	var user = null;
	if ($scope.isStudent) {
		user = OpenDashboard_API.getCurrentUser().user_id;
	}
	
	var handleStatementResponse = function (statements) {
		if (statements) {
			$scope.total = statements.length;
			
			var week = moment().utc().subtract(7,'day');
			var weekOfStatements = statements.filter(function(statement){
			      return moment(statement.stored).isAfter(week);
			});
			$scope.thisWeek = weekOfStatements.length;
			
			var day = moment().utc().subtract(1,'day');
			var dayOfStatements = statements.filter(function(statement){
			      return moment(statement.stored).isAfter(day);
			});
			$scope.today = dayOfStatements.length;
			
			var groupByStudent = function(statement) {
				var actor = statement.actor;
				if (actor.mbox) {
					var mbox = actor.mbox;							
					return mbox;
				}
			};
			var statementCountByStudent = OpenLRSService.groupByAndMap(statements,groupByStudent);
			var topUserObj = _.max(statementCountByStudent,function(obj){return obj.y[0]});
			var statement = _.find(statements,function(s){return s.actor.mbox == topUserObj.x;});
			if (statement) {
				var actor = statement.actor;
				if (actor.name) {
					$scope.topUser = actor.name;
				}
				else if (actor.mbox) {
					$scope.topUser = actor.mbox;
				}
			}
			
			var groupByActivity = function(statement) {
				var obj = statement.object;
				if (obj.id) {
					return obj.id;
				}
			};
			var statementCountByActivity = OpenLRSService.groupByAndMap(statements,groupByActivity);
			var topActivityObj = _.max(statementCountByActivity,function(obj){return obj.y[0]});
			var statement = _.find(statements,function(s){return s.object.id == topActivityObj.x;});
			if (statement) {
				var obj = statement.object;
				if (obj.id) {
					$scope.topActivity = obj.id;
				}
			}

		}
	};
	
	if (user) {
		OpenLRSService.getStatementsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,user)
		.then(handleStatementResponse);
	}
	else {
		OpenLRSService.getStatements($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
		.then(handleStatementResponse);
	}
});
})();
