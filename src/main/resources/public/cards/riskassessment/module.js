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
(function(angular, Math, moment) {
'use strict';
    
angular
.module('od.cards.riskassessment', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('riskassessment',{
        title: 'Risk Assessment',
        description: '',
        imgUrl: '',
        cardType: 'riskassessment',
        styleClasses: 'od-card col-xs-12',
	    config: [],
	    requires: ['ROSTER', 'MODELOUTPUT'],
	    uses: []
    });
 })
 .controller('RiskAssessmentController', function($scope, $state, $stateParams, $translate, $translatePartialLoader, $log, _, 
	 SessionService, EventService, RosterService, ModelOutputDataService, CourseDataService) {
	 
   $translatePartialLoader.addPart('risk-assessment');
   $translate
   .refresh()
     .then(function() {
    
       $scope.courses = null;
       $scope.error = null;
       
       if (!$scope.contextMapping) {
         $log.debug($stateParams);
         $state.go('index.courselist');
       }

       $scope.activeCourse = SessionService.getCourse();
       $scope.activeCourse['id'] = $scope.contextMapping.context;
		
       var options = {};
       options.contextMappingId = $scope.contextMapping.id;
       options.dashboardId = $scope.activeDashboard.id;
       options.cardId = $scope.card.id;
       options.courseId = $scope.contextMapping.context;
       options.tenantId = $scope.contextMapping.tenantId;
       options.isLti = SessionService.isLTISession();
       
       if (!options.isLti) {
    	 var currentUser = SessionService.getCurrentUser();
         CourseDataService.getMemberships(currentUser.tenant_id, currentUser.user_id)
         .then(function(courseData){
             $log.debug(courseData);
             $scope.courses = courseData;
           });
       }
       
       ModelOutputDataService
       .getModelOutputForCourse(options,$scope.contextMapping.context,0,1000)
       .then(function (data) {
         $scope.model_output = data;
         
         $scope.listView = 0;
         $scope.radarView = 1;
         $scope.views = [$scope.listView, $scope.radarView];
         $scope.view = $scope.listView;
         $scope.filtered = null;
         
         var benchmarkData = [100,150,"70",100,100,100];

         $scope.radarData = [benchmarkData];
         $scope.radarColors = ["#D3D3D3"];
         $scope.compareGroup = ['benchmark'];
         
         RosterService
         .getRoster($scope.contextMapping.tenantId, $scope.contextMapping.id)
         .then(function(rosterData){
        	 
        	 if (rosterData.isError) {
           	  $scope.errorData = {};
        	  $scope.errorData['errorCode'] = rosterData.errorCode;
        	  $scope.error = rosterData.errorCode;
        	  return;
        	 }
        	 
        	 $scope.roster = rosterData;
        	 
        	_.remove($scope.roster,function(member){return member.role == 'Instructor'});
           
            _.forEach($scope.roster, function(obj){
            		var match = _.find($scope.model_output, function(o) {
            			return o.output['ALTERNATIVE_ID'].toString() == obj.user_id;
            		});
            		
            		if (match)
            		  _.merge(obj,match);
            });
            
           var riskCountFunction = function(modelOutput) {
             return modelOutput.output.MODEL_RISK_CONFIDENCE;
           }
             
           var riskPercentageFunction = function(count,total) {
             if (!count || !total) return 0;
             return Math.floor((count/total)*100);
           }
    
    	   $scope.findInRoster = function(id) {
    	     return _.find($scope.roster,{'user_id':id});
    	   } 
    
    	   $scope.ALL_STUDENTS = {'id':0, 'label':'ALL STUDENTS', 'translationValue':$translate.instant('RA_LABEL_ALL_STUDENTS')};
    	   $scope.HIGH_RISK = {'id':1, 'label':'HIGH RISK', 'translationValue':$translate.instant('RA_LABEL_HIGH_RISK')};
    	   $scope.MEDIUM_RISK = {'id':2, 'label':'MEDIUM RISK', 'translationValue':$translate.instant('RA_LABEL_MEDIUM_RISK')};
    	   $scope.LOW_RISK = {'id':3, 'label':'LOW RISK', 'translationValue':$translate.instant('RA_LABEL_LOW_RISK')};
    	   $scope.NO_RISK = {'id':4, 'label':'NO RISK', 'translationValue':$translate.instant('RA_LABEL_NO_RISK')};
    
    
            var modelOutputTotal = _.size($scope.model_output);
            var model_output_counts = _.countBy($scope.model_output, riskCountFunction);
            var highCount =  model_output_counts[$scope.HIGH_RISK.label] ? model_output_counts[$scope.HIGH_RISK.label] : 0;
            var mediumCount = model_output_counts[$scope.MEDIUM_RISK.label] ? model_output_counts[$scope.MEDIUM_RISK.label] : 0;
            var lowCount = model_output_counts[$scope.LOW_RISK.label] ? model_output_counts[$scope.LOW_RISK.label] : 0;
            var noCount = model_output_counts[$scope.NO_RISK.label] ? model_output_counts[$scope.NO_RISK.label] : 0;
            
            $scope.counts = {};
            $scope.counts[$scope.ALL_STUDENTS.label] = $scope.roster.length;
            $scope.counts[$scope.HIGH_RISK.label] = highCount;
            $scope.counts[$scope.MEDIUM_RISK.label] = mediumCount;
            $scope.counts[$scope.LOW_RISK.label] = lowCount;
            $scope.counts[$scope.NO_RISK.label] = noCount;
            
           // LIST VIEW
    
           // LEFT NAV
    
          $scope.switchCourse = function(courseId) {
             if ($scope.courses) {
               var course = _.find($scope.courses,{'id':courseId});
               if (course) {
            	   $scope.activeCourse = course;
               }
             }
           }
    
          $scope.changeGrouping = function(grouping) {
               $scope.grouping = grouping;
               if (grouping == $scope.ALL_STUDENTS.label) {
            	   $scope.filterExpression = null;
               }
               else {
            	   $scope.filterExpression = grouping;
               }
           }
            
           $scope.highRiskPercentage = riskPercentageFunction(highCount, modelOutputTotal);
           $scope.mediumRiskPercentage = riskPercentageFunction(mediumCount, modelOutputTotal);
           $scope.lowRiskPercentage = riskPercentageFunction(lowCount, modelOutputTotal);
           $scope.noRiskPercentage = riskPercentageFunction(noCount, modelOutputTotal);
           
           // DOUGHNUT
           $scope.labels = [$scope.HIGH_RISK.translationValue, $scope.MEDIUM_RISK.translationValue, $scope.LOW_RISK.translationValue, $scope.NO_RISK.translationValue];
           $scope.colors = ["#d9534f","#f0ad4e","#5bc0de","#5cb85c"]
           $scope.data = [highCount,mediumCount,lowCount,noCount];
         
           $scope.donutClick = function(points, evt) {
        	 var category = _.find($scope.categories, {'translationValue':points[0].label});
        	 $scope.changeGrouping(category.label);
           }
           
           // TABLE
    
    	   $scope.categories = [$scope.ALL_STUDENTS, $scope.HIGH_RISK, $scope.MEDIUM_RISK, $scope.LOW_RISK, $scope.NO_RISK];
    	   $scope.groupings = [$scope.ALL_STUDENTS.label, $scope.HIGH_RISK.label, $scope.MEDIUM_RISK.label, $scope.LOW_RISK.label, $scope.NO_RISK.label];
    	   $scope.grouping = $scope.groupings[0];
    
           $scope.changeToView = function(view) {
             $scope.view = view;
           }
    
           $scope.toggleCompare = function(userId) {
             var idx = $scope.compareGroup.indexOf(userId);
             
             if (idx > -1) {
               $scope.compareGroup.splice(idx, 1);
               $scope.radarData.splice(idx,1);
               $scope.radarColors.splice(idx,1);
             }
             else {
               $scope.compareGroup.push(userId);
               
               var learner = $scope.findInRoster(userId);
               var color = randomColorGenerator();
               learner['color'] = color
               var data = [];
               data[0] = ((learner.output.R_CONTENT_READ * 100) >= 200) ? 200 : learner.output.R_CONTENT_READ * 100;
               data[1] = (learner.output.GPA_CUMULATIVE  >= 4) ? 200 : learner.output.GPA_CUMULATIVE * 50;
               data[2] = (learner.output.RMN_SCORE >= 100) ? 200 : learner.output.RMN_SCORE + 100
               data[3] = ((learner.output.R_FORUM_POST * 100) >= 200) ? 200 : learner.output.R_FORUM_POST * 100;
               data[4] = ((learner.output.R_ASN_SUB * 100) >= 200) ? 200 : learner.output.R_ASN_SUB * 100;
               data[5] = ((learner.output.R_SESSIONS * 100) >= 200) ? 200 : learner.output.R_SESSIONS * 100;
               $scope.radarColors.push(learner.color);
               $scope.radarData.push(data);
             }
             
             // hack to fix radar reload issue
             var clrs = angular.copy($scope.radarColors);
             $scope.radarColors = clrs;
             var dt = angular.copy($scope.radarData);
             $scope.radarData = dt;
           }
    
           // COMPARISION VIEW

           $scope.translateRiskLevel = function(riskLevel) {
        	   if (riskLevel == 'HIGH RISK') {
        	     return $translate.instant('RA_LABEL_HIGH_RISK');
        	   }
        	   else if (riskLevel == 'MEDIUM RISK') {
        	     return $translate.instant('RA_LABEL_MEDIUM_RISK');
        	   }
        	   else if (riskLevel == 'LOW RISK') {
        	     return $translate.instant('RA_LABEL_LOW_RISK')   
        	   }
        	   else if (riskLevel == 'NO RISK') {
        	     return $translate.instant('RA_LABEL_NO_RISK');
        	   }
        	   
        	   return '';
           };
    
           function randomColorGenerator() {
             return '#'	+ (Math.random()
            		 .toString(16) + '0000000').slice(2, 8);
           }
           
           function scaledBadgeClass(scale,normalizer,value) {
             var normalized = value;
             if (normalizer) {
               normalized = normalizer * normalized;
             }
             
             var klass = '';
             if (normalized < scale[0]) {
               klass = 'od-badge-danger';
             }
             else if (normalized < scale[1]) {
               klass = 'od-badge-warning'; 
             }
             else if (normalized < scale[2]) {
               klass = 'od-badge-info';
             }
             else {
               klass = 'od-badge-success'; 
             }
             return klass;
           }
           
    	   $scope.getIndicators = function(learner) {
    		 if (learner) {
        	     var indicators = [];
        	     
                var contentRead = {};
                contentRead['translationKey'] = 'RA_LABEL_CONTENT_READ';
                contentRead['name'] = 'CONTENT READ';
                var contentReadValue = learner.output.R_CONTENT_READ;
                if (!contentReadValue) {
                	 contentReadValue = 0;
                }
                contentRead['label'] = scaledBadgeClass([50,100,150],100,contentReadValue);
                contentRead['value'] = contentReadValue;
                
                indicators.push(contentRead);
                
                var cumulativeGPA = {};
                cumulativeGPA['translationKey'] = 'RA_LABEL_CUMMULATIVE_GPA';
                cumulativeGPA['name'] = 'CUMMULATIVE GPA';
                var cumulativeGPAValue = learner.output.GPA_CUMULATIVE;
                if (!cumulativeGPAValue) {
                	 cumulativeGPAValue = 0;
                }
                cumulativeGPA['label'] = scaledBadgeClass([1,2,3],null,cumulativeGPAValue);
                cumulativeGPA['value'] = cumulativeGPAValue;
                indicators.push(cumulativeGPA);
                
                var score = {};
                score['translationKey'] = 'RA_LABEL_GRADEBOOK_SCORE';
                score['name'] = 'GRADEBOOK SCORE';
                var scoreValue = learner.output.RMN_SCORE;
                if (!scoreValue) {
                	 scoreValue = 0;
                }
                score['label'] = scaledBadgeClass([60,70,80],null,scoreValue);
                score['value'] = scoreValue;
                indicators.push(score);
                
                var forumPost = {};
                forumPost['translationKey'] = 'RA_LABEL_FORUM_ACTIVITY';
                forumPost['name'] = 'FORUM ACTIVITY';
                var forumPostValue = learner.output.R_FORUM_POST;
                if (!forumPostValue) {
                	 forumPostValue = 0;
                }
                forumPost['label'] = scaledBadgeClass([50,100,150],100,forumPostValue);
                forumPost['value'] = forumPostValue;
                
                indicators.push(forumPost);
                
                var assignments = {};
                assignments['translationKey'] = 'RA_LABEL_ASSIGNMENT_ACTIVITY';
                assignments['name'] = 'ASSIGNMENT ACTIVITY';
                var assignmentsValue = learner.output.R_ASN_SUB;
                if (!assignmentsValue) {
                	 assignmentsValue = 0;
                }
                assignments['label'] = scaledBadgeClass([50,100,150],100,assignmentsValue);
                assignments['value'] = assignmentsValue;
                indicators.push(assignments);
                
                var sessions = {};
                sessions['translationKey'] = 'RA_LABEL_SESSION_ACTIVITY';
                sessions['name'] = 'SESSION ACTIVITY';
                var sessionsValue = learner.output.R_SESSIONS;
                if (!sessionsValue) {
                	 sessionsValue = 0;
                }
                sessions['label'] = scaledBadgeClass([50,100,150],100,sessionsValue);
                sessions['value'] = sessionsValue;
                indicators.push(sessions);
                
                return indicators;
    		 }    		 
    	   }
    	   
    	   // RADAR
    	   
    	   $scope.radarOptions = {
    		pointLabelFontSize : 14,
    		// Boolean - If we want to override with a hard
    		// coded scale
    		scaleOverride : true,
    		// ** Required if scaleOverride is true **
    		// Number - The number of steps in a hard coded
    		// scale
    		scaleSteps : 5,
    		// Number - The value jump in the hard coded scale
    		scaleStepWidth : 40,
    		// Number - The scale starting value
    		scaleStartValue : 0,
    		showTooltips : false,
    		angleLineColor: "#000000",
    		angleLineWidth : 1.5
    	   };
    	   
    	   $scope.radarLabels = [$translate.instant('RA_LABEL_CONTENT_READ'),
        	   $translate.instant('RA_LABEL_CUMMULATIVE_GPA'),
        	   $translate.instant('RA_LABEL_GRADEBOOK_SCORE'),
        	   $translate.instant('RA_LABEL_FORUM_ACTIVITY'),
        	   $translate.instant('RA_LABEL_ASSIGNMENT_ACTIVITY'),
        	   $translate.instant('RA_LABEL_SESSION_ACTIVITY')];

         })          

       });

  }); // $translate.refresh.then
});

})(angular, Math, moment);
