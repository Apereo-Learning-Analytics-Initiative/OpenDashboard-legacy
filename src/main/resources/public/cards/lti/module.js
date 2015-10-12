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
(function(angular, JSON, $) {
'use strict';
    
angular
.module('od.cards.lti', ['OpenDashboardAPI', 'OpenDashboardRegistry'])
.config(function(registryProvider){
    registryProvider.register('lti',{
        title: 'Learning Tool Interoperability',
        description: 'Use this card to launch out to an LTI tool provider',
        cardType: 'lti',
        styleClasses: 'od-card col-xs-12',
        config: [
          {field:'launchUrl',fieldName:'Launch URL',fieldType:'url',required:true,translatableLabelKey:'LABEL_LAUNCH_URL'},
          {field:'key',fieldName:'Consumer Key',fieldType:'text',required:true,translatableLabelKey:'LABEL_CONSUMER_KEY'},
          {field:'secret',fieldName:'Consumer Secret',fieldType:'password',required:true,translatableLabelKey:'LABEL_CONSUMER_SECRET'}
        ]
    });
})
.controller('LtiCardController', function($scope, $timeout, SessionService, LtiProxyService) {
    $scope.readyToLaunch = false;
    $scope.outboundLaunch = null;

    LtiProxyService.post($scope.contextMapping.id,$scope.card.id,SessionService.getInbound_LTI_Launch())
        .then(function(proxiedLaunch){
            $scope.outboundLaunch = proxiedLaunch;
            $timeout(function() {
                var selector = '#' + $scope.card.id + ' > #lti_launch_form';
                $(selector).attr('action', $scope.outboundLaunch.launchUrl);
                $(selector).submit();
            }, 2000);
        });
})
.service('LtiProxyService', function($http) {
    return {
        post : function (contextMappingId,cardId,inboundLaunch) {
            var promise =
            $http({
                method  : 'POST',
                url     : '/api/'+contextMappingId+'/lti/launch/'+cardId,
                data    : JSON.stringify(inboundLaunch),
                headers : { 'Content-Type': 'application/json' }
            })
            .then(function (response) {
                if (response.data) {
                    return response.data;
                }
                else {
                    return null;
                }
            });
            return promise;
        }
    }
});
})(angular, JSON, $);
