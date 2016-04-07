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
    .controller('WelcomeController', function($log, $scope, $state, SessionService, ContextMappingService) {
        
        $scope.saveContextMapping = function() {
            var inbound_lti_launch_request = SessionService.getInbound_LTI_Launch();
            
            // TODO handle non-context case
            var cm_options = {};
            cm_options.key = inbound_lti_launch_request.oauth_consumer_key;
            cm_options.context = inbound_lti_launch_request.context_id;
            
            var options = ContextMappingService.createContextMappingInstance(cm_options);

            ContextMappingService.create(options)
            .then(function(savedContextMapping) {
                var cm = ContextMappingService.createContextMappingInstance(savedContextMapping);
                
                var dashboards = cm.dashboards;
                if (dashboards && dashboards.length > 0) {
                    var dashboard = dashboards[0];
                    $log.log('default dashboard: '+dashboard);
    				$state.go('index.dashboard', {cmid:cm.id,dbid:dashboard.id});
                }
                else {
                  // no dashboards
                  $state.go('index.addDashboard', {cmid:cm.id});
                }
            });
        };  
        
        $scope.saveContextMapping();
    });
})(angular);
