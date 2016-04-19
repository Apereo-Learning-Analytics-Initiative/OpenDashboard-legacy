describe('the data-services test', function () {
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

    describe('the ProviderService tests', function () {
        var service,
                http,
                httpBackend,
                scope;
 
        beforeEach(inject(function (ProviderService, $http, $httpBackend, $rootScope, $q) {
            service = ProviderService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));



        describe('initial setup of ProviderService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('ProviderService.getProviderTypes', function () {
            var prov2 = {'type': 'COURSE', 'key': 'LABEL_COURSE_PROVIDERS_KEY', 'desc': 'LABEL_COURSE_PROVIDERS_DESC'};
            var prov3 = {'type': 'EVENT', 'key': 'LABEL_EVENT_PROVIDERS_KEY', 'desc': 'LABEL_EVENT_PROVIDERS_DESC'};
            var prov5 = {'type': 'MODELOUTPUT', 'key': 'LABEL_MODELOUTPUT_PROVIDERS_KEY', 'desc': 'LABEL_MODELOUTPUT_PROVIDERS_DESC'};
            var prov7 = {'type': 'ROSTER', 'key': 'LABEL_ROSTER_PROVIDERS_KEY', 'desc': 'LABEL_ROSTER_PROVIDERS_DESC'};
            var providerTypes = [prov2, prov3, prov5, prov7];

            it('getProviderTypes should be defined', function () {
                expect(service.getProviderTypes()).toBeDefined()
            });
            it('should return a guid like value', function () {
                expect(service.getProviderTypes()).toEqual(providerTypes);
            });
        });

    });

    describe("the CourseDataService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

        beforeEach(inject(function (CourseDataService, $http, $httpBackend, $rootScope, $q) {
            service = CourseDataService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of CourseDataService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('CourseDataService.getContexts', function () {
            it('should not be equal to the CourseDataService when key does not match in response', function () {
                httpBackend.when('POST', '/api/context').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContexts(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the CourseDataService when type does not match in response', function () {
                httpBackend.when('POST', '/api/context').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContexts(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the CourseDataService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/context').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getContexts(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the CourseDataService when everything does match in response', function () {
                httpBackend.when('POST', '/api/context').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContexts(prov1)
                        .then(function (response) {
                            expect(response).toEqual([prov1.type, prov1.key, prov1.desc]);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('CourseDataService.getContext', function () {
            it('should not be equal to the CourseDataService when key does not match in response', function () {
                httpBackend.when('POST', '/api/context/123').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContext(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the CourseDataService when type does not match in response', function () {
                httpBackend.when('POST', '/api/context/123').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContext(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the CourseDataService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/context/123').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getContext(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the CourseDataService when everything does match in response', function () {
                httpBackend.when('POST', '/api/context/123').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getContext(prov1, '123')
                        .then(function (response) {
                            expect(response).toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });
    });

    describe("the EventService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'};

        beforeEach(inject(function (EventService, $http, $httpBackend, $rootScope, $q) {
            service = EventService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of EventService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('EventService.getEventsForCourse', function () {
            it('should not be equal to the EventService when content does not match in response with page', function () {
                httpBackend.when('POST', '/api/event/course/123?page=1&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourse(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=0&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourse(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=1&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourse(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=0&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourse(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page', function () {
                httpBackend.when('POST', '/api/event/course/123?page=1&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourse(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=0&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourse(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=1&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourse(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/course/123?page=0&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourse(prov1, '123')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('EventService.getEventsForUser', function () {
            it('should not be equal to the EventService when content does not match in response with page', function () {
                httpBackend.when('POST', '/api/event/user/123?page=1&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForUser(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=0&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForUser(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=1&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForUser(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=0&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForUser(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page', function () {
                httpBackend.when('POST', '/api/event/user/123?page=1&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForUser(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=0&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForUser(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=1&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForUser(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/user/123?page=0&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForUser(prov1, '123')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('EventService.getEventsForCourseAndUser', function () {
            it('should not be equal to the EventService when content does not match in response with page', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=1&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '1')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=0&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=1&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '1', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the EventService when content does not match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=0&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getEventsForCourseAndUser(prov1, '123', '124')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=1&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '1')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=0&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response with page and size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=1&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourseAndUser(prov1, '123', '124', '1', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the EventService when everything does match in response without page or size', function () {
                httpBackend.when('POST', '/api/event/course/123/user/124?page=0&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getEventsForCourseAndUser(prov1, '123', '124')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });
    });

    describe("the ModelOutputDataService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'};

        beforeEach(inject(function (ModelOutputDataService, $http, $httpBackend, $rootScope, $q) {
            service = ModelOutputDataService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of ModelOutputDataService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('ModelOutputDataService.getModelOutputForCourse', function () {
            it('should not be equal to the ModelOutputDataService when content does not match in response with page', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=1&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForCourse(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response with size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=0&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForCourse(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response with page and size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=1&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForCourse(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response without page or size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=0&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForCourse(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with page', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=1&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForCourse(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=0&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForCourse(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with page and size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=1&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForCourse(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response without page or size', function () {
                httpBackend.when('POST', '/api/modeloutput/course/123?page=0&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForCourse(prov1, '123')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('ModelOutputDataService.getModelOutputForUser', function () {
            it('should not be equal to the ModelOutputDataService when content does not match in response with page', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=1&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForUser(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response with size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=0&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForUser(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response with page and size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=1&size=5').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForUser(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ModelOutputDataService when content does not match in response without page or size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=0&size=10').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL', 'content': 'TEST_CONTENT1'});

                service.getModelOutputForUser(prov1, '123')
                        .then(function (response) {
                            expect(response).not.toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with page', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=1&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForUser(prov1, '123', '1')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=0&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForUser(prov1, '123', '', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response with page and size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=1&size=5').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForUser(prov1, '123', '1', '5')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ModelOutputDataService when everything does match in response without page or size', function () {
                httpBackend.when('POST', '/api/modeloutput/user/123?page=0&size=10').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC', 'content': 'TEST_CONTENT'});

                service.getModelOutputForUser(prov1, '123')
                        .then(function (response) {
                            expect(response).toBe(prov1.content);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

    });

    describe("the RosterService tests", function () {
        var service,
                http,
                httpBackend,
                scope,
                openDashboardApi;
        var member = ({'user_id': '123', 'user_image': '123', 'role': 'a', 'roles': 'ab', 'person': 'test', 'events': '1',
            'relative_activity_level': '0', 'risk': '0', 'last_activity': '10/11/1900', 'fromLTI': 'no', 'fromService': 'yes', 'isInstructor': 'no'},
        {'user_id': '124', 'user_image': '124', 'role': 'b', 'roles': 'ab', 'person': 'test2', 'events': '2',
            'relative_activity_level': '1', 'risk': '1', 'last_activity': '10/11/1900', 'fromLTI': 'no', 'fromService': 'yes', 'isInstructor': 'no'},
        {'user_id': '125', 'user_image': '125', 'role': 'c', 'roles': 'ab', 'person': 'test3', 'events': '3',
            'relative_activity_level': '2', 'risk': '2', 'last_activity': '10/11/1900', 'fromLTI': 'yes', 'fromService': 'no', 'isInstructor': 'yes'});

        beforeEach(inject(function (RosterService, $http, $httpBackend, $rootScope, $q, OpenDashboard_API) {
            service = RosterService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
            openDashboardApi = OpenDashboard_API;
        }));

        describe('initial setup of RosterService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('RosterService.getRoster', function () {

            it('should not be equal to the RosterService when key does not match in response', function () {
                httpBackend.when('POST', '/api/roster').respond(member);

                service.getRoster(member)
                        .then(function (response) {
                            expect(response).not.toEqual(member);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the RosterService when type does not match in response', function () {
                httpBackend.when('POST', '/api/roster').respond(member);

                service.getRoster(member)
                        .then(function (response) {
                            expect(response).not.toEqual(member);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the RosterService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/roster').respond(member);

                service.getRoster(member)
                        .then(function (response) {
                            expect(response).not.toEqual(member);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });
    });

    describe('the dataService tests', function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

        beforeEach(inject(function (dataService, $http, $httpBackend, $rootScope, $q) {
            service = dataService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of dataService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('dataService.checkUniqueValue', function () {

            it('should be equal to the dataService when everything does match in response', function () {
                spyOn(service, 'checkUniqueValue');
                var dashboard = {id: null};
                service.checkUniqueValue("Dashboard", "field", "value");

                expect(service.checkUniqueValue).toHaveBeenCalledWith("Dashboard", "field", "value");
            });
        });
    });
});