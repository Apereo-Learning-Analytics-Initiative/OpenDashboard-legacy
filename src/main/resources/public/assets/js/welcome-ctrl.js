(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('WelcomeController', function($log, $scope, $state, $translate, $translatePartialLoader, 
    											ContextService, ContextMappingService) {    
    	$translatePartialLoader.addPart('welcome');
        $translate.refresh();
        
        $scope.saveContextMapping = function() {
            var inbound_lti_launch_request = ContextService.getInbound_LTI_Launch();
            
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
                    url = url + '/' + dashboard.id;
                }
                else {
                  // no dashboards
                  $state.go('index.addDashboard', {cmid:cm.id});
                }
            });
        };        
    });
})(angular);
