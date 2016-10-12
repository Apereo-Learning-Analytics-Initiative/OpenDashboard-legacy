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

            console.log('StudentViewController');

            $scope.error = null;
            $scope.actions = [];
            $scope.state = $state;

            $scope.activityByVerbChartData = null;
            $scope.activityOverTimeData = null;

            $scope.activityByVerbDisplay = 'chart';
            $scope.activityOverTimeDisplay = 'chart';

            $scope.verbList = [
                {
                    label: 'LoggedIn',
                    color: 'rgba(13, 191, 255, 0.5)',
                    filter: true
                },{
                    label: 'LoggedOut',
                    color: 'rgba(232, 6, 222, 0.5)',
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
                    label: 'Completed',
                    color: 'rgba(6, 11, 178, 0.5)',
                    filter: true
                },{
                    label: 'Submitted',
                    color: 'rgba(84, 255, 0, 0.5)',
                    filter: true
                },{
                    label: 'Searched',
                    color: 'rgba(232, 163, 12, 0.5)',
                    filter: true
                },{
                    label: 'Shared',
                    color: 'rgba(50, 12, 132, 0.5)',
                    filter: true
                },{
                    label: 'Bookmarked',
                    color: 'rgba(13, 175, 255, 0.5)',
                    filter: true
                },{
                    label: 'Commented',
                    color: 'rgba(248, 255, 0, 0.5)',
                    filter: true
                },{
                    label: 'Recommended',
                    color: 'rgba(232, 143, 12, 0.5)',
                    filter: true
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
                            console.log(actions);
                            $scope.actions = actions.content;
                            activityByVerbChart();
                            activityOverTime();
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
                        backgroundColor: ['rgba(255, 99, 132, 0.5)','rgba(255, 99, 132, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(255, 206, 86, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)','rgba(75, 192, 192, 0.5)'],
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
                })

                console.log(chartData.labels);

                _.each(chartData.labels, function (verb) {
                    chartData.datasets[0].data.push(datasets[verb]);
                });

                //chartData.labels = _.uniq(chartData.labels);

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

            function activityOverTime () {
                var chartData;
                var colors;
                var options;
                
                var datasets = [];
                var days = [];
                var daysOfWeek = [];
                var verbs = [];

                chartData = {
                    labels: [],
                    datasets: [{
                        label: 'Total',
                        data: [],
                        backgroundColor: [],
                        borderWidth: 1
                    }]
                };

                _.each($scope.actions, function (action) {
                    //console.log(action);
                    var verb = _.last(action.verb.split('#'));
                    var dayOfWeek = moment(action.timestamp).format('dddd');


                    if (!daysOfWeek[dayOfWeek]) {
                        daysOfWeek[dayOfWeek] = {
                            total: 0
                        };
                    }

                    if (!daysOfWeek[dayOfWeek][verb]) {
                        daysOfWeek[dayOfWeek][verb] = 0;
                    }

                    daysOfWeek[dayOfWeek].total++;
                    daysOfWeek[dayOfWeek][verb]++;
                });

                // hard coding labels so they can be sorted by type easily
                chartData.labels = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

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

                _.each(chartData.labels, function (dayOfWeek) {
                    _.each(chartData.datasets, function (dataset) {
                        if (dataset.label === 'Total') {
                            //dataset.data.push(daysOfWeek[dayOfWeek].total)
                        } else {
                            dataset.data.push(daysOfWeek[dayOfWeek][dataset.label])
                        }
                    });
                });

                //chartData.labels = _.uniq(chartData.labels);

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

                console.log(chartData);

                if (!$scope.activityOverTimeData) {
                    activityOverTimeChart = new Chart(document.getElementById('activityOverTime'), {
                        type: 'line',
                        data: chartData,
                        options: options
                    });
                } else {
                    activityOverTimeChart.data.datasets = chartData.datasets;
                    activityOverTimeChart.update();
                }


                $scope.activityOverTimeData = chartData;

            }

            function getRandomInt(min, max) {
                min = Math.ceil(min);
                max = Math.floor(max);
                return Math.floor(Math.random() * (max - min)) + min;
            }

            $scope.$on('filterChange', activityOverTime);

            
        } 
    );
})(angular);
