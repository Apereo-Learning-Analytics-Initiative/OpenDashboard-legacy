(function() {
	'use strict';
	
	angular
		.module('RssReaderCard', ['ngResource'])
		.factory('RssReaderCardFactory', function ($resource) {
			return $resource('http://ajax.googleapis.com/ajax/services/feed/load', {}, {
				fetch: { method: 'JSONP', params: {v: '1.0', callback: 'JSON_CALLBACK'} }
			});
		})
		.controller('RssReaderCardController', function ($scope, RssReaderCardFactory) {
			$scope.feed = null;
			RssReaderCardFactory.fetch({q: $scope.selectedCard.config.url, num: 10}, {}, function (data) {
				$scope.feed = data.responseData.feed;
			});
		});
})();