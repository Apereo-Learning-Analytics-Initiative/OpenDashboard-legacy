(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('museController',[
    '$scope',
    '$rootScope',
    '$http',
    '$q',
    '$timeout',
    '$state',
    'SessionService',
    'museApiService',
    function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, museApiService){
    	"use strict";
      
		var currentUser = SessionService.getCurrentUser();

    	if (currentUser) {  	
    	    var data = museApiService
    	    .getCourseList(currentUser.tenant_id, currentUser.user_id)
    	    .then(function (data){
    	    	var museCourseList = data.enrollments;
    	       	for (var i = 0; i < museCourseList.length; i++){
    	       		if (data.enrollments[i].role == "teacher") {
        	    		data.enrollments[i].statistics = JSON.parse(museCourseList[i].class.metadata["http://unicon.net/vocabulary/v1/classStatistics"]);
    	       		}
    	    		data.enrollments[i].startdate = museCourseList[i].class.metadata["http://unicon.net/vocabulary/v1/classStartDate"];
    	    		data.enrollments[i].enddate = museCourseList[i].class.metadata["http://unicon.net/vocabulary/v1/classEndDate"];
    	    		data.enrollments[i].title = museCourseList[i].class.title;
    	    		data.enrollments[i].userId = museCourseList[i].user.userId;
    	       	}
    	       	
    	       	var studentCourseList = [];
    	       	var instructorCourseList = [];
    	       	
    	       	for(var i in data.enrollments){	
    	       		if(data.enrollments[i].role == "student" && data.allowStudent){ 
    	       			studentCourseList.push(data.enrollments[i]);
    	       		}else if(data.enrollments[i].role == "teacher" && data.allowInstructor){
    	       			instructorCourseList.push(data.enrollments[i]);
    	       		}
    	       	}
    	       	
    	    	$scope.studentView = data.allowStudent;
    	    	$scope.instructorView = data.allowInstructor;
    	    	$scope.defaultView = data.allowDefault;
    	    	if (data.allowInstructor == false && data.allowStudent == false){
    	    		$scope.defaultView = true;
    	    	}
    	    	
    	    	$scope.studentCourseList = studentCourseList;
    	    	$scope.instructorCourseList = instructorCourseList;
    	    	
    	    	$scope.authentication = data.authentication;
    	    	$scope.sortColumn = "startdate";
    	    	$scope.reverseSort = true;
    	    	
    	    	// show scope variables *** Debug statement
    	    	console.log($scope);
    	    	
    	    	$scope.sortData = function (column) {
    	    		$scope.reverseSort = ($scope.sortColumn == column) ? !$scope.reverseSort : false;
    	    		$scope.sortColumn = column;
    	    	}
    	    	
    	    	$scope.getSortClass = function (column){
    	    		if ($scope.sortColumn == column){
    	    			return $scope.reverseSort ? 'arrow-down' : 'arrow-up'
    	    		}
    	    		
    	    		return '';
    	    	}
    	    });
    	    
    	    
    	    d3.select("#greek_logo").on("mouseover", function(d){
        		d3.select("#hidden-name").transition().duration(5000).ease(d3.easeLinear).style("opacity", 1);
        	})
        	.on("mouseout", function(d){
        		d3.select("#hidden-name").transition().duration(100).ease(d3.easeLinear).style("opacity", 0);
        	});
        	
    	}
    	
    	
    }])
})(angular);

