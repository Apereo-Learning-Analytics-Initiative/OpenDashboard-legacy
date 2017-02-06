/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the Educational Community License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
'use strict';

angular
.module('OpenDashboard')
.controller('TenantCtrl',

function TenantCtrl($scope, $state, Notification, TenantService, tenants) {
  $scope.tenants = tenants;
  $scope.tenant = {};
  
  $scope.save = function () {
    TenantService
    .createTenant($scope.tenant)
    .then(
      function (updatedContextMapping) {
    	Notification.success('Tenant created');
        $state.go('index.admin.tenants',{}, {reload: true});                    
      },
      function (error) {
        Notification.error('Unable to add tenant.');
      });
    };
  
  
})
.controller('EditTenantCtrl',

function EditTenantCtrl($scope, $state, Notification, TenantService, tenant) {
  $scope.tenant = tenant;
  
  $scope.save = function () {
    TenantService
    .updateTenant($scope.tenant)
    .then(
      function (updatedContextMapping) {
    	Notification.success('Tenant updated');
        $state.go('index.admin.tenants.tenant',{id:tenant.id}, {reload: true});                    
      },
      function (error) {
        Notification.error('Unable to update tenant.');
      });
    };
  
  
})
.controller('SelectTenantCtrl',

function SelectTenantCtrl($scope, $state, Notification, TenantService, tenant, providerTypes) {
  $scope.tenant = tenant;
  $scope.providerTypes = providerTypes;
  $scope.consumer = {};
  $scope.selectProviderType = function(providerType) {
    $state.go('index.admin.tenants.tenant.provider', {providerType:providerType});
  };
  
  	function createGuid()
	{
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
	}

  $scope.addConsumer = function () {
	
    if ($scope.consumer.name) {
        
      var consumer = {};
      
      if ($scope.consumer.id) {
        consumer['id'] = $scope.consumer.id;
      }
      else {
        consumer['id'] = createGuid();
      }
      
      consumer['name'] = $scope.consumer.name;
      consumer['oauthConsumerKey'] = createGuid();
      consumer['oauthConsumerSecret'] = createGuid();
      
      
      if (!$scope.tenant.consumers) {
    	 $scope.tenant.consumers = [];
      }
      $scope.tenant.consumers.push(consumer);
      
      TenantService.updateTenant($scope.tenant)
        .then(
          function (tenant) {
            Notification.success('Consumer created');
            $scope.consumer = {};
            $state.go('index.admin.tenants.tenant',{id:tenant.id}, {reload: true});                    
          },
          function (error) {
            Notification.error('Unable to add consumer.');
          });
     }
    	
  };
  
  $scope.removeConsumer = function(id,key) {
    if (id) {
      _.remove($scope.tenant.consumers,function(c) {return c.id === id;});
    }
    else {
      _.remove($scope.tenant.consumers,function(c) {return c.oauthConsumerKey === key;});
    }
    
   TenantService.updateTenant($scope.tenant)
    .then(
        function (tenant) {
          Notification.success('Consumer removed');
          $scope.consumer = {};
          $state.go('index.admin.tenants.tenant',{id:tenant.id}, {reload: true});                    
        },
        function (error) {
          Notification.error('Unable to remove consumer.');
        });
  };
  
  
});

