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
   $scope.chartData =[[]];
   
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
	  	
	  	var userGuid = $scope.data[learner][0].user_id;
	  	var userName = $scope.data[learner][0].person.name_full;
	  	var color = $scope.data[learner].color;
	  	$scope.showLineGraphGroup(userGuid, userName, color);
   };
   
   
   $scope.chooseGraph = function() {
	   if ($scope.currentGraph == 'messageView') {
		   $scope.messageViewGraph();
	   }
	   if ($scope.currentGraph == 'studentView') {
		   $scope.studentViewGraph();
	   }
   }
   //test
   //show Messages View
   $scope.messageViewGraph = function() {
	      
	         //get the graph data from the uber object
	         var nodeData = [];
	         _.forEach($scope.data, function(value,key){
	        	 _.forEach(value.forumData, function(value2,key2) {
	        		 
		    		  var data = {};
		    		  data.id = value2.id;
		    		  data.value = value.forumData.length;
		    		  data.label = value[0].person.name_full;
		    		  data.color = value.color;
		    		  data.author = value2.author;
		    		  //data.shadow = true;
		    		  data.title=value2.title + " " + value.color;		  
		    		  nodeData.push(data);
		    	  });	        	 
	         });
	         
	        
	         
	         
	         var edgeData = [];
	       //create edges 
	         _.forEach($scope.data, function (value,key) {        	 
	        	 var edge = {};
	        	 //var i=1;
	        	 _.forEach(value.forumData, function (internalValue,internalKey) {  
	        		 var edge = {};
	        		 
	            	 if (!(typeof internalValue.replyTo === "undefined")) {
	            		 edge.from = internalValue.id;
	            		 edge.to = internalValue.replyTo;
	            		 edge.numLabels = 1;
	                     //edge.value = i++;
	                     edge.smooth = {
	                         type: "discrete",                   
	                     };
	                     //edge.shadow=true;
	                     edge.length = 150; // one hundred is too small
	                     var t = _.find(edgeData, edge);                     
	                     if (t == null) {
	                    	 edgeData.push(edge);
	                     }
	                     else {
	                    	 edge.numLabels++;
	                         edge.label=edge.numLabels;                    	 
	                     }
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
                 //edge.shadow=true;
                 edge.length = 150; // one hundred is too small
                    var t = _.find(edgeData, edge);                     
                    if (t == null) {
                   	 edgeData.push(edge);
                    }
                    else {
                   	 edge.numLabels++;
                   	 edge.label=edge.numLabels;                    	 
                    }                    
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
   
   
   $scope.showLineGraphGroup = function(userId, userName, color) {
	   
	   $scope.activityGraphGroups = new vis.DataSet();   
	   $scope.activityGraphDataset = new vis.DataSet();
	   $scope.activityGraphOptions = {
		    drawPoints: true,
		    legend: true,
		    
		};
	   
	  // $scope.activityGraphOptions.dataAxis.left.range.max = 20;
	   	   
	   var container = document.getElementById('studentActivityChart');		   
	   

	   //Add Groups, Average and Student Group
	   $scope.activityGraphGroups.add({
		   id: 'average',
		   content: 'course activity average',
	   });

	   var fullUserId = 'mailto:' + userId + '@tincanapi.dev.sakaiproject.org';

	   if($scope.activityGraphGroups.get(fullUserId) == null){
		   $scope.activityGraphGroups.add({
			   id: fullUserId,
			   content: userName,
			   //color: color
		   });
	   }
		   
	   // add average items
	   var numStudents = Object.keys($scope.data).length;	         	        
	   Object.keys($scope.activityData.totalByWeekNumber).forEach(function (key) {	        	 
		   $scope.activityGraphDataset.add( [	       	 	            
		                                     {x: "week of: " + key, y: $scope.activityData.totalByWeekNumber[key]/numStudents, group: 'average'}
		                                     ]);   
	   }); 
		   
		   
		   
	   //experimental way
	   Object.keys($scope.activityData.totalByWeekNumber).forEach(function (key) {
		   //if the user isn't here all together, add 0
		   if($scope.activityData.studentActivityStats[fullUserId]==null) {
			   $scope.activityGraphDataset.add( [	       	 	            
			                                     {x: "week of: " + key, y: 0, group: fullUserId}
			                                     ]);   
		   } else if ($scope.activityData.studentActivityStats[fullUserId].totalByWeekNumber[key] == null) {
			   $scope.activityGraphDataset.add( [	       	 	            
			                                     {x: "week of: " + key, y: 0, group: fullUserId}
			                                     ]); 
		   } else {			   
			   var value=0;
			   value = $scope.activityData.studentActivityStats[fullUserId].totalByWeekNumber[key];				   
			   $scope.activityGraphDataset.add( [	       	 	            
			                                     {x: "week of: " + key, y: value, group: fullUserId}
			                                     ]);   
		   }
	   });

		   
		   
	       
		var newOptions = {		    
		    dataAxis: {
		    	left: {
		    		range: {
		    			max:$scope.activityData.max
		    		}
		    	}
		  	}
		};
		
		
		if($scope.graph2d != null) {
			$scope.graph2d.destroy();	
		}
		
		$scope.graph2d = new vis.Graph2d(container, $scope.activityGraphDataset, $scope.activityGraphGroups, $scope.activityGraphOptions);
		$scope.graph2d.setOptions(newOptions);

   }
   

   $translatePartialLoader.addPart('sakai');
   $translate
   .refresh()
     .then(function() {
       $q.all([
 		RosterService
		.getRosterBasicLIS($scope.contextMapping.tenantId, $scope.contextMapping.id, SessionService.getInbound_LTI_Launch().ext.ext_ims_lis_memberships_id),
		ForumDataService
		.getAllMessages($scope.contextMapping.tenantId, $scope.contextMapping.id),
		EventService.getCourseEventsStatsForCourse($scope.options,$scope.contextMapping.context,0,10000)		
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
         // end activity
       });

  }); // $translate.refresh.then
});

})(angular, Math, moment);


function randomColorGenerator() {	
	var new_light_color = 'rgb(' + (Math.floor((255-228)*Math.random()) + 229) + ',' + 
    (Math.floor((255-228)*Math.random()) + 229) + ',' + 
    (Math.floor((255-228)*Math.random()) + 229) + ')';
	return new_light_color;
}
