(function(angular) {
'use strict';
    
angular
.module('od.cards.snapp', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('snapp',{
        title: 'SNAPP Card',
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
//console.log($scope.messages);		  
		  var messagesByAuthor = _.groupBy($scope.messages, function (message) { return message.author;});
		  
		  var nodeData = [];
		  _.forEach(messagesByAuthor, function(value,key){
//			  console.log(key);
//			  console.log(value);
			  var data = {};
			  data.id = key;
			  data.value = value.length;
			  data.label = key;
			  nodeData.push(data);
		  });
		  
//console.log(nodeData);

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
//console.log(messagesById);
           var edgeData = [];
           _.forEach(messagesById, function (value,key) {
        	   //console.log(value);
        	   //console.log(key);
        	   
        	   var ids = key.split("::");
        	   //console.log(ids[0]);
        	   //console.log(ids[1]);
        	   
        	   var edge = {};
        	   edge.from = _.result(_.findWhere($scope.messages,{id:ids[0]}), 'author');
        	   edge.to = _.result(_.findWhere($scope.messages,{id:ids[1]}), 'author');
        	   edge.value = value.length;
        	   edge.title = value.length + ' messages';
        	   edgeData.push(edge);
           });
           
          //console.log(edgeData);
		  var edges = new vis.DataSet(edgeData);

		  // create a network
		  $scope.graphData = {
		    nodes: nodes,
		    edges: edges
		  }; 
		  
		  $scope.graphEvents = {
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
