describe('the card-ctrl test', function () {
    beforeEach(module('OpenDashboard'));
    describe('the SelectCardController tests', function () {
        var controller,
                contextMappingService,
                registry,
                mockCard,
                scope,
                mockContextMapping,
                state,
                notification;

//        describe('SelectCardController.addCard', function () {
//            beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
//                scope = $rootScope.$new();
//                var DashboardId = "mockId";
//                contextMappingService = ContextMappingService;
//                state = $state;
//                registry = {title: 'Risk Assessment',
//                description: '',
//                imgUrl: '',
//                cardType: 'riskassessment',
//                styleClasses: 'od-card col-xs-12',
//        	    config: [],
//        	    requires: ['ROSTER', 'MODELOUTPUT'],
//        	    uses: [], registry: 'mockRegistry'};
//                scope.cards = registry.registry;
//
//                mockContextMapping = {
//                    id: "mockId",
//                    key: "mockKey",
//                    dashboards: [{id: 'mockId'}],
//                    context: 'mockContext',
//                    modified: null
//                };
//                mockCard = {
//                    title: "mockTitle",
//                    description: "mockDescription",
//                    imgUrl: "test.com/test",
//                    cardType: "riskassessment",
//                    styleClasses: "mockStyle",
//                    config: [{'id': 'mockId'}]
//
//                };
//
//                scope.$parent.contextMapping = mockContextMapping;
//                controller = $controller('SelectCardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
//                    dashboardId: DashboardId});
//            }));
//
//
//            it('should call the state.go function', function () {
//                spyOn(state, 'go').and.callThrough();
//                scope.addCard('riskassessment');
//                expect(state.go).toHaveBeenCalled();
//            });
//
//            it('should call the ContextMappingService.addCard function', function () {
//                spyOn(contextMappingService, 'addCard').and.callThrough();
//                scope.addCard('roster');
//                expect(contextMappingService.addCard).toHaveBeenCalled();
//            });
//        });

//        describe('SelectCardController.isConfigured', function () {
//            beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
//                scope = $rootScope.$new();
//                var DashboardId = "mockId";
//                contextMappingService = ContextMappingService;
//                state = $state;
//                registry = {title: 'Risk Asessment',
//                description: '',
//                imgUrl: '',
//                cardType: 'riskassessment',
//                styleClasses: 'od-card col-xs-12',
//        	    config: [],
//        	    requires: ['ROSTER', 'MODELOUTPUT'],
//        	    uses: [], registry: 'mockRegistry'};
//                scope.cards = registry.registry;
//
//                mockContextMapping = {
//                    id: "mockId",
//                    key: "mockKey",
//                    dashboards: [{id: 'mockId', cards: [{cardType: 'demo'}]}],
//                    context: 'mockContext',
//                    modified: null
//                };
//                mockCard = {
//                    title: "mockTitle",
//                    description: "mockDescription",
//                    imgUrl: "test.com/test",
//                    cardType: "riskassessment",
//                    styleClasses: "mockStyle",
//                    config: [{'id': 'mockId'}]
//                };
//
//                scope.$parent.contextMapping = mockContextMapping;
//                controller = $controller('SelectCardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
//                    dashboardId: DashboardId});
//            }));
//
//
//            it('scope.isConfigured should return true', function () {
//                expect(scope.isConfigured('riskassessment')).toBe(true);
//            });
//
//            it('scope.isConfigured should return false', function () {
//                expect(scope.isConfigured('roster')).toBe(false);
//            });
//        });

//        describe('SelectCardController.editCard', function () {
//            beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state, Notification) {
//                scope = $rootScope.$new();
//                var DashboardId = "mockId";
//                contextMappingService = ContextMappingService;
//                notification = Notification;
//                state = $state;
//                registry = {title: 'Risk Asessment',
//                description: '',
//                imgUrl: '',
//                cardType: 'riskassessment',
//                styleClasses: 'od-card col-xs-12',
//        	    config: [],
//        	    requires: ['ROSTER', 'MODELOUTPUT'],
//        	    uses: [], registry: 'mockRegistry'};
//                scope.cards = registry.registry;
//
//                mockContextMapping = {
//                    id: "mockId",
//                    key: "mockKey",
//                    dashboards: [{id: 'mockId', cards: [{cardType: 'demo'}]}],
//                    context: 'mockContext',
//                    modified: null
//                };
//                mockCard = {
//                    title: "mockTitle",
//                    description: "mockDescription",
//                    imgUrl: "test.com/test",
//                    cardType: "riskassessment",
//                    styleClasses: "mockStyle",
//                    config: [{'id': 'mockId'}]
//                };
//
//                scope.$parent.contextMapping = mockContextMapping;
//                controller = $controller('SelectCardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
//                    dashboardId: DashboardId, Notification: notification});
//            }));
//
//
//            it('state.go should be called for editCard', function () {
//                spyOn(state, 'go').and.callThrough();
//                scope.editCard('riskassessment');
//                expect(state.go).toHaveBeenCalled();
//            });
//
//            it('Notification.error should be called for editCard', function () {
//                spyOn(notification, 'error').and.callThrough();
//                scope.editCard('roster');
//                expect(notification.error).toHaveBeenCalled();
//            });
//        });
    });

    describe('AddCardController', function () {
        describe('the AddCardController tests', function () {
            var controller,
                    contextMappingService,
                    registry,
                    scope,
                    mockContextMapping,
                    state;

            describe('AddCardController.cancel', function () {
                beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
                    scope = $rootScope.$new();
                    var DashboardId = "mockId";
                    contextMappingService = ContextMappingService;
                    state = $state;
                    var mockCard = {
                        title: "mockTitle",
                        description: "mockDescription",
                        imgUrl: "test.com/test",
                        cardType: "riskassessment",
                        styleClasses: "mockStyle",
                        config: [{'id': 'mockId'}]
                    };
                    registry = {title: 'Risk Asessment',
                    description: '',
                    imgUrl: '',
                    cardType: 'riskassessment',
                    styleClasses: 'od-card col-xs-12',
            	    config: [],
            	    requires: ['ROSTER', 'MODELOUTPUT'],
            	    uses: [], registry: 'mockRegistry'};
                    scope.cards = registry.registry;
                    scope.card = mockCard;

                    mockContextMapping = {
                        id: "mockId",
                        key: "mockKey",
                        dashboards: [{id: 'mockId', cards: [{cardType: 'demo'}]}],
                        context: 'mockContext',
                        modified: null
                    };


                    scope.$parent.contextMapping = mockContextMapping;
                    controller = $controller('AddCardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
                        dashboardId: DashboardId, card: mockCard});
                }));


                it('state.go should be called for cancel', function () {
                    spyOn(state, 'go').and.callThrough();
                    scope.cancel();
                    expect(state.go).toHaveBeenCalled();
                });
            });

            describe('AddCardController.addCard', function () {
                beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
                    scope = $rootScope.$new();
                    var DashboardId = "mockId";
                    contextMappingService = ContextMappingService;
                    state = $state;
                    var mockCard = {
                        title: "mockTitle",
                        description: "mockDescription",
                        imgUrl: "test.com/test",
                        cardType: "riskassessment",
                        styleClasses: "mockStyle",
                        config: [{'id': 'mockId'}]
                    };
                    registry = {title: 'Risk Asessment',
                    description: '',
                    imgUrl: '',
                    cardType: 'riskassessment',
                    styleClasses: 'od-card col-xs-12',
            	    config: [],
            	    requires: ['ROSTER', 'MODELOUTPUT'],
            	    uses: [], registry: 'mockRegistry'};
                    scope.cards = registry.registry;
                    scope.card = mockCard;

                    mockContextMapping = {
                        id: "mockId",
                        key: "mockKey",
                        dashboards: [{id: 'mockId', cards: [{cardType: 'demo'}]}],
                        context: 'mockContext',
                        modified: null
                    };
                    scope.$parent.contextMapping = mockContextMapping;
                    controller = $controller('AddCardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
                        dashboardId: DashboardId, card: mockCard});
                }));


                it('state.go should be called for addCard', function () {
                    spyOn(contextMappingService, 'addCard').and.callThrough();
                    scope.addCard();
                    expect(contextMappingService.addCard).toHaveBeenCalled();
                });
            });
        });
    });

    describe('EditCardController', function () {
        describe('the EditCardController tests', function () {
            var controller,
                    contextMappingService,
                    registry,
                    scope,
                    mockContextMapping,
                    mockCard,
                    state;

            describe('EditCardController.cancel', function () {
                beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
                    scope = $rootScope.$new();
                    var DashboardId = "mockId";
                    contextMappingService = ContextMappingService;
                    state = $state;
                    mockCard = {
                        title: "mockTitle",
                        description: "mockDescription",
                        imgUrl: "test.com/test",
                        cardType: "riskassessment",
                        styleClasses: "mockStyle",
                        config: [{'id': 'mockId'}]
                    };
                    registry = {title: 'Risk Asessment',
                    description: '',
                    imgUrl: '',
                    cardType: 'riskassessment',
                    styleClasses: 'od-card col-xs-12',
            	    config: [],
            	    requires: ['ROSTER', 'MODELOUTPUT'],
            	    uses: [], registry: [{id: 'demo', cardType: 'demo', config: [{id: 'mockId'}]}]};
                    scope.cards = registry.registry;
                    scope.$parent.activeDashboard = mockCard;
                    registry.registry['demo'] = {config: 'mockConfig'};

                    mockContextMapping = {
                        id: "mockId",
                        key: "mockKey",
                        dashboards: [{id: 'mockId', cards: [{id: 'mockId', cardType: 'demo'}]}],
                        context: 'mockContext',
                        modified: null
                    };


                    scope.$parent.contextMapping = mockContextMapping;
                    controller = $controller('EditCardController', {$scope: scope, $state: state, registry: registry, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
                        dashboardId: DashboardId, cardId: 'mockId'});
                }));


                it('state.go should be called for cancel', function () {
                    spyOn(state, 'go').and.callThrough();
                    scope.cancel();
                    expect(state.go).toHaveBeenCalled();
                });
            });

//            describe('EditCardController.editCard', function () {
//                beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
//                    scope = $rootScope.$new();
//                    var DashboardId = "mockId";
//                    contextMappingService = ContextMappingService;
//                    state = $state;
//                    mockCard = {
//                        title: "mockTitle",
//                        description: "mockDescription",
//                        imgUrl: "test.com/test",
//                        cardType: "riskassessment",
//                        styleClasses: "mockStyle",
//                        config: [{'id': 'mockId'}]
//                    };
//                    registry = {title: 'Risk Asessment',
//                    description: '',
//                    imgUrl: '',
//                    cardType: 'riskassessment',
//                    styleClasses: 'od-card col-xs-12',
//            	    config: [],
//            	    requires: ['ROSTER', 'MODELOUTPUT'],
//            	    uses: [], registry: [{id: 'demo', cardType: 'riskassessment', config: [{id: 'mockId'}]}]};
//                    scope.cards = registry.registry;
//                    scope.$parent.activeDashboard = mockCard;
//                    registry.registry['riskassessment'] = {config: 'mockConfig'};
//
//                    mockContextMapping = {
//                        id: "mockId",
//                        key: "mockKey",
//                        dashboards: [{id: 'mockId', cards: [{id: 'mockId', cardType: 'demo'}]}],
//                        context: 'mockContext',
//                        modified: null
//                    };
//
//
//                    scope.$parent.contextMapping = mockContextMapping;
//                    controller = $controller('EditCardController', {$scope: scope, $state: state, registry: registry, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
//                        dashboardId: DashboardId, cardId: 'mockId'});
//                }));
//
//
//                it('state.go should be called for editCard', function () {
//                    spyOn(contextMappingService, 'update').and.callThrough();
//                    scope.editCard();
//                    expect(contextMappingService.update).toHaveBeenCalled();
//                });
//            });
        });
    });
    
    describe('RemoveCardController', function () {
        describe('the RemoveCardController tests', function () {
            var controller,
                    contextMappingService,
                    registry,
                    scope,
                    mockContextMapping,
                    mockCard,
                    state;

            describe('RemoveCardController.removeCard', function () {
                beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
                    scope = $rootScope.$new();
                    var DashboardId = "mockId";
                    contextMappingService = ContextMappingService;
                    state = $state;
                    mockCard = {
                        title: "mockTitle",
                        description: "mockDescription",
                        imgUrl: "test.com/test",
                        cardType: "riskassessment",
                        styleClasses: "mockStyle",
                        config: [{'id': 'mockId'}]
                    };
                    registry = {title: 'Risk Asessment',
                    description: '',
                    imgUrl: '',
                    cardType: 'riskassessment',
                    styleClasses: 'od-card col-xs-12',
            	    config: [],
            	    requires: ['ROSTER', 'MODELOUTPUT'],
            	    uses: [], registry: [{id: 'demo', cardType: 'demo', config: [{id: 'mockId'}]}]};
                    scope.cards = registry.registry;
                    scope.$parent.activeDashboard = mockCard;
                    registry.registry['riskassessment'] = {config: 'mockConfig'};

                    mockContextMapping = {
                        id: "mockId",
                        key: "mockKey",
                        dashboards: [{id: 'mockId', cards: [{id: 'mockId', cardType: 'riskassessment'}]}],
                        context: 'mockContext',
                        modified: null
                    };


                    scope.$parent.contextMapping = mockContextMapping;
                    controller = $controller('EditCardController', {$scope: scope, $state: state, registry: registry, ContextMappingService: contextMappingService, contextMapping: mockContextMapping,
                        dashboardId: DashboardId, cardId: 'mockId'});
                }));


                it('state.go should be called for cancel', function () {
                    spyOn(state, 'go').and.callThrough();
                    scope.cancel();
                    expect(state.go).toHaveBeenCalled();
                });
            });
        });
    });
});