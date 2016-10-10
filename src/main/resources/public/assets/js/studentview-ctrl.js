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

            console.log('StudentViewController');

            $scope.error = null;
            $scope.actions = null;
            $scope.state = $state;

            $scope.activityByVerbChartData = null;
            $scope.activityByDateChartData = null;

            $scope.activityByVerbDisplay = 'chart';
            $scope.activityByDateDisplay = 'chart';

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
                            activityByVerbChart(actions.content);
                            activityByDateChart(actions.content);
                        });
                    }   
                });
            }

            function activityByVerbChart (actions) {
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
                        backgroundColor: ['rgba(255, 99, 132, 0.2)', 'rgba(255, 206, 86, 0.2)', 'rgba(75, 192, 192, 0.2)'],
                        borderColor: [],
                        borderWidth: 1
                    },
                    {
                        label: 'Average Per Day',
                        data: [],
                        backgroundColor: ['rgba(255, 99, 132, 0.5)', 'rgba(255, 206, 86, 0.5)', 'rgba(75, 192, 192, 0.5)'],
                        borderColor: [],
                        borderWidth: 1
                    }]
                };

                _.each(actions, function (action) {
                    //console.log(action);
                    var day = _.first(action.timestamp.split(' '));
                    var time = _.last(action.timestamp.split(' '));
                    var verb = _.last(action.verb.split('#'));

                    console.log(day, time, verb);

                    days.push(day);

                    if (!datasets[verb]) {
                        datasets[verb] = 0;
                        chartData.labels.push(verb);
                    }

                    datasets[verb]++;
                });

                days = _.uniq(days);

                _.each(chartData.labels, function (verb) {
                    console.log(verb);
                    chartData.datasets[0].data.push(datasets[verb]);

                    chartData.datasets[1].data.push(days.length/datasets[verb]);
                });


                console.log(datasets);

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

            function activityByDateChart (actions) {
                var chartData;
                var colors;
                var options;
                var activityByDateChart;
                var datasets = [];
                var days = [];
                var verbs = [];

                chartData = {
                    labels: [],
                    datasets: [{
                        label: 'Bookmarked',
                        data: [],
                        backgroundColor: ['rgba(255, 99, 132, 0.2)', 'rgba(255, 206, 86, 0.2)', 'rgba(75, 192, 192, 0.2)'],
                        borderColor: [],
                        borderWidth: 1
                    }]
                };

                _.each(actions, function (action) {
                    //console.log(action);
                    var day = _.first(action.timestamp.split(' '));
                    var time = _.last(action.timestamp.split(' '));
                    var verb = _.last(action.verb.split('#'));

                    console.log(day, time, verb);

                    days.push(day);
                    verbs.push(verb);

                    chartData.labels.push(day);
                });

                chartData.labels = _.uniq(chartData.labels);


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

                activityByDateChart = new Chart(document.getElementById('activityByDateChart'), {
                    type: 'bar',
                    data: chartData,
                    options: options
                });

                $scope.activityByDateChartData = chartData;

            }

            
        } 
    );
})(angular);
