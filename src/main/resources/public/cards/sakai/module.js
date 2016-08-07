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
.module('od.cards.sakai', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('sakai',{
        title: 'Sakai Dashboard',
        description: '',
        imgUrl: '',
        cardType: 'sakai',
        styleClasses: 'od-card col-xs-12',
	    config: [],
	    requires: [],
	    uses: []
    });
 })
 .controller('SakaiDashboardController', function($scope, $state, $stateParams, $translate, $translatePartialLoader, $log, $q, _, 
     OpenDashboard_API, ContextMappingService, SessionService, EventService, RosterService, ForumDataService, CourseDataService) {
	 
   $scope.graphData = {};	 
   $scope.rosterData = null;
   $scope.forumMessageData = null;
   $scope.activityData = null;
   $scope.series = ['Events over time'];
   $scope.labels = [];
   $scope.data =[[]];
   
   $scope.options = {};
   $scope.options.contextMappingId = $scope.contextMapping.id;
   $scope.options.dashboardId = $scope.activeDashboard.id;
   $scope.options.cardId = $scope.card.id;
   $scope.options.courseId = $scope.contextMapping.context;
   $scope.options.tenantId = $scope.contextMapping.tenantId;
   $scope.options.isLti = SessionService.isLTISession();

   $scope.learnerFilter = function(member) {
	 return	member.roles && member.roles.indexOf("Learner") >= 0;
   };
   
   
   //highlight the student that has been selected
   $scope.focusOnStudent = function(learner) {	   
	  	$scope.network.focus(learner, {locked: false, 
			animation: true });	  	
	  	$scope.network.selectNodes([learner]);	  	
   };
   
   
   $scope.chooseGraph = function() {
	   if ($scope.currentGraph == 'messageView') {
		   $scope.messageViewGraph();
	   }
	   if ($scope.currentGraph == 'studentView') {
		   $scope.studentViewGraph();
	   }
   }
   
   //show Messages View
   $scope.messageViewGraph = function() {
	   $log.debug("$scope.data");
	   $log.debug($scope.data);
	   
	         //get the graph data from the uber object
	         var nodeData = [];
	         _.forEach($scope.data, function(value,key){
	        	 _.forEach(value.forumData, function(value2,key2) {
	        		 
	        		 $log.debug(value2);
		    		  var data = {};
		    		  data.id = value2.id;
		    		  data.value = value.forumData.length;
		    		  data.label = value[0].person.name_full;
		    		  data.color = value.color;
		    		  data.author = value2.author;
		    		  //data.shadow = true;
		    		  data.title=value2.title;		  
		    		  nodeData.push(data);
		    	  });	        	 
	         });
	         
	         $log.debug(nodeData);
	         
	         
	         var edgeData = [];
	       //create edges 
	         _.forEach($scope.data, function (value,key) {        	 
	        	 var edge = {};
	        	 //var i=1;
	        	 _.forEach(value.forumData, function (internalValue,internalKey) {  
	        		 var edge = {};
	        		 $log.debug("blah");
	        		 $log.debug(internalValue);
	            	 if (!(typeof internalValue.replyTo === "undefined")) {
	            		 edge.from = internalValue.id;
	            		 edge.to = internalValue.replyTo;
	            		 edge.numLabels = 1;
	                     //edge.value = i++;
	                     edge.smooth = {
	                         type: "discrete",
	                         //forceDirection : "none",
	                         //roundness: 0                         
	                     };
	                     edge.shadow=true;
	                     edge.length = 150; // one hundred is too small
	                     var t = _.find(edgeData, edge);                     
	                     if (t == null) {
	                    	 edgeData.push(edge);
	                     }
	                     else {
	                    	 edge.numLabels++;
	                         edge.label=edge.numLabels;                    	 
	                     }
	                     //$log.debug(edgeData);
	            	 }            	 
	        	 });
	        	 
	         });  
	         
	         var graphOptions = {
	         edges: {
		    	  
		    	  arrows: {
		    	      to:     {enabled: true, scaleFactor:1},
		    	      middle: {enabled: false, scaleFactor:1},
		    	      from:   {enabled: false, scaleFactor:1}
		    	    }
	         }};
	         
	         var nodes = new vis.DataSet(nodeData);
	         var edges = new vis.DataSet(edgeData);
	         $scope.graphData.nodes = nodes;
	         $scope.graphData.edges = edges;
	         
	        
	    	 var container = document.getElementById('mynetwork');

	    	 // initialize your network!
	    	 var network = new vis.Network(container, $scope.graphData, graphOptions);	
	    	 

	    	  // create a network
	    	  var container = document.getElementById('mynetwork');
	    	  //var data = {
	    	  //  nodes: nodes,
	    	  //  edges: edges
	    	  //};
	    	  var options = {};
	    	  var network = new vis.Network(container, data, options);
	    	  $scope.network = network;
	    	 
	    	 
	    	 
   };
   
   	$scope.studentViewGraph = function() {
	   
        //get the graph data from the uber object
        var nodeData = [];
        _.forEach($scope.data, function(value,key){
      	 
   		  var data = {};
   		  data.id = value[0].person_sourcedid;
   		  data.value = value.forumData.length;
   		  data.label = value[0].person.name_full;
   		  data.color = value.color;
   		  data.shadow = true;
   		  data.title = value.forumData.length + " forum contributions";

  	      nodeData.push(data);
   	  });
        
        var edgeData = [];
        var messageSent = [];
        var repliesSent = [];
        var intializedThread = [];
        
        //create edges 
        _.forEach($scope.data, function (value,key) {        	 
       	 var edge = {};
       	 var i=1;
       	 _.forEach(value.forumData, function (internalValue,internalKey) {        		 
           	 if (!(typeof internalValue.replyTo === "undefined")) {
           		 edge.from = key;
           		 edge.to = $scope.messageIdMap[internalValue.replyTo];
        		 edge.numLabels = 1;
                 //edge.value = i++;
                 edge.smooth = {
                     type: "discrete",
                     //forceDirection : "none",
                     roundness: .6                         
                 };
                 edge.shadow=true;
                 edge.length = 150; // one hundred is too small
                    var t = _.find(edgeData, edge);                     
                    if (t == null) {
                   	 edgeData.push(edge);
                    }
                    else {
                   	 edge.numLabels++;
                   	 edge.label=edge.numLabels;                    	 
                    }
                    $log.debug(edgeData);
           	 }            	 
       	 });
       	 
        });  
      
       var nodes = new vis.DataSet(nodeData);
       var edges = new vis.DataSet(edgeData);
       $scope.graphData.nodes = nodes;
       $scope.graphData.edges = edges;
       
       var graphOptions = {
  	         edges: {
  		    	  
  		    	  arrows: {
  		    	      to:     {enabled: true, scaleFactor:1},
  		    	      middle: {enabled: false, scaleFactor:1},
  		    	      from:   {enabled: false, scaleFactor:1}
  		    	    }
  	         }};

  	    var container = document.getElementById('mynetwork');

  	    // initialize your network!
  	    var network = new vis.Network(container, $scope.graphData,graphOptions);// $scope.graphOptions);  
  	  $scope.network = network;
   };


   $translatePartialLoader.addPart('sakai');
   $translate
   .refresh()
     .then(function() {
       $q.all([
 		RosterService
		.getRosterBasicLIS($scope.contextMapping.tenantId, $scope.contextMapping.id, SessionService.getInbound_LTI_Launch().ext.ext_ims_lis_memberships_id),
		ForumDataService
		.getAllMessages($scope.contextMapping.tenantId, $scope.contextMapping.id),
		EventService
	     .getEventsForCourse($scope.options,$scope.contextMapping.context,0,10000)
       ]).then(function(responses){
    	 // roster
         $scope.data = _.groupBy(responses[0], function (member) { return member.person_sourcedid;});
                 
         _.forEach($scope.data, function(value,key){
        	 var t = '(' + key + ')';
        	 var userMessages = _.filter(responses[1], function (message) { 
        		 if (message.author.includes(t)) {
        			 return true;
        		 } else {
        			 return false;
        		 }
        	 });
        	 
        	 value.forumData = userMessages;
        	 
        	 //add color
        	 value.color = randomColorGenerator();
         });

            
         //build messageIdMap
         var messageIdMap = {};
         _.forEach($scope.data, function(value,key){
        	        	 
        	 var messageIds = [];
        	 _.forEach(value.forumData, function(t,y){
        		 messageIdMap[t.id] = key;
        	 });
          });
         $scope.messageIdMap = messageIdMap;
         //end build messageIdMap

         //Create the Student View of the Forum Data
         $scope.studentViewGraph();

	  	
         // activity
         $scope.activityData = responses[2];
         
         var occurrenceDay = function(occurrence){
             $log.debug(occurrence);

           return moment(occurrence.timestamp).startOf('day').format();
         };
    
         var groupToDay = function(group, day){
           return {
             dt: {day:moment(day).format('MMM D'),d:moment(day)},
             count: group.length
           }
         };
    
         var result = _.chain($scope.activityData)
          .groupBy(occurrenceDay)
          .map(groupToDay)
          .sortBy('dt.d')
          .value();
         $log.debug(result);
         
         _.forEach(result,function(o){
           $scope.labels.push(o.dt.day);
           $scope.data[0].push(o.count);
         });
         // end activity
       });

  }); // $translate.refresh.then
});

})(angular, Math, moment);


function randomColorGenerator() {
	
	var new_light_color = 'rgb(' + (Math.floor((256-229)*Math.random()) + 230) + ',' + 
    (Math.floor((256-229)*Math.random()) + 230) + ',' + 
    (Math.floor((256-229)*Math.random()) + 230) + ')';
	return new_light_color;
	
    //return '#'	+ (Math.random()
   //		 .toString(16) + '0000000').slice(2, 8);
}
