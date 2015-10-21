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
.controller('AdminCtrl',

function AdminCtrl($scope, $state, providerTypes) {

  $scope.providerTypes = providerTypes;
  
  $scope.selectProviderType = function(providerType) {
	$state.go('index.admin.providers', {providerType:providerType});
  };
  
});

angular
.module('OpenDashboard')
.controller('ProviderListCtrl',

function ProviderListCtrl($scope, $state, _, providerType, providers, providerData) {

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

function ProviderListCtrl($scope, $state, _, ProviderService, Notification, providerType, provider) {
	
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

function ProviderListCtrl($scope, $state, _, ProviderService, Notification, providerType, provider, providerData) {
	
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

function ProviderListCtrl($scope, $state, _, ProviderService, Notification, providerType, provider, providerData) {
	
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

angular
.module('OpenDashboard')
.controller('PreconfigureDashboardsCtrl',

function PreconfigureDashboardsCtrl($scope, $state, _, preconfiguredDashboards) {
	
	$scope.preconfiguredDashboards = preconfiguredDashboards;
});

angular
.module('OpenDashboard')
.controller('AddPreconfiguredDashboardCtrl',

function AddPreconfiguredDashboardCtrl($log, $scope, $state, _, DashboardService, Notification, registry) {
	$scope.cards = registry.registry;
	$scope.dashboard = {};
	
	  $scope.isConfigured = function(cardType) {
		var isConfigured = false;
		
		if ($scope.dashboard.cards) {
		  var indx = _.find($scope.dashboard.cards,{'cardType':cardType});
		  if (indx) isConfigured = true;
		}
		
		return isConfigured;
	  }
	
	$scope.addCard = function(card) {
		$log.debug($scope.cards);
	    $log.debug(card);
	    $log.debug(card.cardType);
        var newCard = angular.copy($scope.cards[card.cardType]);
        $log.debug(newCard);
        
        var cardConfig = newCard.config;
        if (cardConfig && cardConfig.length > 0) {
          // TODO
        }
        else {
          newCard.config = {};
          if (!$scope.dashboard.cards) {
            $scope.dashboard.cards = [];
          }
          $scope.dashboard.cards.push(newCard);
        }
	};
	
	$scope.removeCard = function(card) {
	  $scope.dashboard.cards = _.reject($scope.dashboard.cards,{'cardType':card.cardType});
	};
	
	$scope.save = function () {
	  $scope.submitted = true;
	  DashboardService
	  .createPreconfigured($scope.dashboard)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard created');
		  $scope.submitted = false;
		  $state.go('index.admin.dashboards', { reload: true });
		},
		function(error) {
		  Notification.error('Unable to create preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
	
});

angular
.module('OpenDashboard')
.controller('EditPreconfiguredDashboardCtrl',

function EditPreconfiguredDashboardCtrl($log, $scope, $state, _, DashboardService, Notification, registry, preconfiguredDashboard) {
	$scope.cards = registry.registry;
	$scope.dashboard = preconfiguredDashboard;
	
	  $scope.isConfigured = function(cardType) {
		var isConfigured = false;
		
		if ($scope.dashboard.cards) {
		  var indx = _.find($scope.dashboard.cards,{'cardType':cardType});
		  if (indx) isConfigured = true;
		}
		
		return isConfigured;
	  }
	
	$scope.addCard = function(card) {
		$log.debug($scope.cards);
	    $log.debug(card);
	    $log.debug(card.cardType);
        var newCard = angular.copy($scope.cards[card.cardType]);
        $log.debug(newCard);
        
        var cardConfig = newCard.config;
        if (cardConfig && cardConfig.length > 0) {
          // TODO
        }
        else {
          newCard.config = {};
          if (!$scope.dashboard.cards) {
            $scope.dashboard.cards = [];
          }
          $scope.dashboard.cards.push(newCard);
        }
	};
	
	$scope.removeCard = function(card) {
	  $scope.dashboard.cards = _.reject($scope.dashboard.cards,{'cardType':card.cardType});
	};
	
	$scope.save = function () {
	  $scope.submitted = true;
	  DashboardService
	  .updatePreconfigured($scope.dashboard)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard updated');
		  $scope.submitted = false;
		  $state.go('index.admin.dashboards', { reload: true });
		},
		function(error) {
		  Notification.error('Unable to create preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
	
});

angular
.module('OpenDashboard')
.controller('RemovePreconfiguredDashboardCtrl',

function RemovePreconfiguredDashboardCtrl($scope, $state, _, DashboardService, Notification, preconfiguredDashboard) {

	$scope.preconfiguredDashboard = preconfiguredDashboard;
	
	$scope.remove = function () {
	  $scope.submitted = true;
	  DashboardService
	  .removePreconfigured($scope.preconfiguredDashboard.id)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard removed');
		  $scope.submitted = false;
		  $state.go('index.admin.dashboards', { reload: true });
		},
		function(error) {
		  Notification.error('Unable to remove preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
});

angular
.module('OpenDashboard')
.controller('SettingsCtrl',

function SettingsCtrl($scope, $state, _, configuredSettings) {
 $scope.configuredSettings = configuredSettings;
 $scope.isConfigured = !_.isEmpty(configuredSettings)
})
.controller('AddSettingsCtrl',

function AddSettingsCtrl($scope, $state, SettingService, Notification) {
  
  $scope.save = function () {
    SettingService.createSetting($scope.setting)
    .then(
        function(data) {
          Notification.success('Saved setting.');
          $state.go('index.admin.settings');
          console.log("Success", data);
        },
        function(error) {
          Notification.error('Unable to save setting');
          $state.go('index.admin.settings');
          console.log("Error", error);
        }
    );
  }
})
.controller('EditSettingsCtrl',

function EditSettingsCtrl($scope, $state, _, configuredSettings, SettingService, Notification) {

    $scope.configuredSettings = configuredSettings;
    
    $scope.save = function () {
      SettingService.updateSettings($scope.configuredSettings)
      .then(
          function(data) {
            Notification.success('Updated setting.');
            $state.go('index.admin.settings');
          },
          function(error) {
            Notification.error('Unable to update setting');
            $state.go('index.admin.settings');
          }
      );
    }
})
.controller('RemoveSettingsCtrl',

function RemoveSettingsCtrl($scope, $state, _, configuredSettings, SettingService, Notification) {
  
  $scope.configuredSettings = configuredSettings;
  
  $scope.remove = function (setting) {
    SettingService.removeSetting(setting.id)
    .then(
        function(data) {
          Notification.success('Setting has been removed.');
          $state.go('index.admin.settings');
        },
        function(error) {
          Notification.error('Unable to remove setting');
          $state.go('index.admin.settings');
        }
    );
  }
});
