describe('the framework-services test', function () {
    beforeEach(module('OpenDashboard', function ($provide, $translateProvider) {
        $provide.factory('customLoader', function ($q) {
            return function () {
                var deferred = $q.defer();
                deferred.resolve({});
                return deferred.promise;
            };
        });

        $translateProvider.useLoader('customLoader');
    }));

    describe('the UUIDService tests', function () {
        var service;

        beforeEach(inject(function (UUIDService) {
            service = UUIDService;
        }));

        describe('initial setup of UUIDService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('initial values of UUIDService', function () {
            it('generate should be undefined', function () {
                expect(!service.generate()).toBeDefined()
            });
            it('should return a guid like value', function () {
                expect(service.generate()).toContain('-');
            });
        });
    });

    describe('the SessionService tests', function () {

        //Notice the scope of service
        var service,
                http,
                openDashboardApi,
                httpBackend,
                scope;

        //Any thing you need to do before or after is declared first
        beforeEach(inject(function (SessionService, OpenDashboard_API, $http, $httpBackend, $rootScope) {
            service = SessionService;
            openDashboardApi = OpenDashboard_API;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        //First test should always prove that you have the service your testing
        describe('intial setup of SessionService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        //Second test the default settings
        describe('intial values of SessionService', function () {
            it('isAuthenticated should be false', function () {
                expect(service.isAuthenticated()).toBe(false);
            });
            it('isLtiSession should be false', function () {
                expect(service.isLTISession()).toBe(false);
            });
            it('hasAdminRole should be false', function () {
                expect(service.hasAdminRole()).toBe(false);
            });
            it('hasInstructorRole should be false', function () {
                expect(service.hasInstructorRole()).toBe(false);
            });
            it('hasStudentRole should be false', function () {
                expect(service.hasStudentRole()).toBe(false);
            });
            it('getCourse should be undefined', function () {
                expect(!service.getCourse()).toBeDefined();
            });
            it('getInbound_LTI_Launch should be undefined', function () {
                expect(!service.getInbound_LTI_Launch()).toBeDefined();
            });
            it('getCurrentUser should be undefined', function () {
                expect(!service.getCurrentUser()).toBeDefined();
            });
        });

        describe('authenticate', function () {
            it('should return true when response is authenticated and has name', function () {
                httpBackend.when('GET', '/user').respond({"authenticated": true,
                    "name": "admin", "principal" : {"tenantId" : "12345"}
                });
                service.authenticate();
                httpBackend.flush();
                expect(service.isAuthenticated()).toBe(true);
            });

            it('should return false when response is not authenticated and has name', function () {
                httpBackend.when('GET', '/user').respond({"authenticated": false,
                    "name": "admin"
                });
                service.authenticate();
                httpBackend.flush();
                expect(service.isAuthenticated()).toBe(false);

            });

            it('should return false when response is authenticated and has no name', function () {
                httpBackend.when('GET', '/user').respond({"authenticated": true,
                    "name": ""
                });
                service.authenticate();
                httpBackend.flush();
                expect(service.isAuthenticated()).toBe(false);

            });

            it('should return false when response is empty', function () {
                httpBackend.when('GET', '/user').respond({});
                service.authenticate();
                httpBackend.flush();
                expect(service.isAuthenticated()).toBe(false);
            });
        });

        describe('getCourse will use OpenDasboard_API', function () {
            it('should call getCourse', function () {
                spyOn(openDashboardApi, 'getCourse');
                service.getCourse();
                expect(openDashboardApi.getCourse).toHaveBeenCalled();
            });
        });

        describe('getInbound_LTI_Launch will use OpenDasboard_API', function () {
            it('should call getInbound_LTI_Launch', function () {
                spyOn(openDashboardApi, 'getInbound_LTI_Launch');
                service.getInbound_LTI_Launch();
                expect(openDashboardApi.getInbound_LTI_Launch).toHaveBeenCalled();
            });
        });

        describe('getCurrentUser will use OpenDasboard_API', function () {
            it('should call getCurrentUser', function () {
                spyOn(openDashboardApi, 'getCurrentUser');
                service.getCurrentUser();
                expect(openDashboardApi.getCurrentUser).toHaveBeenCalled();
            });
        });
    });

    describe('the FeatureFlagService tests', function () {

        //Notice the scope of service
        var service,
                http,
                httpBackend,
                scope;

        //Any thing you need to do before or after is declared first
        beforeEach(inject(function (FeatureFlagService, $http, $httpBackend, $rootScope, $q) {
            service = FeatureFlagService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();

        }));

        //First test should always prove that you have the service your testing
        describe('intial setup of FeatureFlagService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('FeatureFlagService.isFeatureActive', function () {
            it('should return false when key does not match in response', function () {
                httpBackend.when('GET', '/features/testKey').respond({'testKey': 'false'});

                service.isFeatureActive('testKey')
                        .then(function (response) {
                            expect(response).toBe(false);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should return true when key matches in response', function () {
                httpBackend.when('GET', '/features/testKey').respond({'testKey': 'true'});

                service.isFeatureActive('testKey')
                        .then(function (response) {
                            expect(response).toBe(true);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should return false when response gives an error', function () {
                httpBackend.when('GET', '/features/testKey').respond(500, {'testKey': 'true'});

                service.isFeatureActive('testKey')
                        .then(function (response) {
                            expect(response).toBe(false);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

//            it('should return undefined when response doesnt exist', function () {
//                httpBackend.when('GET', '/features/testKey').respond({'notCorrectKey': 'true'});
//
//                service.isFeatureActive('testKey')
//                        .then(function (response) {
//                            expect(response).toBeUndefined();
//                        },
//                                function (error) {
//                                    expect(error).toBeUndefined();
//                                });
//                httpBackend.flush();
//            })
        });
    });

    describe('the DashboardService tests', function () {
        var service;
        var contextMappingId = 0;

        beforeEach(inject(function (DashboardService) {
            service = DashboardService;
        }));

        describe('initial setup of DashboardService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('initial values of DashboardService', function () {
            it('should be defined', function () {
                expect(service.getContextMappingById(contextMappingId)).toBeDefined();
            });
        });
    });

    describe('the ContextMappingService tests', function () {
        //Notice the scope of service
        var service,
                http,
                httpBackend,
                scope,
                UUIdService,
                mockContextMapping;
        var contextMapping = {'key': 'contextKey'};
        var contextMapValue = {'key': 'contextKey', 'context': 'contextValue'};
        var contextMapId = {'id': '123'};

        //Any thing you need to do before or after is declared first
        beforeEach(inject(function (ContextMappingService, $http, $httpBackend, $rootScope, $q, UUIDService) {
            service = ContextMappingService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
            UUIdService = UUIDService;

            spyOn(UUIdService, 'generate').and.callThrough();

            mockContextMapping = {
                id: "mockId",
                key: "mockKey",
                dashboards: null,
                context: 'mockContext',
                modified: null
            };

            mockContextMapping.addDashboard = function addDashboard(dashboard) {
                if (!this.dashboards) {
                    this.dashboards = [];
                }
                this.dashboards.push(dashboard);
            };
        }));

        //First test should always prove that you have the service your testing
        describe('intial setup of ContextMappingService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('ContextMappingService.update', function () {
            it('should not be equal to the contextMap when key does not match in response', function () {
                httpBackend.when('PUT', '/api/consumer/context').respond({'contextMappingValue': 'false', 'context': 'contextValue'});

                service.update(contextMapValue)
                        .then(function (response) {
                            expect(response).not.toBe(contextMapValue);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
            it('should not be equal to the contextMap when key value does not match in response', function () {
                httpBackend.when('PUT', '/api/consumer/context').respond({'key': 'false', 'context': 'contextValue'});

                service.update(contextMapValue)
                        .then(function (response) {
                            expect(response).not.toBe(contextMapValue);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
            it('should not be equal to the contextMap when context does not match in response', function () {
                httpBackend.when('PUT', '/api/consumer/context').respond({'key': 'contextKey', 'contextValue': 'contextValue'});

                service.update(contextMapValue)
                        .then(function (response) {
                            expect(response).not.toBe(contextMapValue);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
            it('should not be equal to the contextMap when context value does not match in response', function () {
                httpBackend.when('PUT', '/api/consumer/context').respond({'key': 'contextKey', 'context': 'contextValue1'});

                service.update(contextMapValue)
                        .then(function (response) {
                            expect(response).not.toBe(contextMapValue);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
            it('should return the contextMap value when key matches response', function () {
                httpBackend.when('PUT', '/api/consumer/context').respond({'key': 'contextKey', 'context': 'contextValue'});

                service.update(contextMapValue)
                        .then(function (response) {
                            expect(response).toEqual(contextMapValue);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('ContextMappingService.addDashboard', function () {
            it('should set dashboard.id when give a defined context mapping', function () {
                var dashboard = {id: null};
                expect(dashboard.id).toBeNull();
                service.addDashboard(mockContextMapping, dashboard);
                expect(dashboard.id).not.toBeNull();
            });

            it('should not set dashboard.id when give an undefined context mapping', function () {
                var dashboard = {id: null};
                expect(dashboard.id).toBeNull();
                service.addDashboard(undefined, dashboard);
                expect(dashboard.id).toBeNull();
            });

            it('should not set dashboard.id when give a null value context mapping', function () {
                var dashboard = {id: null};
                expect(dashboard.id).toBeNull();
                service.addDashboard(null, dashboard);
                expect(dashboard.id).toBeNull();
            });

            //2
            it('should call UUIDService.generate when given valid context mapping', function () {
                var dashboard = {id: null};
                expect(UUIdService.generate).not.toHaveBeenCalled();
                service.addDashboard(mockContextMapping, dashboard);
                expect(UUIdService.generate).toHaveBeenCalled();
            });

            it('should not call UUIDService.generate when given an undefined context mapping', function () {
                var dashboard = {id: null};
                expect(UUIdService.generate).not.toHaveBeenCalled();
                service.addDashboard(undefined, dashboard);
                expect(UUIdService.generate).not.toHaveBeenCalled();
            });

            it('should not call UUIDService.generate when given null value context mapping', function () {
                var dashboard = {id: null};
                expect(UUIdService.generate).not.toHaveBeenCalled();
                service.addDashboard(null, dashboard);
                expect(UUIdService.generate).not.toHaveBeenCalled();
            });

            it('should add a dashboard with valid id to contextMapping.dashboards', function () {
                var dashboard = {id: null};
                service.addDashboard(mockContextMapping, dashboard);
                expect(dashboard).not.toBeNull();
                expect(mockContextMapping.dashboards).not.toBeNull();
                expect(mockContextMapping.dashboards).toEqual([dashboard]);
            });

            it('should call the ContextMappingService.update with an updated contextMapping', function () {
                spyOn(service, 'update');
                var dashboard = {id: null};
                service.addDashboard(mockContextMapping, dashboard);

                expect(service.update).toHaveBeenCalledWith(mockContextMapping);
            });

            it('should return expected response from ContextMappingService.update', function () {
                var testResponse = {"testValue": true, "mockUser": "admin"};
                var dashboard = {id: null};

                spyOn(service, 'update').and.callThrough();
                httpBackend.when('PUT', '/api/consumer/context')
                        .respond({"testValue": true,
                            "mockUser": "admin"
                        });
                service.addDashboard(mockContextMapping, dashboard).then(function (response) {
                    expect(response).toEqual(testResponse);
                },
                        function (error) {
                            expect(response).toBeUndefined();
                        });
                httpBackend.flush();
            });
        });

//        describe('ContextMappingService.getWithKeyAndContext', function () {
//            it('should not be equal to the contextMap when key does not match in response', function () {
//                httpBackend.when('GET', '/api/consumer/Key/context/context').respond({'contextMappingValue': 'false', 'context': 'contextValue'});
//
//                service.get("Key", "context")
//                        .then(function (response) {
//                            expect(response).not.toEqual(jasmine.objectContaining(contextMapValue));
//                        },
//                                function (error) {
//                                    expect(error).toBeUndefined();
//                                });
//                httpBackend.flush();
//            });
//            it('should not be equal to the contextMap when key does not match in response', function () {
//                httpBackend.when('GET', '/api/consumer/contextKey/context/contextValue').respond({'key': 'contextKey', 'context': 'contextValue'});
//
//                service.get("contextKey", "contextValue")
//                        .then(function (response) {
//                            expect(response).toEqual(jasmine.objectContaining(contextMapValue));
//                        },
//                                function (error) {
//                                    expect(error).toBeUndefined();
//                                });
//                httpBackend.flush();
//            });
//        });

//        describe('ContextMappingService.getById', function () {
//            it('should not be equal to the contextMap when key does not match in response', function () {
//                httpBackend.when('GET', '/api/cm/123').respond({'id': '124'});
//
//                service.getById("123")
//                        .then(function (response) {
//                            expect(response).not.toEqual(jasmine.objectContaining(contextMapId));
//                        },
//                                function (error) {
//                                    expect(error).toBeUndefined();
//                                });
//                httpBackend.flush();
//            });
//            it('should not be equal to the contextMap when key does not match in response', function () {
//                httpBackend.when('GET', '/api/cm/123').respond({'id': '123'});
//
//                service.getById("123")
//                        .then(function (response) {
//                            expect(response).toEqual(jasmine.objectContaining(contextMapId));
//                        },
//                                function (error) {
//                                    expect(error).toBeUndefined();
//                                });
//                httpBackend.flush();
//            });
//        });
    });
});
