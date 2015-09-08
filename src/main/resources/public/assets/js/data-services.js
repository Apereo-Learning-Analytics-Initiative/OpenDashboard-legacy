(function(angular, JSON, Math) {
  'use strict';
	
  	angular
	.module('OpenDashboard')
	.service('AssignmentService', function($log, $http, OpenDashboard_API) {

		return {
			getAssignments: function(options) {
				
					var url = '/api/assignments';
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    			var assignments = [];
			    			angular.forEach(response.data, function(value,key) {
			                    assignments.push(value);
			                });
			    			
				    		return assignments;		    			
			    		}
			    		$log.debug('No assignments found for course');
			    		return null;
			    	}, function (error) {
			    		$log.error(error);
			    		return null;
			    	});
					return promise;
			}
		}
	});
	  angular
	  .module('OpenDashboard')
	  	.service('CourseDataService', function($log, $http, OpenDashboard_API) {

			return {
				getContexts: function(options) {
					
					$log.debug(options);
				
					var url = '/api/context';
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    			var contexts = [];
			    			angular.forEach(response.data, function(value,key) {
			    				contexts.push(value);
			                });
			    			
				    		return contexts;		    			
			    		}
			    		$log.debug('No contexts found for user');
			    		return null;
			    	}, function (error) {
			    		$log.error(error);
			    		return null;
			    	});
					return promise;
				},
				getContext: function(options, contextId) {				
					$log.debug(options);
					
					var url = '/api/context/'+contextId;
					var promise = $http({
						method  : 'POST',
						url		: url,
						data    : JSON.stringify(options),
						headers : { 'Content-Type': 'application/json'}
					})
					.then(function (response) {
						if (response && response.data) {
							
				    		return response.data;		    			
						}
						$log.debug('No contexts found for user');
						return null;
					}, function (error) {
						$log.error(error);
						return null;
					});
					return promise;
				 }		
			}
		});
		angular.
		module('OpenDashboard')
		.service('EventService',function($log, $http, _) {
			
			return {
				getEventsForCourse : function (options, courseId, page, size) {
					$log.debug(options);
					$log.debug(courseId);
					$log.debug(page);
					$log.debug(size);
					var p = page || 0;
		            var s = size || 10;
		
				
					var url = '/api/event/course/'+courseId+'?page='+p+'&size='+s;
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		$log.debug(response);
			    		if (response && response.data) {
			    		  return response.data.content;	    	
			    		}
			    		return null;
			    	}, function (error) {
			    		$log.error(error);
			    		return null;
			    	});
					return promise;
				},
				getEventsForUser : function (options, userId, page, size) {
					$log.debug(options);
					$log.debug(userId);
					$log.debug(page);
					$log.debug(size);
					var p = page || 0;
		            var s = size || 10;
		
				
					var url = '/api/event/user/'+userId+'?page='+p+'&size='+s;
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    		  return response.data.content;		    	
			    		}
			    		return null;
			    	}, function (error) {
			    		$log.error(error);
			    		return null;
			    	});
					return promise;
				},
				getEventsForCourseAndUser : function (options, courseId, userId, page, size) {
					$log.debug(options);
					$log.debug(courseId);
					$log.debug(userId);
					$log.debug(page);
					$log.debug(size);
					var p = page || 0;
		            var s = size || 10;
		
				
					var url = '/api/event/course/'+courseId+'/user/'+userId+'?page='+p+'&size='+s;
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    		  return response.data.content;	    	
			    		}
			    		return null;
			    	}, function (error) {
			    		$log.error(error);
			    		return null;
			    	});
					return promise;
				},
				groupByAndMap: function(events,groupByFunction,mapFunction) {
				
					if (!groupByFunction) {
						groupByFunction = function (event) {
							$log.debug(event);
							//by default groupBy stored date
							var timestamp = event.timestamp;
				    		return timestamp.slice(0, timestamp.indexOf("T"));
						};
					}
					
					if (!mapFunction) {
						mapFunction = function(value,key){
							// by default map to the number of events for the value
		    				var numberOfEvents = 0;
		    				if (value && value != null) {
		    					numberOfEvents = value.length;
		    				}
		    				
		    				return {
		    					x:key,
		    					y: [numberOfEvents]
		    				};
		    			};
					}
					
					return _.chain(events)
							.groupBy(groupByFunction)
							.map(mapFunction)
							.value();
				}
			}
		});
		  angular
		  .module('OpenDashboard')
		  	.service('ForumDataService', function($log, $http, OpenDashboard_API) {
				return {
					getForums: function(options) {
						
						$log.debug(options);
					
						var url = '/api/forums';
				    	var promise = $http({
				    		method  : 'POST',
				    		url		: url,
				    		data    : JSON.stringify(options),
				    		headers : { 'Content-Type': 'application/json'}
				    	})
				    	.then(function (response) {
				    		if (response && response.data) {
					    		return response.data;		    			
				    		}
				    		$log.debug('No forums found for course');
				    		return null;
				    	}, function (error) {
				    		$log.error(error);
				    		return null;
				    	});
						return promise;
					},
					getMessages: function(options, topicId) {
						
						$log.debug(options);
						$log.debug(topicId);
						
						var url = '/api/forums/'+topicId+'/messages';
						var promise = $http({
							method  : 'POST',
							url		: url,
							data    : JSON.stringify(options),
							headers : { 'Content-Type': 'application/json'}
						})
						.then(function (response) {
							if (response && response.data) {
								return response.data;		    			
							}
							$log.debug('No messages found for course');
							return null;
						}, function (error) {
							$log.error(error);
							return null;
						});
						return promise;
					}
				}
			});
		  angular
		  .module('OpenDashboard')
		  	.service('ModelOutputDataService', function($log, $http, OpenDashboard_API) {

				return {
					getModelOutputForCourse: function(options,courseId,page,size) {
						
						$log.debug(options);
						$log.debug(courseId);
						$log.debug(page);
						$log.debug(size);
						var p = page || 0;
		                var s = size || 10;

					
						var url = '/api/modeloutput/course/'+courseId+'?page='+p+'&size='+s;
				    	var promise = $http({
				    		method  : 'POST',
				    		url		: url,
				    		data    : JSON.stringify(options),
				    		headers : { 'Content-Type': 'application/json'}
				    	})
				    	.then(function (response) {
				    		if (response && response.data) {
				    			console.log('****');
				    			console.log(response);
				    			return response.data.content;		    	
				    		}
				    		$log.debug('No model output');
				    		return null;
				    	}, function (error) {
				    		$log.error(error);
				    		return null;
				    	});
						return promise;
					},
					getModelOutputForUser: function(options,userId,page,size) {
						
						$log.debug(options);
						$log.debug(userId);
						$log.debug(page);
						$log.debug(size);
						var p = page || 0;
						var s = size || 10;
						
						
						var url = '/api/modeloutput/user/'+userId+'?page='+p+'&size='+s;
						var promise = $http({
							method  : 'POST',
							url		: url,
							data    : JSON.stringify(options),
							headers : { 'Content-Type': 'application/json'}
						})
						.then(function (response) {
							if (response && response.data) {
								return response.data.content;		    	
							}
							$log.debug('No model output');
							return null;
						}, function (error) {
							$log.error(error);
							return null;
						});
						return promise;
					}
				}
			});
			angular
			.module('OpenDashboard')
			.service('OutcomesService', function($log, $http, OpenDashboard_API) {
				return {
					getOutcomesForCourse: function(options) {
					
						$log.debug(options);
						var url = '/api/outcomes';
				    	var promise = $http({
				    		method  : 'POST',
				    		url		: url,
					    	data    : JSON.stringify(options),
				    		headers : { 'Content-Type': 'application/json'}
				    	})
				    	.then(function (response) {
				    		if (response && response.data) {
					    		return response.data;		    			
				    		}
				    		
				    		$log.debug('No outcomes found for getOutcomesForCourse');
				    		return null;
				    	}, function (error) {
				    		$log.error(error);
				    		return null;
				    	});
						return promise;
					}
				}
			});
			angular
			.module('OpenDashboard')
			.service('RosterService', function($log, $http, OpenDashboard_API) {
				return {
					getRoster: function(options) {
						
						$log.debug(options);
						var url = '/api/roster';
				    	var promise = $http({
				    		method  : 'POST',
				    		url		: url,
				    		data    : JSON.stringify(options),
				    		headers : { 'Content-Type': 'application/json'}
				    	})
				    	.then(function (response) {
				    		if (response && response.data) {
				    			var members = [];
				    			angular.forEach(response.data, function(value,key) {
				                    var member = OpenDashboard_API.createMemberInstance();
				                    member.fromService(value);
				                    members.push(member);
				                });
				    			
					    		return members;		    			
				    		}
				    		$log.debug('No members found for getRoster');
				    		return null;
				    	}, function (error) {
				    		$log.error(error);
				    		return null;
				    	});
						return promise;
					}
				}
			});

	
})(angular, JSON, Math);
