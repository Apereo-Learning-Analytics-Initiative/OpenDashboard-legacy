(function(angular, JSON, $) {
'use strict';
    
angular
.module('od.cards.lti', ['OpenDashboardAPI', 'OpenDashboardRegistry'])
.config(function(registryProvider){
    registryProvider.register('lti',{
        title: 'LTI',
        description: 'Use this card to launch out to an LTI tool',
        imgUrl: 'https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQ8F5Og94mVZUAFy7fMqcmv5NZUJMqH8j0FcgvFzete2Z5YJClgDQ',
        cardType: 'lti',
        styleClasses: 'od-card col-xs-12',
        config: [
          {field:'launchUrl',fieldName:'Launch URL',fieldType:'url',required:true},
          {field:'key',fieldName:'Consumer Key',fieldType:'text',required:true},
          {field:'secret',fieldName:'Consumer Secret',fieldType:'text',required:true}
        ]
    });
})
.controller('LtiCardController', function($scope, $timeout, ContextService, LtiProxyService) {
    $scope.readyToLaunch = false;
    $scope.outboundLaunch = null;

    LtiProxyService.post($scope.contextMapping.id,$scope.card.id,ContextService.getInbound_LTI_Launch())
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
