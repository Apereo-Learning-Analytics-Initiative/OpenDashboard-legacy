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
         

         //get the graph data from the uber object
         var nodeData = [];
         _.forEach($scope.data, function(value,key){
       	 
    		  var data = {};
    		  data.id = value[0].person.name_full;
    		  data.value = value.forumData.length;
    		  data.label = value[0].person.name_full;
    		  data.color = value.color;
    		  
    		  if (data.value >= 1) {
    			  nodeData.push(data);
    		  }
    	  });
        
         var nodes = new vis.DataSet(nodeData);
         var messagesById = _.groupBy($scope.messages, function (message) {		   
           var replyTo = '0';
           if (message.replyTo) {
             replyTo = message.replyTo;
           }

           return message.id + "::" + replyTo;
         });
         
         var edgeData = [];
         var messageSent = [];
         var repliesSent = [];
         var intializedThread = [];
         _.forEach(messagesById, function (value,key) {
           var ids = key.split("::");	  		        	   
           var edge = {};
           edge.from = _.result(_.findWhere($scope.messages,{id:ids[0]}), 'author');
           edge.to = _.result(_.findWhere($scope.messages,{id:ids[1]}), 'author');
           edge.value = value.length;
           edge.smooth = {
               type: "discrete",
               forceDirection : "none",
               roundness: 0
           };
           edge.length = 300; // one hundred is too small
           edgeData.push(edge);

           // determine the number of total number of messages sent 
           if (isNaN(messageSent[edge.from])) {
             messageSent[edge.from] = 1;
           }
           else{
             messageSent[edge.from]++;
           }

           // if edge to is undefined then will be the initial message for the thread
           if (!_.isUndefined(edge.to)){
             if (isNaN(repliesSent[edge.from])) {
               repliesSent[edge.from] = 1;
             }
             else {
               repliesSent[edge.from]++;
             }
           }
           else {
             // only one will be undefined and will have init the thread
             intializedThread[edge.from] = 'true';
           }

           // will only be undefined if not initialized yet or has been set to true
           if(_.isUndefined(intializedThread[edge.from])){
                intializedThread[edge.from] = 'false';
           }
         });

	  	var edges = new vis.DataSet(edgeData);

	  	// create a network
	  	$scope.graphOptions = {
	  	  nodes: {
	  		
		    shape: 'dot',	  		
		    scaling: {
              customScalingFunction: function (min,max,total,value) {
                return value/total;
              },
              min:10,
              max:100
            }		    
	      },
	      edges: {
	        scaling: {
              min:0,
              max:10,
              customScalingFunction: function (min,max,total,value) {
	            var width = .5;
                return value * width;
              }
	        }
	      }
        };

	  	$scope.graphData = {
	  	  nodes: nodes,
	  	  edges: edges
	  	}; 
         // end forum
	  	
         // activity

         $scope.activityData = responses[2];
         $log.debug($scope.activityData);
         
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
    return '#'	+ (Math.random()
   		 .toString(16) + '0000000').slice(2, 8);
}
