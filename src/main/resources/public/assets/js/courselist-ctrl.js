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
(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('CourseListController', function($scope, $state, OpenDashboard_API, SessionService, TenantService, CourseDataService, ContextMappingService) {
      $scope.error = null;
      $scope.courses = null;
      var currentUser = SessionService.getCurrentUser();
      if (currentUser) {
        CourseDataService.getMemberships(currentUser.tenant_id, currentUser.user_id)
          .then(function(courseData){
        	if (courseData.isError) {
        	  $scope.errorData = {};
        	  $scope.errorData['userId'] = currentUser.user_id;
        	  $scope.errorData['errorCode'] = courseData.errorCode;
        	  $scope.error = courseData.errorCode;
        	} else {
        	  $scope.courses = courseData;
            TenantService.getTenant(currentUser.tenant_id)
              .then(function (tenant) {
                $scope.tenant = tenant;
              });
        	}
        });
      }
      
      $scope.goToDashboard = function(tenant,course) {
    	var currentUser = SessionService.getCurrentUser();

      console.log('tenant', tenant);
    	
    	ContextMappingService.getWithTenantAndCourse(currentUser.tenant_id,course.id)
    	.then(function(data){
    	  if (!data) {
    		ContextMappingService.createWithTenantAndCourse(tenant.id,course.id)
    		.then(function(data) {
    		  var options = {};
    		  options['id'] = data.context;
    		  options['title'] = course.title;
    		  OpenDashboard_API.setCourse(options);
    		  //$state.go('index',{"tenantId":tenant.id,"courseId":course.id});
    		  $scope.contextMapping = data;
    	      $scope.activeDashboard = $scope.contextMapping.dashboards[0];
    	      $state.go('index.dashboard', {cmid:$scope.contextMapping.id,dbid:$scope.activeDashboard.id});

    		});
    	  }
    	  else {
    	    var options = {};
    	    options['id'] = data.context;
    	    options['title'] = course.title;
    	    OpenDashboard_API.setCourse(options);
    		//$state.go('index',{"tenantId":tenant.id,"courseId":course.id});
      		$scope.contextMapping = data;
    	    $scope.activeDashboard = $scope.contextMapping.dashboards[0];
    	    $state.go('index.dashboard', {cmid:$scope.contextMapping.id,dbid:$scope.activeDashboard.id});
    	  }
    	});
    	  
        
      }
      
    });
})(angular);
