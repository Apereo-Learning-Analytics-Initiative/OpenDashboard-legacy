'use strict';

angular
.module('OpenDashboard')
.controller('AdminCtrl',

function AdminCtrl($scope, $state, $translate, $translatePartialLoader, providerTypes) {
  $translatePartialLoader.addPart('admin');
  $translate.refresh();

  $scope.providerTypes = providerTypes;
  
  $scope.selectProviderType = function(providerType) {
	$state.go('index.admin.providers', {providerType:providerType});
  };
  
});

angular
.module('OpenDashboard')
.controller('ProviderListCtrl',

function ProviderListCtrl($scope, $state, $translate, $translatePartialLoader, _, providerType, providers, providerData) {
  $translatePartialLoader.addPart('providerlist');
  $translate.refresh();

  $scope.providers = providers;
  $scope.providerType = providerType;
  $scope.providerData = providerData;
  
  $scope.isConfigured = function(providerKey) {
	var isConfigured = false;
	
	if ($scope.providerData) {
	  var indx = _.find($scope.providerData,{'providerKey':providerKey});
	  if (indx) isConfigured = true;
	}
	
	return isConfigured;
  }
  
  $scope.configureProvider = function(providerType, providerKey) {
	$state.go('index.admin.configureprovider', {providerType:providerType,providerKey:providerKey});
  };
   
  $scope.editProvider = function(providerType, providerKey) {
	$state.go('index.admin.editconfigureprovider', {providerType:providerType,providerKey:providerKey});
  };
  
  $scope.deleteProvider = function(providerType, providerKey) {
	$state.go('index.admin.deleteconfigureprovider', {providerType:providerType,providerKey:providerKey});
  };


});

angular
.module('OpenDashboard')
.controller('ConfigureProviderCtrl',

function ProviderListCtrl($scope, $state, $translate, $translatePartialLoader, _, ProviderService, Notification, providerType, provider) {
	$translatePartialLoader.addPart('configureprovider');
	$translate.refresh();
	
	$scope.config = {};
	$scope.submitted = false;

	$scope.provider = provider;
	$scope.providerType = providerType;
	
	$scope.save = function () {
	  $scope.submitted = true;

	  var providerData = {};
	  providerData.providerType = $scope.providerType;
	  providerData.providerKey = $scope.provider.key;
	  
	  var configOptions = $scope.provider.providerConfiguration.options;
	  
	  providerData.options = _.map($scope.config,function(value,key){
		  var configOption = _.findWhere(configOptions,{'key':key});
		  var option = {};
		  option.key = key;
		  option.value = value;
		  option.required = configOption.required;
		  option.encrypt = configOption.encrypt;
		  
		  return option;
	  });
	  
	  ProviderService
	  .create(providerData)
	  .then(function(data){
		  Notification.success('Provider data saved');
		  $scope.submitted = false;
		  $state.go('index.admin.providers', {providerType:$scope.providerType}, { reload: true });
	  }
	  ,function(error){
		  Notification.error('Unable to save provider data');
		  $scope.submitted = false;
	  });
	}
	
});

angular
.module('OpenDashboard')
.controller('EditConfigureProviderCtrl',

function ProviderListCtrl($scope, $state, $translate, $translatePartialLoader, _, ProviderService, Notification, providerType, provider, providerData) {
	$translatePartialLoader.addPart('configureprovider');
	$translate.refresh();
	
	$scope.config = {};
	$scope.submitted = false;

	$scope.provider = provider;
	$scope.providerType = providerType;
	$scope.providerData = providerData;
	
	$scope.getTranslatableLabel = function (key) {
	  var label = null;
	  
	  if (provider.providerConfiguration && provider.providerConfiguration.options) {
	    var option = _.find(provider.providerConfiguration.options,{'key':key});
	    if (option) {
	      if (option.translatableLabelKey) {
	    	label = option.translatableLabelKey;  
	      }
	      else if (option.label) {
	    	label = option.label;  
	      }
	    }
	  }
	  
	  return label;
	}
	
	
	$scope.save = function () {
	  $scope.submitted = true;

	  ProviderService
	  .update($scope.providerData)
	  .then(function(data){
		  Notification.success('Provider data saved');
		  $scope.submitted = false;
		  $state.go('index.admin.providers', {providerType:$scope.providerType});
	  }
	  ,function(error){
		  Notification.error('Unable to save provider data');
		  $scope.submitted = false;
	  });
	}
	
});

angular
.module('OpenDashboard')
.controller('DeleteConfigureProviderCtrl',

function ProviderListCtrl($scope, $state, $translate, $translatePartialLoader, _, ProviderService, Notification, providerType, provider, providerData) {
	$translatePartialLoader.addPart('configureprovider');
	$translate.refresh();
	
	$scope.config = {};
	$scope.submitted = false;

	$scope.provider = provider;
	$scope.providerType = providerType;
	$scope.providerData = providerData;
	
	$scope.remove = function () {
	  $scope.submitted = true;
	  
	  ProviderService
	  .remove($scope.providerData)
	  .then(function(data){
		  Notification.success('Provider data deleted');
		  $scope.submitted = false;
		  $state.go('index.admin.providers', {providerType:$scope.providerType} , { reload: true });
	  }
	  ,function(error){
		  Notification.error('Unable to delete provider data');
		  $scope.submitted = false;
	  });
	}
	
});