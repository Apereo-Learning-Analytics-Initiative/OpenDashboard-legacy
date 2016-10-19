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

            console.log('StudentViewController');

            $scope.error = null;
            $scope.actions = [];
            $scope.state = $state;

            $scope.activityByVerbChartData = null;
            $scope.activityOverTimeData = null;

            $scope.displayMode = 'chart';
            $scope.detailView = '30days';
            $scope.allFilters = false;

            $scope.verbList = [
                {
                    label: 'Completed',
                    color: 'rgba(6, 11, 178, 0.5)',
                    filter: true
                },{
                    label: 'Submitted',
                    color: 'rgba(84, 255, 0, 0.5)',
                    filter: true
                },{
                    label: 'NavigatedTo',
                    color: 'rgba(255, 132, 7, 0.5)',
                    filter: true
                },{
                    label: 'Viewed',
                    color: 'rgba(29, 178, 37, 0.5)',
                    filter: true
                },{
                    label: 'LoggedIn',
                    color: 'rgba(13, 191, 255, 0.5)',
                    filter: false
                },{
                    label: 'LoggedOut',
                    color: 'rgba(232, 6, 222, 0.5)',
                    filter: false
                },{
                    label: 'Searched',
                    color: 'rgba(232, 163, 12, 0.5)',
                    filter: false
                },{
                    label: 'Shared',
                    color: 'rgba(50, 12, 132, 0.5)',
                    filter: false
                },{
                    label: 'Bookmarked',
                    color: 'rgba(13, 175, 255, 0.5)',
                    filter: false
                },{
                    label: 'Commented',
                    color: 'rgba(248, 255, 0, 0.5)',
                    filter: false
                },{
                    label: 'Recommended',
                    color: 'rgba(232, 143, 12, 0.5)',
                    filter: false
                }];

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
                            activityByVerbChart();
                            hourlyActivityChart();
                            activityDayOfWeek();
                            activityOverTime($scope.detailView);
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
                    datasets: [{
                        label: 'Total',
                        data: [],
                        //backgroundColor: ['rgba(255, 99, 132, 0.5)','rgba(255, 99, 132, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)'],
                        backgroundColor: [],
                        borderColor: [],
                        borderWidth: 1
                    }]
                };

                _.each($scope.actions, function (action) {
                    //console.log(action);
                    var verb = _.last(action.verb.split('#'));

                    if (!datasets[verb]) {
                        datasets[verb] = 0;
                    }

                    datasets[verb]++;
                });

                chartData.labels = [];
                _.each($scope.verbList, function (verb) {
                    chartData.labels.push(verb.label);
                    chartData.datasets[0].data.push(datasets[verb.label]);
                    chartData.datasets[0].backgroundColor.push(verb.color);
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
                        //backgroundColor: ['rgba(255, 99, 132, 0.5)','rgba(255, 99, 132, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)'],
                        backgroundColor: ['rgba(108, 178, 195, 0.5)'],
                        borderColor: [],
                        borderWidth: 1
                    }]
                };

                _.each($scope.actions, function (action) {
                    //console.log(action);
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
                    //console.log(action);
                    var verb = _.last(action.verb.split('#'));
                    var dayOfWeek = moment(action.timestamp).format('dddd');

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

                _.each($scope.verbList, function (verb, index) {
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
                        dataset.data.push(daysOfWeek[label][dataset.label]);
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
                    //console.log(action);
                    var verb = _.last(action.verb.split('#'));
                    var date = moment(action.timestamp).format('MM-DD');
                    var daysAgo = moment(action.timestamp).diff(moment(), 'days', true);


                    // Build data based on days ago
                    if (detailView === '7days') {
                        if (daysAgo >= -7 && daysAgo <= 0) {
                            if (!dates[date]) {
                                dates[date] = [];
                                _.each($scope.verbList, function (verb) {
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
                                _.each($scope.verbList, function (verb) {
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

                _.each($scope.verbList, function (verb, index) {
                    var colors;
                    var set;
                    if (verb.filter) {
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
                    $scope.detailView = data;
                }
                activityOverTime($scope.detailView);
            });

            $scope.$on('toggleAllFilters', function (event, data) {
                console.log($scope.allFilters);

                $scope.allFilters = !$scope.allFilters;

                _.each($scope.verbList, function (verb) {
                    verb.filter = $scope.allFilters;
                });
                activityOverTime($scope.detailView);
            });
            
        } 
    );
})(angular);
