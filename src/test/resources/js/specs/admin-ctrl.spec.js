//describe('the admin-ctrl test', function () {
//  
//    //TODO all of the other ctrls not included below
//    beforeEach(module('OpenDashboard'));
//    
//    describe('the SettingsCtrl tests', function () {
//        var controllerWithSettings,
//            controllerWithNoSettings,
//            controllerWithEmptySettings,
//            scope,
//            childScope,
//            grandChildScope,
//            state,
//            lodash,
//            settings;
//        
//            beforeEach(inject(function ($controller, $rootScope, $state, _) {
//                scope = $rootScope.$new();
//                childScope = scope.$new();
//                grandchildScope = childScope.$new();
//                state = $state;
//                lodash = _;
//                settings = [{id: '1', key: 'testKey1', value: 'testValue1'},
//                           {id: '2', key: 'testKey2', value: 'testValue2'},
//                           {id: '3', key: 'testKey3', value: 'testValue3'}];
//                controllerWithNoSettings = $controller('SettingsCtrl', {$scope: scope, $state: state, _: lodash, configuredSettings: settings});
//                controllerWithEmptySettings = $controller('SettingsCtrl', {$scope: childScope, $state: state, _: lodash, configuredSettings: []});
//                controllerWithEmptySettings = $controller('SettingsCtrl', {$scope: grandchildScope, $state: state, _: lodash, configuredSettings: null});
//            }));
//            
//            it('should set $scope.configureSetting to configuredSettings', function(){
//              expect(scope.configuredSettings).toBe(settings);
//            });
//
//            it('should set $scope.isConfigured to true when configuredSettings is not empty', function(){
//              expect(scope.isConfigured).toBe(true);
//            });
//            
//            it('should set $scope.isConfigured to true when configuredSettings is empty', function(){
//              expect(childScope.isConfigured).toBe(false);
//            });
//            
//            it('should set $scope.isConfigured to true when configuredSettings is null', function(){
//              expect(childScope.isConfigured).toBe(false);
//            });
//    });
//    
//    describe('the AddSettingsCtrl tests', function () {
//      var controller,
//          scope,
//          state,
//          setting,
//          notification,
//          settingService;
//      
//          beforeEach(inject(function ($controller, $rootScope, $state, SettingService, Notification) {
//              scope = $rootScope.$new();
//              state = $state;
//              settingService = SettingService;
//              notification = Notification;
//              scope.setting = setting;
//              controller = $controller('AddSettingsCtrl', {$scope: scope, $state: state, SettingService: settingService, Notification: notification});
//          }));
//          
//          it('should define $scope.save', function(){
//            expect(scope.save).toBeDefined();
//          });
//
//          describe('$scope.save function tests', function (){
//            it('should call settingService.createSetting with $scope.setting', function(){
//              spyOn(settingService, 'createSetting').and.callThrough();
//              scope.save();
//              expect(settingService.createSetting).toHaveBeenCalledWith(scope.setting);
//            });
//          });
//    });
//    
//    describe('the EditSettingsCtrl tests', function () {
//      var controller,
//      scope,
//      state,
//      setting,
//      notification,
//      settingService;
//      
//      beforeEach(inject(function ($controller, $rootScope, $state, SettingService, Notification) {
//        scope = $rootScope.$new();
//        state = $state;
//        settingService = SettingService;
//        notification = Notification;
//        settings = [{id: '1', key: 'testKey1', value: 'testValue1'},
//                    {id: '2', key: 'testKey2', value: 'testValue2'},
//                    {id: '3', key: 'testKey3', value: 'testValue3'}];
//        scope.setting = setting;
//        controller = $controller('EditSettingsCtrl', {$scope: scope, $state: state, configuredSettings: settings, SettingService: settingService, Notification: notification});
//      }));
//      
//      it('should define $scope.configuredSettings', function(){
//        expect(scope.configuredSettings).toBeDefined();
//      });
//      
//      it('should set configuredSettings to test settings', function(){
//        expect(scope.configuredSettings).toEqual(settings);
//      });
//      
//      describe('$scope.save function tests', function (){
//
//        it('should be defined defined', function(){
//          expect(scope.save).toBeDefined();
//        });
//
//        it('should call settingService.updateSettings with $scope.configuredSettings', function(){
//          spyOn(settingService, 'updateSettings').and.callThrough();
//          scope.save();
//          expect(settingService.updateSettings).toHaveBeenCalledWith(scope.configuredSettings);
//        });
//      });
//    });
//    
//    describe('the RemoveSettingsCtrl tests', function () {
//      var controller,
//      scope,
//      state,
//      setting,
//      notification,
//      settingService;
//      
//      beforeEach(inject(function ($controller, $rootScope, $state, SettingService, Notification) {
//        scope = $rootScope.$new();
//        state = $state;
//        settingService = SettingService;
//        notification = Notification;
//        settings = [{id: '1', key: 'testKey1', value: 'testValue1'},
//                    {id: '2', key: 'testKey2', value: 'testValue2'},
//                    {id: '3', key: 'testKey3', value: 'testValue3'}];
//        scope.setting = setting;
//        controller = $controller('RemoveSettingsCtrl', {$scope: scope, $state: state, configuredSettings: settings, SettingService: settingService, Notification: notification});
//      }));
//      
//      it('should define $scope.configuredSettings', function(){
//        expect(scope.configuredSettings).toBeDefined();
//      });
//      
//      it('should set configuredSettings to test settings', function(){
//        expect(scope.configuredSettings).toEqual(settings);
//      });
//      
//      describe('$scope.remove function tests', function (){
//        
//        it('should be defined', function(){
//          expect(scope.remove).toBeDefined();
//        });
//        
//        it('should call settingService.removeSetting with setting id', function(){
//          spyOn(settingService, 'removeSetting').and.callThrough();
//          scope.remove(settings[0]);
//          expect(settingService.removeSetting).toHaveBeenCalledWith('1');
//        });
//      });
//    });
//});
