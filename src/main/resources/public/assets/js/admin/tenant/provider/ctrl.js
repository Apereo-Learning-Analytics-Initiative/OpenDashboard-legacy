/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
'use strict';

angular
.module('OpenDashboard')
.controller('ProviderListCtrl',

function ProviderListCtrl($scope, $state, _, providerType, providers) {

  $scope.providers = providers;
  $scope.providerType = providerType;
  
  $scope.isConfigured = function(providerKey) {
	var isConfigured = false;
	
	if ($scope.tenant.providerData) {
	  var indx = _.find($scope.tenant.providerData,{'providerKey':providerKey});
	  if (indx) isConfigured = true;
	}
	
	return isConfigured;
  }
  
  $scope.configureProvider = function(providerType, providerKey) {
	$state.go('index.admin.tenants.tenant.provider.configure', {providerType:providerType,providerKey:providerKey});
  };
   
  $scope.editProvider = function(providerType, providerKey) {
	$state.go('index.admin.tenants.tenant.provider.edit', {providerType:providerType,providerKey:providerKey});
  };
  
  $scope.deleteProvider = function(providerType, providerKey) {
	$state.go('index.admin.tenants.tenant.provider.delete', {providerType:providerType,providerKey:providerKey});
  };


});

angular
.module('OpenDashboard')
.controller('ConfigureProviderCtrl',

function ConfigureProviderCtrl($scope, $state, _, TenantService, Notification, providerType, provider) {
	
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
		  var configOption = _.find(configOptions, function (obj) {
		  	return obj.key === 'key';
		  });
		  var option = {};
		  option.key = key;
		  option.value = value;
		  option.required = configOption.required;
		  option.encrypt = configOption.encrypt;
		  
		  return option;
	  });
	  
	  if (!$scope.tenant.providerData) {
	    $scope.tenant.providerData = [];
	  }
	  $scope.tenant.providerData.push(providerData);

	  TenantService
	  .updateTenant($scope.tenant)
	  .then(function(data){
		  Notification.success('Provider data saved');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant.provider', {providerType:$scope.providerType}, { reload: true });
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

function EditConfigureProviderCtrl($scope, $state, _, ProviderService, TenantService, Notification, providerType, provider, providerData) {
	
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
	
    $scope.getType = function (key) {
      var type = null;
      
      if (provider.providerConfiguration && provider.providerConfiguration.options) {
        var option = _.find(provider.providerConfiguration.options,{'key':key});
        if (option && option.type) {
            type = option.type;  
        }
      }
      
      return type;
    }
	
	$scope.save = function () {
	  $scope.submitted = true;
	  
      var index = _.indexOf($scope.tenant.providerData, _.find($scope.tenant.providerData, { 'providerType': $scope.providerData.providerType, 'providerKey': $scope.providerData.providerKey }));
      $scope.tenant.providerData.splice(index, 1, $scope.providerData);

	  TenantService
	  .updateTenant($scope.tenant)
	  .then(function(data){
		  Notification.success('Provider data saved');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant.provider', {providerType:$scope.providerType});
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

function DeleteConfigureProviderCtrl($scope, $state, _, ProviderService, TenantService, Notification, providerType, provider, providerData) {
	
	$scope.config = {};
	$scope.submitted = false;

	$scope.provider = provider;
	$scope.providerType = providerType;
	$scope.providerData = providerData;
	
	$scope.remove = function () {
	  $scope.submitted = true;
	  
      var index = _.indexOf($scope.tenant.providerData, _.find($scope.tenant.providerData, { 'providerType': $scope.providerData.providerType, 'providerKey': $scope.providerData.providerKey }));
      $scope.tenant.providerData.splice(index, 1);

	  TenantService
	  .updateTenant($scope.tenant)
	  .then(function(data){
		  Notification.success('Provider data deleted');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant.provider', {providerType:$scope.providerType} , { reload: true });
	  }
	  ,function(error){
		  Notification.error('Unable to delete provider data');
		  $scope.submitted = false;
	  });
	}
	
});

