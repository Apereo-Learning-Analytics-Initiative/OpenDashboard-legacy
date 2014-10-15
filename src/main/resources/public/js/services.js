var OpenDashboardServices = angular.module('OpenDashboardServices', []);

OpenDashboardServices.service('ContextMappingService', function($http) {
	return {
		create : function (contextMapping) {
			var promise =
			$http({
		        method  : 'POST',
		        url     : '/api/consumer/'+contextMapping.key+'/context',
		        data    : JSON.stringify(contextMapping),
		        headers : { 'Content-Type': 'application/json' }
			})
			.then(function (response) {
				return response.data;
			});
			return promise;
		},

		get : function (key,context) {
			var promise =
			$http({
		        method  : 'GET',
		        url     : '/api/consumer/'+key+'/context/'+context,
		        headers : { 'Content-Type': 'application/json' }
			})
			.then(function (response) {
				if (response.data) {
					return new ContextMapping(response.data);
				}
				else {
					return null;
				}
				
				
			});
			return promise;
		}
	}
});

OpenDashboardServices.service('CardService', function($http) {
	return {
		get : function (context) {
			var promise =
			$http({
		        method  : 'GET',
		        url     : '/api/cards',
		        headers : { 'Content-Type': 'application/json' }
			})
			.then(function (response) {
				if (response.data) {
					var cards = [];
					
					angular.forEach(response.data, function(value,index) {
						cards.push(new Card(value));
					});
					
					return cards;
				}
				else {
					return null;
				}
			});
			return promise;
		}
	}
});

OpenDashboardServices.service('CardInstanceService', function($http) {
	return {
		create : function (cardInstance) {
			var promise =
			$http({
		        method  : 'POST',
		        url     : '/api/cards/'+cardInstance.context,
		        data    : JSON.stringify(cardInstance),
		        headers : { 'Content-Type': 'application/json' }
			})
			.then(function (response) {
				return response.data;
			});
			return promise;
		},
		get : function (context) {
			var promise =
			$http({
		        method  : 'GET',
		        url     : '/api/cards/'+context,
		        headers : { 'Content-Type': 'application/json' }
			})
			.then(function (response) {
				if (response.data) {
					var cardInstances = [];
					
					angular.forEach(response.data, function(value,index) {
						cardInstances.push(new CardInstance(value));
					});
					
					return cardInstances;
				}
				else {
					return [];
				}
			});
			return promise;
		}
	}
});

