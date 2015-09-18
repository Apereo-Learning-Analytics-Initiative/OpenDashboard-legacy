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
	    config: [],
	    requires:['MODELOUTPUT']
    });
 })
 .controller('ModelViewerCardController', function($scope, $log, $translate, 
               $translatePartialLoader, ModelOutputDataService, SessionService) {
    $translatePartialLoader.addPart('modelviewer-card');
    $translate.refresh();
    
    $scope.modeloutput = null;
    $scope.course = SessionService.getCourse();
    
	var user = null;
	if ($scope.isStudent) {
		user = SessionService.getCurrentUser().user_id;
	}
	
	var handleResponse = function (response) {
		if (response.isError) {
		  $scope.isError = true;
		  if (response.data && response.data.data) {
			$scope.errorMessage = response.data.data;
		  }
		  else {
			  $scope.errorMessage = "ERROR_GENERAL";
		  }
		}
		else {
		  $scope.modeloutput = response;
		}
    	
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
