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

	debugger;
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

angular
.module('OpenDashboard')
.controller('PreconfigureDashboardsCtrl',

function PreconfigureDashboardsCtrl($scope, $state, $translate, $translatePartialLoader, _, preconfiguredDashboards) {
	$translatePartialLoader.addPart('framework');
	$translate.refresh();
	
	$scope.preconfiguredDashboards = preconfiguredDashboards;
});

angular
.module('OpenDashboard')
.controller('AddPreconfiguredDashboardCtrl',

function AddPreconfiguredDashboardCtrl($log, $scope, $state, $translate, $translatePartialLoader, _, DashboardService, Notification, registry) {
	$translatePartialLoader.addPart('framework');
	$translate.refresh();
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

function EditPreconfiguredDashboardCtrl($log, $scope, $state, $translate, $translatePartialLoader, _, DashboardService, Notification, registry, preconfiguredDashboard) {
	$translatePartialLoader.addPart('framework');
	$translate.refresh();
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

function RemovePreconfiguredDashboardCtrl($scope, $state, $translate, $translatePartialLoader, _, DashboardService, Notification, preconfiguredDashboard) {
	$translatePartialLoader.addPart('framework');
	$translate.refresh();
	
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