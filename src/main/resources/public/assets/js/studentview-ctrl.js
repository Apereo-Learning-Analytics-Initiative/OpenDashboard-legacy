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
(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('StudentViewController',
        function ($scope, $http, $timeout, $state, SessionService, EnrollmentDataService, EventService) {
            var currentUser;
            var activityOverTimeChart;
            var activityDayOfWeekChart;

            $scope.error = null;
            $scope.actions = [];
            $scope.state = $state;

            $scope.activityByVerbChartData = null;
            $scope.activityOverTimeData = null;

            $scope.studentFilters.displayMode = 'chart';
            $scope.studentFilters.detailView = '90days';
            $scope.allFilters = false;
            
            var clrs = [
                          'rgba(6, 11, 178, 0.5)',
                          'rgba(84, 255, 0, 0.5)',
                          'rgba(255, 132, 7, 0.5)',
                          'rgba(29, 178, 37, 0.5)',
                          'rgba(13, 191, 255, 0.5)',
                          'rgba(232, 6, 222, 0.5)',
                          'rgba(232, 163, 12, 0.5)',
                          'rgba(50, 12, 132, 0.5)',
                          'rgba(13, 175, 255, 0.5)',
                          'rgba(248, 255, 0, 0.5)',
                          'rgba(232, 143, 12, 0.5)'
            ];
            
            $scope.studentFilters.verblist = [
//                {
//                    label: 'Completed',
//                    color: 'rgba(6, 11, 178, 0.5)',
//                    filter: false
//                },{
//                    label: 'Submitted',
//                    color: 'rgba(84, 255, 0, 0.5)',
//                    filter: true
//                },{
//                    label: 'NavigatedTo',
//                    color: 'rgba(255, 132, 7, 0.5)',
//                    filter: false
//                },{
//                    label: 'Viewed',
//                    color: 'rgba(29, 178, 37, 0.5)',
//                    filter: true
//                },
//                 {
//                     label: 'Posted',
//                     color: 'rgba(13, 191, 255, 0.5)',
//                     filter: true
//                 },
//                {
//                     label: 'Modified',
//                     color: 'rgba(232, 6, 222, 0.5)',
//                     filter: true
//                },
//                {
//                    label: 'Searched',
//                    color: 'rgba(232, 163, 12, 0.5)',
//                    filter: false
//                },{
//                    label: 'Shared',
//                    color: 'rgba(50, 12, 132, 0.5)',
//                    filter: false
//                },{
//                    label: 'Bookmarked',
//                    color: 'rgba(13, 175, 255, 0.5)',
//                    filter: false
//                },{
//                    label: 'Commented',
//                    color: 'rgba(248, 255, 0, 0.5)',
//                    filter: false
//                },{
//                    label: 'Recommended',
//                    color: 'rgba(232, 143, 12, 0.5)',
//                    filter: false
//                }
                ];

            currentUser = SessionService.getCurrentUser();

            /**
             * TODO: Move data loading to payload resolver
             */
            if (currentUser) {
                EnrollmentDataService.getEnrollmentsForUser(currentUser.tenant_id, currentUser.user_id)
                .then(function(enrollments){
                    if (enrollments.isError) {
                        $scope.errorData = {};
                        $scope.errorData['userId'] = currentUser.user_id;
                        $scope.errorData['errorCode'] = enrollments.errorCode;
                        $scope.error = enrollments.errorCode;
                    } else {
                        EventService.getEventForClassAndUser(currentUser.tenant_id, $state.params.groupId, $state.params.studentId, 0, 1000)
                        .then(function (actions) {
                            $scope.actions = actions.content;
                            var allVerbs = [];
                            _.each($scope.actions, function (action) {
                            	$scope.uniqueVerbs 
                                var verb = _.last(action.verb.split('#'));
                                verb = _.last(verb.split('/'));
                                allVerbs.push(verb);
            
                            });
                            
                            var uniqueVerbs = _.uniq(allVerbs);
                            _.each(uniqueVerbs, function(verb, index){
                            	
                                var x = 0;
                                if (index < clrs.length) {
                                  x = index;
                                }
                                
                                var obj = {
                                   label: verb,
                                   color: clrs[x],
                                   filter: false
                               };
                               
                               $scope.studentFilters.verblist.push(obj);
                            });

                            activityByVerbChart();
                            hourlyActivityChart();
                            activityDayOfWeek();
                            activityOverTime($scope.studentFilters.detailView);
                        });
                    }   
                });
            }

            function activityByVerbChart () {
                var chartData;
                var colors;
                var options;
                var activityByDateChart;
                var datasets = [];
                var days = [];

				                
	            chartData = {
	                    labels: [],
	                    datasets: [
		                    {
		                        label: 'Total',
		                        data: [],		                        
		                        backgroundColor: [],
		                        borderColor: [],
		                        borderWidth: 1
		                    }
	                    ]
	            };
	              
	              
	            // if the average data exists, then add a column to the chart      
	            var averageDataExists = (typeof $scope.currentCourse.eventTypeAverages !== 'undefined' && $scope.currentCourse.eventTypeAverages !== null);    	            
                if (averageDataExists) {  
	                chartData.datasets.push({
		                        label: 'Class Average',
		                        data: [],	                     
		                        backgroundColor: [],
		                        borderColor: [],
		                        borderWidth: 1
		                    }
	                );                
                } 
                






				//fill in any verbs that the student has
                _.each($scope.actions, function (action) {
                    var verb = _.last(action.verb.split('#'));
                    verb = _.last(verb.split('/'));

                    if (!datasets[verb]) {
                        datasets[verb] = 0;
                    }

                    datasets[verb]++;
                });
              
  
                //fill in any verbs that the course may have averages for, but 
                //that the student didn't have
                _.each($scope.currentCourse.eventTypeAverages, function (value, verb) {

                    if (!datasets[verb]) {
                        datasets[verb] = 0;
                    }
                });
                

                chartData.labels = [];
                _.each($scope.studentFilters.verblist, function (verb) {               
                    chartData.labels.push(verb.label);                                                            
                    chartData.datasets[0].data.push(datasets[verb.label]/$scope.currentCourse.eventTypeAverages[verb.label]);
                    chartData.datasets[0].backgroundColor.push(verb.color);
                    
                    //populate only if the average Data exists
                    if (averageDataExists) {
                    	//since this is a normalized comparison, we set the
                    	//averages as the value to normalize against
                    	chartData.datasets[1].data.push(1);
                    	chartData.datasets[1].backgroundColor.push('rgba(135, 135, 135, 0.7)');
                    }
                })

                options = {
                    maintainAspectRatio: false,
                    stacked: false,
                    scales: {
                        yAxes: [{
                    		display: false,
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    },
                    
                    //Set the tooltips to be the actual values, not the normalized data
                    tooltips: {
				        callbacks: {
				            label: function(tooltipItem) {
				                if (tooltipItem.datasetIndex == 0) {
				                	return $scope.currentStudent.firstName + ": "  + datasets[tooltipItem.label];
				                }
				                else {				                
				                	return "Course Average: " + Math.round($scope.currentCourse.eventTypeAverages[tooltipItem.label]);
				                }
				            }			           
				        }
				    }
                };

                activityByVerbChart = new Chart(document.getElementById('activityByVerbChart'), {
                    type: 'bar',
                    data: chartData,
                    options: options
                });

                $scope.activityByVerbChartData = chartData;
            }

            function hourlyActivityChart () {
                var chartData;
                var colors;
                var options;
                var hourlyActivityChart;
                var hours = [];

                chartData = {
                    labels: [],
                    datasets: [{
                        label: 'Total',
                        data: [],                        
                        backgroundColor: ['rgba(108, 178, 195, 0.5)'],
                        borderColor: [],
                        borderWidth: 1
                    }]
                };

                _.each($scope.actions, function (action) {
                    var hour = moment(action.timestamp).format('HH');
                    // build data based on 24 hour clock
                    if (!hours[hour]) {
                        hours[hour] = 0;
                    }
                    hours[hour]++;
                });

                chartData.labels = ['00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23'];
                _.each(chartData.labels, function (label) {
                    chartData.datasets[0].data.push(hours[label]);
                });

                options = {
                    maintainAspectRatio: false,
                    stacked: true,
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                };

                hourlyActivityChart = new Chart(document.getElementById('hourlyActivityChart'), {
                    type: 'line',
                    data: chartData,
                    options: options
                });

                $scope.hourlyActivityChartData = chartData;

            }

            function activityDayOfWeek (event, detailView) {
                var chartData;
                var colors;
                var options;
                
                var datasets = [];
                var daysOfWeek = [];

                chartData = {
                    labels: [],
                    datasets: []
                };

                _.each($scope.actions, function (action) {
                    var verb = _.last(action.verb.split('#'));
                    verb = _.last(verb.split('/'));
                    var dayOfWeek = moment(action.timestamp).format('dddd');
                    
                    var testDate = new Date(action.timestamp);

                    if (!daysOfWeek[dayOfWeek]) {
                        daysOfWeek[dayOfWeek] = [];
                    }
                    if (!daysOfWeek[dayOfWeek][verb]) {
                        daysOfWeek[dayOfWeek][verb] = 0;
                    }
                    daysOfWeek[dayOfWeek][verb]++;
                });

                // hard coding labels so they can be sorted by type easily
                chartData.labels = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

                _.each($scope.studentFilters.verblist, function (verb, index) {
                    var colors;
                    var set;
                    //if (verb.filter) {
                        colors = [];
                        _.each(chartData.labels, function () {
                            colors.push(verb.color);
                        });
                        set = {
                            label: verb.label,
                            data: [],
                            backgroundColor: colors,
                            borderWidth: 1
                        }
                        chartData.datasets.push(set);
                    //}
                });

                _.each(chartData.labels, function (label) {
                    _.each(chartData.datasets, function (dataset) {
                    	// GG null check added
                    	if (daysOfWeek[label]) {
                    	  dataset.data.push(daysOfWeek[label][dataset.label]);
                    	}
                    });
                });

                options = {
                    maintainAspectRatio: false,
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }],
                        xAxes: [{
                            stacked: true
                        }]
                    }
                };


                if (!$scope.activityDayOfWeekData) {
                    activityDayOfWeekChart = new Chart(document.getElementById('activityDayOfWeekChart'), {
                        type: 'line',
                        data: chartData,
                        options: options
                    });
                } else {
                    activityDayOfWeekChart.data.datasets = chartData.datasets;
                    activityDayOfWeekChart.config.data.labels = chartData.labels; 
                    activityDayOfWeekChart.update();
                }


                $scope.activityDayOfWeekData = chartData;

            }

            function activityOverTime (detailView) {
                var chartData;
                var colors;
                var options;
                
                var datasets = [];
                var daysOfWeek = [];
                var dates = [];

                chartData = {
                    labels: [],
                    datasets: []
                };

                _.each($scope.actions, function (action) {
                    var verb = _.last(action.verb.split('#'));
                    verb = _.last(verb.split('/'));
                    var date = moment(action.timestamp).format('MM-DD');
                    var daysAgo = moment(action.timestamp).diff(moment(), 'days', true);


                    // Build data based on days ago
                    if (detailView === '7days') {
                        if (daysAgo >= -7 && daysAgo <= 0) {
                            if (!dates[date]) {
                                dates[date] = [];
                                _.each($scope.studentFilters.verblist, function (verb) {
                                    dates[date][verb.label] = 0;
                                });
                            }
                            dates[date][verb]++;
                            chartData.labels.push(date);
                        }
                    }

                    if (detailView === '30days') {
                        if (daysAgo >= -30 && daysAgo <= 0) {
                            if (!dates[date]) {
                                dates[date] = [];
                                _.each($scope.studentFilters.verblist, function (verb) {
                                    dates[date][verb.label] = 0;
                                });
                            }
                            dates[date][verb]++;
                            chartData.labels.push(date);
                        }
                    }

                    if (detailView === '90days') {
                        if (daysAgo >= -90 && daysAgo <= 0) {
                            if (!dates[date]) {
                                dates[date] = [];
                                _.each($scope.studentFilters.verblist, function (verb) {
                                    dates[date][verb.label] = 0;
                                });
                            }
                            dates[date][verb]++;
                            chartData.labels.push(date);
                        }
                    }
                });

                // hard coding labels so they can be sorted by type easily
                if (detailView === 'dayOfWeek') {
                    chartData.labels = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
                }

                chartData.labels = _.uniq(chartData.labels);
                chartData.labels = _.sortBy(chartData.labels, function (label) {
                    return label;
                });

                _.each($scope.studentFilters.verblist, function (verb, index) {
                    var colors;
                    var set;
                    if (true) {
                        colors = [];
                        _.each(chartData.labels, function () {
                            colors.push(verb.color);
                        });
                        set = {
                            label: verb.label,
                            data: [],
                            backgroundColor: colors,
                            borderWidth: 1
                        }
                        chartData.datasets.push(set);
                    }
                });

                _.each(chartData.labels, function (label) {
                    _.each(chartData.datasets, function (dataset) {
                        dataset.data.push(dates[label][dataset.label]);
                    });
                });

                options = {
                    maintainAspectRatio: false,
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }],
                        xAxes: [{
                            stacked: true
                        }]
                    }
                };

                if (!$scope.activityOverTimeData) {
                    activityOverTimeChart = new Chart(document.getElementById('activityOverTime'), {
                        type: 'bar',
                        data: chartData,
                        options: options
                    });
                } else {
                    activityOverTimeChart.data.datasets = chartData.datasets;
                    activityOverTimeChart.config.data.labels = chartData.labels; 
                    activityOverTimeChart.update();
                }


                $scope.activityOverTimeData = chartData;

            }

            function getRandomInt(min, max) {
                min = Math.ceil(min);
                max = Math.floor(max);
                return Math.floor(Math.random() * (max - min)) + min;
            }


            $scope.$on('updateTable', function (event, data) {
                if (data) {
                    $scope.studentFilters.detailView = data;
                }
                activityOverTime($scope.studentFilters.detailView);
            });

            $scope.$on('toggleAllFilters', function (event, data) {
                $scope.allFilters = !$scope.allFilters;

                _.each($scope.studentFilters.verblist, function (verb) {
                    verb.filter = $scope.allFilters;
                });
                activityOverTime($scope.studentFilters.detailView);
            });
            
        } 
    );
})(angular);
