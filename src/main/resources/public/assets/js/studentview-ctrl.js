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
            $scope.enrollments = null;

            currentUser = SessionService.getCurrentUser();

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
                        .then(function (statistics) {
                            console.log(statistics);
                        });
                    }   
                });
            }
        } 
    );
})(angular);
