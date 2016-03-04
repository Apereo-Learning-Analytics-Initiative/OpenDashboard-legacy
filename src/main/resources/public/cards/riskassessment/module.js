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
        title: 'Risk Asessment',
        description: '',
        imgUrl: '',
        cardType: 'riskassessment',
        styleClasses: 'od-card col-xs-12',
	    config: [],
	    requires: ['ROSTER', 'MODELOUTPUT'],
	    uses: []
    });
 })
 .controller('RiskAssessmentController', function($scope, $translate, $translatePartialLoader, $log, _, SessionService, EventService, RosterService, ModelOutputDataService) {
	 
   $translatePartialLoader.addPart('risk-assessment');
   $translate
   .refresh()
     .then(function() {
    	 $scope.model_output = [
    	   {"output":{"RMN_SCORE_PARTIAL":"82.6232","RC_FINAL_GRADE":"1.3","R_ASSMT_SUB":null,"COURSE_ID":"13","PERCENTILE":0,"SAT_MATH":650,"ENROLLMENT":5,"SAT_VERBAL":600,"R_CONTENT_READ":"1.0","ACADEMIC_RISK":1,"APTITUDE_SCORE":1190,"R_ASN_SUB":"1.0","ID":1,"MODEL_RISK_CONFIDENCE":"LOW RISK","R_ASN_READ":null,"AGE":22,"ONLINE_FLAG":false,"R_FORUM_POST":null,"R_LESSONS_VIEW":null,"FAIL_PROBABILITY":"0.596","PASS_PROBABILITY":"0.404","ALTERNATIVE_ID":"8","RC_GENDER":2,"GPA_CUMULATIVE":"2.2182","RC_CLASS_CODE":"4","STANDING":"0","GPA_SEMESTER":"2.6600","R_FORUM_READ":null,"RC_ENROLLMENT_STATUS":null,"SUBJECT":"Analysis","R_SESSIONS":"1.5663","R_ASSMT_TAKE":null,"RMN_SCORE":"82.6232"},"createdDate":1446481125659},
    	   {"output":{"RMN_SCORE_PARTIAL":"125.0927","RC_FINAL_GRADE":"3.7","R_ASSMT_SUB":null,"COURSE_ID":"13","PERCENTILE":94,"SAT_MATH":660,"ENROLLMENT":5,"SAT_VERBAL":620,"R_CONTENT_READ":"2.2","ACADEMIC_RISK":2,"APTITUDE_SCORE":1280,"R_ASN_SUB":null,"ID":2,"MODEL_RISK_CONFIDENCE":"NO RISK","R_ASN_READ":null,"AGE":22,"ONLINE_FLAG":false,"R_FORUM_POST":null,"R_LESSONS_VIEW":null,"FAIL_PROBABILITY":"0.000","PASS_PROBABILITY":"1.000","ALTERNATIVE_ID":"4","RC_GENDER":2,"GPA_CUMULATIVE":"3.8729","RC_CLASS_CODE":"1","STANDING":"2","GPA_SEMESTER":"3.6800","R_FORUM_READ":null,"RC_ENROLLMENT_STATUS":null,"SUBJECT":"Analysis","R_SESSIONS":"1.3253","R_ASSMT_TAKE":null,"RMN_SCORE":"125.0927"},"createdDate":1446481125668},
    	   {"output":{"RMN_SCORE_PARTIAL":"121.4125","RC_FINAL_GRADE":"3.3","R_ASSMT_SUB":null,"COURSE_ID":"13","PERCENTILE":92,"SAT_MATH":590,"ENROLLMENT":5,"SAT_VERBAL":580,"R_CONTENT_READ":"1.0","ACADEMIC_RISK":2,"APTITUDE_SCORE":1170,"R_ASN_SUB":null,"ID":3,"MODEL_RISK_CONFIDENCE":"NO RISK","R_ASN_READ":null,"AGE":21,"ONLINE_FLAG":false,"R_FORUM_POST":null,"R_LESSONS_VIEW":null,"FAIL_PROBABILITY":"0.000","PASS_PROBABILITY":"1.000","ALTERNATIVE_ID":"6","RC_GENDER":1,"GPA_CUMULATIVE":"3.5438","RC_CLASS_CODE":"4","STANDING":"2","GPA_SEMESTER":"3.6500","R_FORUM_READ":null,"RC_ENROLLMENT_STATUS":null,"SUBJECT":"Analysis","R_SESSIONS":"1.0643","R_ASSMT_TAKE":null,"RMN_SCORE":"121.4125"},"createdDate":1446481125672},
    	   {"output":{"RMN_SCORE_PARTIAL":"93.6217","RC_FINAL_GRADE":"2.0","R_ASSMT_SUB":null,"COURSE_ID":"13","PERCENTILE":87,"SAT_MATH":600,"ENROLLMENT":5,"SAT_VERBAL":570,"R_CONTENT_READ":"0.6","ACADEMIC_RISK":2,"APTITUDE_SCORE":1170,"R_ASN_SUB":null,"ID":4,"MODEL_RISK_CONFIDENCE":"NO RISK","R_ASN_READ":null,"AGE":22,"ONLINE_FLAG":false,"R_FORUM_POST":null,"R_LESSONS_VIEW":null,"FAIL_PROBABILITY":"0.272","PASS_PROBABILITY":"0.728","ALTERNATIVE_ID":"5","RC_GENDER":2,"GPA_CUMULATIVE":"2.5000","RC_CLASS_CODE":"3","STANDING":"0","GPA_SEMESTER":"2.3400","R_FORUM_READ":null,"RC_ENROLLMENT_STATUS":null,"SUBJECT":"Analysis","R_SESSIONS":"0.8434","R_ASSMT_TAKE":null,"RMN_SCORE":"93.6217"},"createdDate":1446481125673},
    	   {"output":{"RMN_SCORE_PARTIAL":"77.2500","RC_FINAL_GRADE":"1.0","R_ASSMT_SUB":null,"COURSE_ID":"13","PERCENTILE":0,"SAT_MATH":0,"ENROLLMENT":5,"SAT_VERBAL":0,"R_CONTENT_READ":"0.3","ACADEMIC_RISK":1,"APTITUDE_SCORE":0,"R_ASN_SUB":null,"ID":5,"MODEL_RISK_CONFIDENCE":"MEDIUM RISK","R_ASN_READ":null,"AGE":36,"ONLINE_FLAG":false,"R_FORUM_POST":null,"R_LESSONS_VIEW":null,"FAIL_PROBABILITY":"0.884","PASS_PROBABILITY":"0.116","ALTERNATIVE_ID":"7","RC_GENDER":1,"GPA_CUMULATIVE":"2.3350","RC_CLASS_CODE":"4","STANDING":"0","GPA_SEMESTER":"1.7667","R_FORUM_READ":null,"RC_ENROLLMENT_STATUS":null,"SUBJECT":"Analysis","R_SESSIONS":"0.2008","R_ASSMT_TAKE":null,"RMN_SCORE":"77.2500"},"createdDate":1446481125674}];
    	 
    	 $scope.roster = [
    	   {"id":"9c97383e-f730-448d-83af-86d0532215a7","user_id":"4","role":"Learner","person":{"contact_email_primary":"jp@test.com","name_given":"James","name_family":"Pedroia","name_full":"James Pedroia"}},
    	   {"id":"1919256c-f269-4091-9888-bb8117dc4535","user_id":"9","role":"Instructor","person":{"contact_email_primary":"sean@teachers.com","name_given":"Sean","name_family":"McBride","name_full":"Sean McBride"}},
    	   {"id":"130007ff-e8ae-4aac-a7b7-3e44cddac673","user_id":"7","role":"Learner","person":{"contact_email_primary":"shunting@test.com","name_given":"Skylar","name_family":"Hunting","name_full":"Skylar Hunting"}},
    	   {"id":"780e8c61-851e-4881-89fa-0b718bee4293","user_id":"6","role":"Learner","person":{"contact_email_primary":"jwales@test.com","name_given":"Josie","name_family":"Wales","name_full":"Josie Wales"}},
    	   {"id":"623429b1-ccc7-4682-9560-e8a09e526379","user_id":"5","role":"Learner","person":{"contact_email_primary":"lw@test.com","name_given":"Luke","name_family":"Walker","name_full":"Luke Walker"}},
    	   {"id":"6458622f-d3ea-4deb-ad8e-6a663ea4c30b","user_id":"8","role":"Learner","person":{"contact_email_primary":"whunting@test.com","name_given":"Will","name_family":"Hunting","name_full":"Will Hunting"}}];
       
    	 function randomColorGenerator() {
							return '#'
									+ (Math.random().toString(16) + '0000000')
											.slice(2, 8);
						}

    	 $scope.listView = 0;
       $scope.radarView = 1;
       $scope.views = [$scope.listView, $scope.radarView];
       $scope.view = $scope.listView;
       $scope.courses = null;
       $scope.filtered = null;
       
       $scope.activeCourse = SessionService.getCourse();       

	   $scope.ALL_STUDENTS = {'id':0, 'label':'ALL STUDENTS', 'translationValue':$translate.instant('RA_LABEL_ALL_STUDENTS')};
	   $scope.HIGH_RISK = {'id':1, 'label':'HIGH RISK', 'translationValue':$translate.instant('RA_LABEL_HIGH_RISK')};
	   $scope.MEDIUM_RISK = {'id':2, 'label':'MEDIUM RISK', 'translationValue':$translate.instant('RA_LABEL_MEDIUM_RISK')};
	   $scope.LOW_RISK = {'id':3, 'label':'LOW RISK', 'translationValue':$translate.instant('RA_LABEL_LOW_RISK')};
	   $scope.NO_RISK = {'id':4, 'label':'NO RISK', 'translationValue':$translate.instant('RA_LABEL_NO_RISK')};
   
	   $scope.categories = [$scope.ALL_STUDENTS, $scope.HIGH_RISK, $scope.MEDIUM_RISK, $scope.LOW_RISK, $scope.NO_RISK];
	   $scope.groupings = [$scope.ALL_STUDENTS.label, $scope.HIGH_RISK.label, $scope.MEDIUM_RISK.label, $scope.LOW_RISK.label, $scope.NO_RISK.label];
	   $scope.grouping = $scope.groupings[0];
	   
	   $scope.findInRoster = function(id) {
	     return _.find($scope.roster,{'id':id});
	   }
	   
	   $scope.getIndicators = function(learner) {
	     var indicators = [];
	     
	     var contentRead = {};
	     contentRead['translationKey'] = 'RA_LABEL_CONTENT_READ';
	     contentRead['name'] = 'CONTENT READ';
	     contentRead['value'] = learner.output.R_CONTENT_READ;
	     indicators.push(contentRead);
	     
	     var cummulativeGPA = {};
	     cummulativeGPA['translationKey'] = 'RA_LABEL_CUMMULATIVE_GPA';
	     cummulativeGPA['name'] = 'CUMMULATIVE GPA';
	     cummulativeGPA['value'] = learner.output.GPA_CUMULATIVE;
	     indicators.push(cummulativeGPA);

	     var score = {};
	     score['translationKey'] = 'RA_LABEL_GRADEBOOK_SCORE';
	     score['name'] = 'GRADEBOOK SCORE';
	     score['value'] = learner.output.RMN_SCORE;
	     indicators.push(score);

	     var forumPost = {};
	     forumPost['translationKey'] = 'RA_LABEL_FORUM_ACTIVITY';
	     forumPost['name'] = 'FORUM ACTIVITY';
	     forumPost['value'] = learner.output.R_FORUM_POST;
	     indicators.push(forumPost);

	     var assignments = {};
	     assignments['translationKey'] = 'RA_LABEL_ASSIGNMENT_ACTIVITY';
	     assignments['name'] = 'ASSIGNMENT ACTIVITY';
	     assignments['value'] = learner.output.R_ASN_SUB;
	     indicators.push(assignments);
	     
	     var sessions = {};
	     sessions['translationKey'] = 'RA_LABEL_SESSION_ACTIVITY';
	     sessions['name'] = 'SESSION ACTIVITY';
	     sessions['value'] = learner.output.R_SESSIONS;
	     indicators.push(sessions);

	     return indicators;
	   }
	   
	   $scope.radarData = [];
	   
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

	   
	   var labelSet = [
         {
           "label" : "Content Read",     // label is the name to be displayed on the axis
           "metric" : "R_CONTENT_READ",  // metric is the name of th field that is to be plotted on the current axes
           "domain" : [0, 200]           // Domain for the scale and input values. 
         },
         {
           "label": "Cumulative GPA",
           "metric": "GPA_CUMULATIVE",
           "domain" : [0, 5]
         },
         {
           "label": "Gradebook Score",
           "metric": "RMN_SCORE",
           "domain" : [0, 200]
         },
         {
           "label": "Forums Activity",
           "metric": "R_FORUM_POST",
           "domain" : [0, 200]
         },
         {
           "label": "Assignment Activity",
           "metric": "R_ASN_SUB",
           "domain" : [0, 200]
         },
         {
           "label": "Sessions Activity",
           "metric": "R_SESSIONS",
           "domain" : [0, 200]
         }];
	   
	   $scope.radarLabels = _.map(labelSet, function(data){return data.label;});
   
 
 _.remove($scope.roster,function(member){return member.role == 'Instructor'});
 
   _.forEach($scope.roster, function(obj){
		var match = _.find($scope.model_output, function(o) {
			return o.output['ALTERNATIVE_ID'].toString() == obj.user_id;
		});
		
		if (match)
		  _.merge(obj,match);
   });
   
 
   
 
 $scope.changeGrouping = function(grouping) {
   $scope.grouping = grouping;
   if (grouping == $scope.ALL_STUDENTS.label) {
	   $scope.filterExpression = null;
   }
   else {
	   $scope.filterExpression = grouping;
   }
 }
 
 
 var riskCountFunction = function(modelOutput) {
   return modelOutput.output.MODEL_RISK_CONFIDENCE;
 }
 
 var riskPercentageFunction = function(count,total) {
   if (!count || !total) return 0;
   return Math.floor((count/total)*100);
 }
 
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

 $scope.highRiskPercentage = riskPercentageFunction(highCount, modelOutputTotal);
 $scope.mediumRiskPercentage = riskPercentageFunction(mediumCount, modelOutputTotal);
 $scope.lowRiskPercentage = riskPercentageFunction(lowCount, modelOutputTotal);
 $scope.noRiskPercentage = riskPercentageFunction(noCount, modelOutputTotal);
 
   // LEFT NAV

   $scope.switchCourse = function(courseId) {
     if ($scope.courses) {
       var course = _.find($scope.courses,{'id':courseId});
       if (course) {
    	   $scope.activeCourse = course;
       }
     }
   }

   // TABLE
   $scope.radarColors = [];
   $scope.compareGroup = [];
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
       data[1] = learner.output.GPA_CUMULATIVE * 40;
       data[2] = ((learner.output.RMN_SCORE * 100) >= 200) ? 200 : learner.output.RMN_SCORE * 100;
       data[3] = ((learner.output.R_FORUM_POST * 100) >= 200) ? 200 : learner.output.R_FORUM_POST * 100;
       data[4] = ((learner.output.R_ASN_SUB * 100) >= 200) ? 200 : learner.output.R_ASN_SUB * 100;
       data[5] = ((learner.output.R_SESSIONS * 100) >= 200) ? 200 : learner.output.R_SESSIONS * 100;
       $scope.radarColors.push(color);
       $scope.radarData.push(data);
     }
     
     $log.debug($scope.compareGroup);
   }
   
   $scope.changeToView = function(view) {
     $scope.view = view;
   }
   
   
   // DOUGHNUT
   $scope.labels = [$scope.HIGH_RISK.translationValue, $scope.MEDIUM_RISK.translationValue, $scope.LOW_RISK.translationValue, $scope.NO_RISK.translationValue];
   $scope.colors = ["#d9534f","#f0ad4e","#5bc0de","#5cb85c"]
   $scope.data = [highCount,mediumCount,lowCount,noCount];
 
   $scope.donutClick = function(points, evt) {
	 var category = _.find($scope.categories, {'translationValue':points[0].label});
	 $scope.changeGrouping(category.label);
   }


     }); // $translate.refresh.then
 
});

})(angular, Math, moment);
