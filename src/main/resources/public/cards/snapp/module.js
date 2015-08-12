(function(angular) {
'use strict';
    
angular
.module('od.cards.snapp', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('snapp',{
        title: 'Discussion Forum Visualization',
        description: 'TBD',
        imgUrl: '',
        cardType: 'snapp',
        styleClasses: 'od-card col-xs-12',
	    config: [
	    ]
    });
 })
 .controller('SnappCardController', function($scope, $log, _, $translate, $translatePartialLoader,
	 ContextService, ForumDataService) {
	 
   $translatePartialLoader.addPart('snapp-card');
   $translate.refresh();
   
   $scope.course = ContextService.getCourse();
   
   // workflow - TODO replace with routes
   $scope.step = '1';
   
   // data
   $scope.forums = null;
   $scope.selectedForum = null;
   $scope.selectedTopic = null;
   $scope.messages = null;
   
   var providerOptions = {};
   providerOptions.contextMappingId = $scope.contextMapping.id;
   providerOptions.dashboardId = $scope.activeDashboard.id;
   providerOptions.cardId = $scope.card.id;
   providerOptions.courseId = $scope.course.id;
	
   ForumDataService
   .getForums(providerOptions)
   .then(
       function(forumData) {
         $scope.forums = forumData;
	   }
    );
   
   $scope.selectForum = function(forum) {
	 $scope.selectedForum = forum;  
	 $scope.step = '2';
   };
   
   $scope.selectTopic = function(topic) {
	 $scope.selectedTopic = topic; 
	 ForumDataService
	 .getMessages(providerOptions,topic.id)
	 .then(
	     function (messagesData) {
		  $scope.messages = messagesData;
//$log.debug($scope.messages);		  
		  var messagesByAuthor = _.groupBy($scope.messages, function (message) { return message.author;});
		  
		  var nodeData = [];
		  _.forEach(messagesByAuthor, function(value,key){
			  //$log.debug('label:' + key);
			  //$log.debug('value: ' + value);
			  var data = {};
			  data.id = key;
			  data.value = value.length;
			  data.label = key;
			  nodeData.push(data);
		  });
		  
//$log.debug(nodeData);

		  $scope.step = '3';
		   // graph
		   var nodes = new vis.DataSet(nodeData);
		   
		   var messagesById = _.groupBy($scope.messages, function (message) {
			   
		       var replyTo = '0';
		       if (message.replyTo) {
		    	   replyTo = message.replyTo;
		       }

			   return message.id + "::" + replyTo;
		   });
//$log.debug(messagesById);
           var edgeData = [];
           var messageSent = [];
           var repliesSent = [];
           var intializedThread = [];
           _.forEach(messagesById, function (value,key) {
        	  //$log.debug('key:' + key);
              //$log.debug('value: ' + value);
        	   
        	   var ids = key.split("::");
        	   //$log.debug(ids[0]);
        	   //$log.debug(ids[1]);
        	   
        	   var edge = {};
        	   edge.from = _.result(_.findWhere($scope.messages,{id:ids[0]}), 'author');
        	   edge.to = _.result(_.findWhere($scope.messages,{id:ids[1]}), 'author');
        	   edge.value = value.length;
               edge.smooth = {
                                type: "discrete",
                                forceDirection : "none",
                                roundness: 0
                             },
               edge.length = 300; // one hundred is too small
               edgeData.push(edge);

               // determine the number of total number of messages sent 
               if (isNaN(messageSent[edge.from])){
                    messageSent[edge.from] = 1;
               }else{
                     messageSent[edge.from]++;
               }

               // if edge to is undefined then will be the initial message for the thread
               if (!_.isUndefined(edge.to)){
                   if (isNaN(repliesSent[edge.from])){
                        repliesSent[edge.from] = 1;
                   }else{
                         repliesSent[edge.from]++;
                   }
               }else{
                    // only one will be undefined and will have init the thread
                   intializedThread[edge.from] = 'true';
               }

               // will only be undefined if not initialized yet or has been set to true
               if(_.isUndefined(intializedThread[edge.from])){
                    intializedThread[edge.from] = 'false';
               }
           });

          //$log.debug(edgeData);
		  var edges = new vis.DataSet(edgeData);

		  // create a network
		  $scope.graphData = {
		    nodes: nodes,
		    edges: edges
		  }; 
		  
		  var selectNodeFunction = function(properties) {
			$log.debug(properties);
			if (properties && properties.nodes && properties.nodes.length == 1) {
			  var selectedNodeName = properties.nodes[0];
			  
			  var studentNodeView = {};
			  studentNodeView.nodeId = selectedNodeName;
			  studentNodeView.numberMessages = messageSent[selectedNodeName];
			  studentNodeView.numberReplies = repliesSent[selectedNodeName];

			  $scope.studentNodeView = studentNodeView;
              // note that the visjs event library is not wrapped in angular
              // therefore after every change that is to update the DOM
              // (within the controller) we must "apply" this change to the scope.
              $scope.$apply();
			}
		  };

          var deselectNodeFunction = function() {
        	$scope.studentNodeView = null;
            $scope.$apply();
          }
		  
		  $scope.graphEvents = {
		    selectNode: selectNodeFunction,
            deselectNode: deselectNodeFunction
		  }; 
		  
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
	      }
		}
	 );
   };

});

})(angular);
