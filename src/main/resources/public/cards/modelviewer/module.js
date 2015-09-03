(function(angular) {
'use strict';
    
angular
.module('od.cards.modelviewer', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('modelviewer',{
        title: 'Model Viewer',
        description: 'Use this card to view model output.',
        cardType: 'modelviewer',
        styleClasses: 'od-card col-xs-12',
	    config: [
          {field:'url',fieldName:'URL',fieldType:'url',required:true},
          {field:'key',fieldName:'Key',fieldType:'text',required:true},
          {field:'secret',fieldName:'Secret',fieldType:'text',required:true}
	    ]
    });
 })
 .controller('ModelViewerCardController', function($scope, $log, $translate, 
               $translatePartialLoader, ModelOutputDataService, ContextService) {
    $translatePartialLoader.addPart('modelviewer-card');
    $translate.refresh();
    
    $scope.modeloutput = null;
    $scope.course = ContextService.getCourse();
    
	var user = null;
	if ($scope.isStudent) {
		user = ContextService.getCurrentUser().user_id;
	}
	
	var handleResponse = function (output) {
    	$scope.modeloutput = output;
	};
	
	var options = {};
	options.contextMappingId = $scope.contextMapping.id;
	options.dashboardId = $scope.activeDashboard.id;
	options.cardId = $scope.card.id;
	options.courseId = $scope.course.id;

    if (user) {
    	ModelOutputDataService.getModelOutputForUser(options,user.id)
		.then(handleResponse);
	}
	else {
		ModelOutputDataService.getModelOutputForCourse(options,$scope.course.id)
		.then(handleResponse);
	}

});

})(angular);
