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
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

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
            var prov4 = {'type': 'FORUM', 'key': 'LABEL_FORUM_PROVIDERS_KEY', 'desc': 'LABEL_FORUM_PROVIDERS_DESC'};
            var prov5 = {'type': 'MODELOUTPUT', 'key': 'LABEL_MODELOUTPUT_PROVIDERS_KEY', 'desc': 'LABEL_MODELOUTPUT_PROVIDERS_DESC'};
            var prov6 = {'type': 'OUTCOME', 'key': 'LABEL_OUTCOMES_PROVIDERS_KEY', 'desc': 'LABEL_OUTCOMES_PROVIDERS_DESC'};
            var prov7 = {'type': 'ROSTER', 'key': 'LABEL_ROSTER_PROVIDERS_KEY', 'desc': 'LABEL_ROSTER_PROVIDERS_DESC'};
            var providerTypes = [prov1, prov2, prov3, prov4, prov5, prov6, prov7];

            it('getProviderTypes should be defined', function () {
                expect(service.getProviderTypes()).toBeDefined()
            });
            it('should return a guid like value', function () {
                expect(service.getProviderTypes()).toEqual(providerTypes);
            });
        });

        describe('ProviderService.getProviders', function () {
            it('should not be equal to the ProviderService when key does not match in response', function () {
                httpBackend.when('GET', '/api/providers/ASSIGNMENT').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});


                service.getProviders("ASSIGNMENT")
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ProviderService when type does not match in response', function () {
                httpBackend.when('GET', '/api/providers/ASSIGNMENT').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getProviders("ASSIGNMENT")
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ProviderService when desc does not match in response', function () {
                httpBackend.when('GET', '/api/providers/ASSIGNMENT').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getProviders("ASSIGNMENT")
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ProviderService when everything does match in response', function () {
                httpBackend.when('GET', '/api/providers/ASSIGNMENT').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getProviders("ASSIGNMENT")
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

    describe("the AssignmentService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

        beforeEach(inject(function (AssignmentService, $http, $httpBackend, $rootScope, $q) {
            service = AssignmentService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of AssignmentService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('AssignmentService.getAssignments', function () {
            it('should not be equal to the AssignmentService when key does not match in response', function () {
                httpBackend.when('POST', '/api/assignments').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getAssignments(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the AssignmentService when type does not match in response', function () {
                httpBackend.when('POST', '/api/assignments').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getAssignments(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the AssignmentService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/assignments').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getAssignments(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(jasmine.objectContaining(prov1));
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the AssignmentService when everything does match in response', function () {
                httpBackend.when('POST', '/api/assignments').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getAssignments(prov1)
                        .then(function (response) {
                            expect(response).toEqual([prov1.type, prov1.key, prov1.desc]);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
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

    describe("the ForumDataService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

        beforeEach(inject(function (ForumDataService, $http, $httpBackend, $rootScope, $q) {
            service = ForumDataService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of ForumDataService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('ForumDataService.getForums', function () {

            it('should not be equal to the ForumDataService when key does not match in response', function () {
                httpBackend.when('POST', '/api/forums').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getForums(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ForumDataService when type does not match in response', function () {
                httpBackend.when('POST', '/api/forums').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getForums(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ForumDataService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/forums').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getForums(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ForumDataService when everything does match in response', function () {
                httpBackend.when('POST', '/api/forums').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getForums(prov1)
                        .then(function (response) {
                            expect(response).toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });
        });

        describe('ForumDataService.getMessages', function () {

            it('should not be equal to the ForumDataService when key does not match in response', function () {
                httpBackend.when('POST', '/api/forums/topicHere/messages').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getMessages(prov1, "topicHere")
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ForumDataService when type does not match in response', function () {
                httpBackend.when('POST', '/api/forums/topicHere/messages').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getMessages(prov1, "topicHere")
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the ForumDataService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/forums/topicHere/messages').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getMessages(prov1, "topicHere")
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the ForumDataService when everything does match in response', function () {
                httpBackend.when('POST', '/api/forums/topicHere/messages').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getMessages(prov1, "topicHere")
                        .then(function (response) {
                            expect(response).toEqual(prov1);
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

    describe("the OutcomesService tests", function () {
        var service,
                http,
                httpBackend,
                scope;
        var prov1 = {'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'};

        beforeEach(inject(function (OutcomesService, $http, $httpBackend, $rootScope, $q) {
            service = OutcomesService;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));

        describe('initial setup of OutcomesService', function () {
            it('should be defined', function () {
                expect(service).toBeDefined();
            });
        });

        describe('OutcomesService.getOutcomesForCourse', function () {

            it('should not be equal to the OutcomesService when key does not match in response', function () {
                httpBackend.when('POST', '/api/outcomes').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY_FAIL', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getOutcomesForCourse(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the OutcomesService when type does not match in response', function () {
                httpBackend.when('POST', '/api/outcomes').respond({'type': 'ASSIGNMENTS',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getOutcomesForCourse(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should not be equal to the OutcomesService when desc does not match in response', function () {
                httpBackend.when('POST', '/api/outcomes').respond({'type': 'ASSIGNMENT',
                    'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY', 'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC_FAIL'});

                service.getOutcomesForCourse(prov1)
                        .then(function (response) {
                            expect(response).not.toEqual(prov1);
                        },
                                function (error) {
                                    expect(error).toBeUndefined();
                                });
                httpBackend.flush();
            });

            it('should be equal to the OutcomesService when everything does match in response', function () {
                httpBackend.when('POST', '/api/outcomes').respond({'type': 'ASSIGNMENT', 'key': 'LABEL_ASSIGNMENT_PROVIDERS_KEY',
                    'desc': 'LABEL_ASSIGNMENT_PROVIDERS_DESC'});

                service.getOutcomesForCourse(prov1)
                        .then(function (response) {
                            expect(response).toEqual(prov1);
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