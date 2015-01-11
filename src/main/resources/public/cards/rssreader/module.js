(function() {
'use strict';
	
angular
.module('od.cards.rssreader', ['ngResource'])
.config(function(registryProvider){
	registryProvider.register('rssreader',{
		title: 'RSS Reader',
		description: 'Use this card to display an RSS feed',
		imgUrl: '/cards/rssreader/rss.png',
		cardType: 'rssreader',
		styleClasses: 'od-card col-xs-12 col-md-6',
		config: [
		  {field:'url',fieldName:'URL',fieldType:'url',required:true}
		]
	});
})
.factory('RssReaderCardFactory', function ($resource) {
	return $resource('http://ajax.googleapis.com/ajax/services/feed/load', {}, {
		fetch: { method: 'JSONP', params: {v: '1.0', callback: 'JSON_CALLBACK'} }
	});
})
.controller('RssReaderCardController', function ($scope, RssReaderCardFactory) {
	$scope.feed = null;
	RssReaderCardFactory.fetch({q: $scope.card.config.url, num: 10}, {}, function (data) {
		$scope.feed = data.responseData.feed;
	});
});
})();