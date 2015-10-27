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
(function(angular) {
'use strict';
    
angular
.module('od.cards.modelviewer', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('modelviewer',{
        title: 'Model Viewer',
        description: 'Use this card to view model output.',
        cardType: 'modelviewer',
        styleClasses: 'od-card col-xs-12',
	    config: [],
	    requires:['MODELOUTPUT']
    });
 })
 .controller('ModelViewerCardController', function($scope, $log, $translate, _,
               $translatePartialLoader, ModelOutputDataService, SessionService, RosterService) {
    $translatePartialLoader.addPart('modelviewer-card');
    $translate.refresh();
    
    $scope.members = null;
    $scope.modeloutput = null;
    $scope.course = SessionService.getCourse();
    
	var options = {};
	options.contextMappingId = $scope.contextMapping.id;
	options.dashboardId = $scope.activeDashboard.id;
	options.cardId = $scope.card.id;
	options.courseId = $scope.course.id;
	
	$scope.findNameFromId = function(userId) {
		var name = userId;
		if ($scope.members) {
			var person = _.result(_.find($scope.members, { 'user_id': userId }), 'person');
			if (person && person.name_full) {
			  name = person.name_full;
			}
		}
		return name;
	};

    $scope.fill_color = function (group) {
	  var fillColor = '#d9534f';
	  
	  if (group === 'no') {
	    fillColor = '#5cb85c';
	  }
	  else if (group === 'low') {
	    fillColor = '#5bc0de';
	  }
	  else if (group === 'medium') {
	    fillColor = '#f0ad4e';
	  }
	  
      return fillColor;
    };

    $scope.label_color = function (group) {
  	  var fillColor = 'white';
	  
		if (group === 'no') {
		  fillColor = 'white';
		}
		else if (group === 'low') {
		  fillColor = 'white';
		}
		else if (group === 'medium') {
		  fillColor = 'black';
		}
		
		return fillColor;
    };

    $scope.tooltip_format = function (datum) {
    	
      var tooltip = datum.object.name + ' : ';

      if (datum.object.score == 2) {
    	tooltip = tooltip + 'No Risk';
      }
      else if (datum.object.score == 2.5) {
    	  tooltip = tooltip + 'Low Risk';
      }
      else if (datum.object.score == 3) {
    	  tooltip = tooltip + 'Medium Risk';
      }
      else {
    	  tooltip = tooltip + 'High Risk';
      }

      return tooltip;
    };
    
    $scope.getDiameter = function () {
      return 100;
    };


    RosterService.getRoster(options)
    .then(function(members) {
    	$scope.members = members;
    	
		var user = null;
		if ($scope.isStudent) {
			user = SessionService.getCurrentUser().user_id;
		}
		
		var handleResponse = function (response) {
			if (response.isError) {
			  $scope.isError = true;
			  if (response.data && response.data.data) {
				$scope.errorMessage = response.data.data;
			  }
			  else {
				  $scope.errorMessage = "ERROR_GENERAL";
			  }
			}
			else {
				
			  $scope.modeloutput = response;
			  
			  var noRisk = [];
			  var lowRisk = [];
			  var mediumRisk = [];
			  var highRisk = [];
			  
			  angular.forEach($scope.modeloutput, function (item) {
				  var obj = {};
				  obj['name'] = $scope.findNameFromId(item.output['ALTERNATIVE_ID']);
				  
		          if (item.output['MODEL_RISK_CONFIDENCE'] == 'NO RISK') {
		        	obj['score'] = 2;
		            noRisk.push(obj);
		          }
		          else if (item.output['MODEL_RISK_CONFIDENCE'] == 'LOW RISK') {
		        	obj['score'] = 2.5;
		            lowRisk.push(obj);
		          }
		          else if (item.output['MODEL_RISK_CONFIDENCE'] == 'MEDIUM RISK') {
		        	obj['score'] = 3;
		            mediumRisk.push(obj);
		          }
		          else {
		        	obj['score'] = 4;
		            highRisk.push(obj);
		          }
		          		          
			  });
			  
	          $scope.chart_data = {
		        no: noRisk,
		        low: lowRisk,
		        medium : mediumRisk,
		        high : highRisk
	          };
			}
		};
	
	    if (user) {
	    	ModelOutputDataService.getModelOutputForUser(options,user.id)
			.then(handleResponse);
		}
		else {
			ModelOutputDataService.getModelOutputForCourse(options,$scope.course.id)
			.then(handleResponse);
		}
    });
    

});

})(angular);
