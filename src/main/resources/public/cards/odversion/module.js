(function(angular) {
'use strict';

angular
.module('od.cards.version',['OpenDashboardRegistry'])
.config(function(registryProvider) {
	registryProvider.register('odversion',{
		title: 'OpenDashboard Version',
		description: 'Displays the OpenDashboard version',
		imgUrl: '/img/od.png',
		cardType: 'odversion',
		styleClasses: 'od-card col-xs-12 col-sm-6 col-md-3'
	});
});
})(angular);