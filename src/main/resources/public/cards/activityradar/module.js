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
angular
.module('od.cards.activityradar', ['OpenDashboardRegistry', 'chart.js'])
 .config(function(registryProvider){
    registryProvider.register('activityradar',{
        title: 'Learning Activity Radar',
        description: 'Student Activity is displayed in radar chart format.',
        cardType: 'activityradar',
        styleClasses: 'od-card col-xs-12',
        config: [],
        requires: [],
        uses: ['MODELOUTPUT']
    });
 })
.controller('RadarCardController', function($scope, $http, ModelOutputDataService, SessionService, RosterService, _) {

  $scope.course = SessionService.getCourse();
  var options = {};
  options.contextMappingId = $scope.contextMapping.id;
  options.dashboardId = $scope.activeDashboard.id;
  options.cardId = $scope.card.id;
  options.courseId = $scope.course.id;
  var updatedJsonData = {},
      jsonData = {};
  $scope.individualStudent = null;
  $scope.data = {};
  $scope.colors = [];
  $scope.selectOptions = {
      category: [
                 'NO RISK',
                 'LOW RISK',
                 'MEDIUM RISK',
                 'HIGH RISK'
                 ],
                 selectedCategory : 'ALL',
                 students: null,
                 selectedStudent : 'ALL'
  };
  
  $scope.options = {
      pointLabelFontSize : 14,
      // Boolean - If we want to override with a hard coded scale
      scaleOverride: true,
      // ** Required if scaleOverride is true **
      // Number - The number of steps in a hard coded scale
      scaleSteps: 5,
      // Number - The value jump in the hard coded scale
      scaleStepWidth: 40,
      // Number - The scale starting value
      scaleStartValue: 0,
      showTooltips: false
  };
  

  var d=[
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
  
  //angular charts needs the labels for each dataset.  These are defined by d.label
  $scope.labels = getLabels(d);
  
  //Get the data from the json file
  init()
    .then(function(data){
      setJsonData(data);
      //normalize the data (hard coded)
      var updatedData = updateData(data);
      setUpdatedJsonData(updatedData);
      var dataPoints = getDataPoint(updatedData, d);
      var students = getStudents(updatedData);
      setData(dataPoints);
      setStudents(students);
    });
  
  $scope.filterByLevel =  function(level){
    if(level === 'ALL'){
      var dataPoints = getDataPoint(updatedJsonData, d);
      setData(dataPoints);
      var students = getStudents(updatedJsonData);
      setStudents(students);
      setIndividualStudent(null);
    }
    else{
      $scope.selectOptions.selectedStudent='ALL';
      var data = filterDataByLevel(updatedJsonData, level);
      var dataPoints = getDataPoint(data, d);
      setData(dataPoints);
      var students = getStudents(data);
      setStudents(students);
      setIndividualStudent(null);
    }
  };
  
  $scope.filterByStudent = function(student){
    if(student === 'ALL'){
      this.filterByLevel($scope.selectOptions.selectedCategory);
      setIndividualStudent(null);
    }
    else{
      var data = filterDataByStudent(updatedJsonData, student);
      var dataPoints = getDataPoint(data, d);
      setData(dataPoints);
      setIndividualStudent(student);
    }
  }

  // set only once
  function setUpdatedJsonData(data){
    updatedJsonData = data;
  }
  // set only once
  function setJsonData(data){
    jsonData = data;
  }
  
  //The sets the drop down menu & the label.dataLabels for chart.js object
  function setStudents(students){
    $scope.selectOptions.students = students;
  }
  
  //data points for the radar chart
  function setData (data){
    $scope.data = data;
  }
  
  //sets the student data for profile and indicator
  function setIndividualStudent(student){
      var data = filterDataByStudent(jsonData, student);
      $scope.individualStudent = ((data[0] !== null) && (data[0] !== undefined)) ? data[0] : null;
  }
  
  // init dataset 
  function init(){
    var promise = ModelOutputDataService.getModelOutputForCourse(options,$scope.course.id)
    .then(function (response) {
       var data = getResponseData(response);
       return data;
    });
    return promise;
  }
  
  function getLabels(d) {
    var labels = {};
    
    labels = _.map(d, function(data){
      return data.label;
    });
    return labels;
  }
  
  function getStudents(d){
    var students = {};
    students = _.map(d, function(data){
      var student = {id: null, color: null};
      student.id = data.ALTERNATIVE_ID;
      student.color = randomColorGenerator();
      return student;
    });
    createColorArray(students);
    return students;
  }
  
  function getDataPoint(studentData, d){
    var allStudentDataPoints = [];
    var self = this;
    angular.forEach(studentData, function(student){
      var individualStudentDataPoints = [];
      angular.forEach(d, function(d) {
        individualStudentDataPoints.push(student[d.metric]);
      });
      allStudentDataPoints.push(individualStudentDataPoints);
    });
    return allStudentDataPoints;
  }
  
  function getResponseData(data){
    var returnData = [];
    angular.forEach(data, function(d){
      returnData.push(d.output);
    });
    return returnData;
  }
  
  //TODO need to make dynamic (remove hard coded values)
  function updateData(data){
    var updatedData = angular.copy(data);
    angular.forEach(updatedData, function(d) {
      d.R_CONTENT_READ = ((d.R_CONTENT_READ * 100) >= 200) ? 200 : (d.R_CONTENT_READ * 100);
      d.R_FORUM_POST = ((d.R_FORUM_POST * 100) >= 200) ? 200 : (d.R_FORUM_POST * 100);
      d.R_ASN_SUB = ((d.R_ASN_SUB * 100) >= 200) ? 200 : (d.R_ASN_SUB * 100);
      d.R_SESSIONS = ((d.R_SESSIONS * 100) >= 200) ? 200 : (d.R_SESSIONS * 100);
      //scale 0..5 scale to 0..200
      d.GPA_CUMULATIVE *= 40;
    });
    return updatedData;
  }
  
  function filterDataByLevel(data, level){
    var filteredData = [];
    angular.forEach(data, function(d){
      if(d.MODEL_RISK_CONFIDENCE === level){
        filteredData.push(d);
      }
    });
    return filteredData;
  }
  
  function filterDataByStudent(data, student){
      var filteredData = [];
      angular.forEach(data, function(d){
        if(d.ALTERNATIVE_ID === student){
          filteredData.push(d);
        }
      });
      return filteredData;
  }
  
  function getOriginalDataPoint(chartLabel){
    var studentData = filterDataByStudent(jsonData, $scope.selectOptions.selectedStudent);
    var metric = _.find(d, {label: chartLabel.label}).metric;
    var value = _.map(studentData, metric);
    return (value[0] === null) ? '0' : value;
  }
 
  /**
  * In order to maintain access to the colors, must create the colors here.
  */
  //random Hex color.  
  function randomColorGenerator() { 
    return '#' + (Math.random().toString(16) + '0000000').slice(2, 8); 
  }
  
  function createColorArray(students){
    var colors = [];
    angular.forEach(students, function(student){
      colors.push(student.color);
    });
    $scope.colors = colors;
  }
});

})(angular);
