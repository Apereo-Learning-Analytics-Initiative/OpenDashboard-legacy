var RssReaderCard = angular.module('RssReaderCard', ['ngResource']);

RssReaderCard.factory('RssReaderCardFactory', function ($resource) {
		return $resource('http://ajax.googleapis.com/ajax/services/feed/load', {}, {
			fetch: { method: 'JSONP', params: {v: '1.0', callback: 'JSON_CALLBACK'} }
		});
	});

RssReaderCard.controller('RssReaderCardController', function ($scope, RssReaderCardFactory) {
		$scope.feed = null;
		RssReaderCardFactory.fetch({q: $scope.selectedCard.config.url, num: 10}, {}, function (data) {
			$scope.feed = data.responseData.feed;
		});
	});