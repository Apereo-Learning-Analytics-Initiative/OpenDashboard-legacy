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
    
angular.module('od.cards.pulse', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
  .config(function(registryProvider){
    registryProvider.register('pulse',{
        title: 'Pulse',
        description: '',
        imgUrl: '',
        cardType: 'pulse',
        styleClasses: 'od-card col-xs-12',
      config: [],
      requires: ['ROSTER', 'MODELOUTPUT'],
      uses: []
    });
  })
  .controller('pulseController', function(
    $scope,
    $state,
    $stateParams,
    $translate,
    $translatePartialLoader,
    $q,
    _, 
    OpenDashboard_API,
    ContextMappingService,
    SessionService,
    EventService,
    RosterService,
    ModelOutputDataService,
    EnrollmentDataService) {
      $scope.loaded = true;
      $scope.enrollments = null;
      $scope.classData = null;

      var currentUser = SessionService.getCurrentUser();
      console.log(currentUser);

      EnrollmentDataService.getEnrollmentsForUser(currentUser.tenant_id, currentUser.user_id)
        .then(function(enrollments) {
          console.log(enrollments);
          $scope.enrollments = enrollments;

          EventService.getEventStatisticsForClass(currentUser.tenant_id, 'demo-class-1')
          .then(function (statistics) {
            console.log(statistics);
          });
        });
    });
  })
(angular, Math, moment);
