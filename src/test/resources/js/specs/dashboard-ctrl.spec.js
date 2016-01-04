describe('the dashboard-ctrl test', function () {
    beforeEach(module('OpenDashboard'));

    describe('the AddDashboardController tests', function () {
        var controller,
                contextMappingService,
                DataService,
                scope,
                mockContextMapping,
                Dashboard;

        describe('AddDashboardController.save', function () {
            //However you do it the mockContextMapping needs to be reset between tests.  I completely recreate here probably should just reset mockContextMapping.dashboards=null;
            beforeEach(inject(function ($controller, $rootScope, ContextMappingService, $state) {
                scope = $rootScope.$new();
                contextMappingService = ContextMappingService;
                state = $state;
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
                    Dashboard = dashboard;
                };
                controller = $controller('AddDashboardController', {$scope: scope, $state: state, ContextMappingService: contextMappingService, contextMapping: mockContextMapping});
            }));


            it('should use the ContextMappingService.addDashboard to update dashboards', function () {
                spyOn(contextMappingService, 'addDashboard').and.callThrough();
                scope.save();
                expect(contextMappingService.addDashboard).toHaveBeenCalled();
                expect(contextMappingService.addDashboard.calls.allArgs()).toEqual([[scope.contextMapping, scope.dashboard]]);
            });

            it('should add a new dashboard to the dashboards', function () {
                spyOn(contextMappingService, 'addDashboard').and.callThrough();
                expect(scope.contextMapping.dashboards).toBe(null);
                scope.save();
                expect(scope.contextMapping.dashboards).not.toBe(null);
            });

            it('should change scope dashboard to the newly created dashboard', function () {
                spyOn(contextMappingService, 'addDashboard').and.callThrough();
                expect(scope.dashboard).toEqual({});
                scope.save();
                expect(scope.dashboard).not.toEqual({});
                expect(scope.contextMapping.dashboards).not.toBe(scope.dashboard);
            });

            it('should call state.go with expect parameters ', function () {
                spyOn(contextMappingService, 'addDashboard').and.callThrough();
                spyOn(state, 'go');
                scope.save();
                expect(state.go).toHaveBeenCalled;
                //expect(state.go).toHaveBeenCalledWith('index.dashboard',{cmid:mockContextMapping.id, dbid:Dashboard.id});
                //TODO figure out how to get the correct parameters.
            });
        });

        describe('AddDashboardController.onDashboardTitleAdd', function () {

            var form = {name: "addDashboardForm", role: "form", novalidate: "novalidate"};
            var field = "test2";
            var model = "test3";
            var dashboardForm;

            beforeEach(inject(function ($controller, $rootScope, dataService, $state) {
                scope = $rootScope.$new();
                DataService = dataService;
                state = $state;
                scope[form] = {name: "addDashboardForm", role: "form", novalidate: "novalidate"};
                dashboardForm = scope[form];
                dashboardForm[field] = {$valid: true, $invalid: false};
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
                    Dashboard = dashboard;
                };
                controller = $controller('AddDashboardController', {$scope: scope, $state: state, dataService: DataService, contextMapping: mockContextMapping, dashboardForm: dashboardForm});
            }));

            it('should use the dataService.checkUniqueValue to update dashboard titles', function () {
                spyOn(DataService, 'checkUniqueValue').and.callThrough();
                scope.onDashboardTitleAdd(form, field, model);
                expect(DataService.checkUniqueValue).toHaveBeenCalled();
                expect(DataService.checkUniqueValue.calls.allArgs()).toEqual([[null, "test2", "test3"]]);
            });

        });
    });

    describe('the DashboardController tests', function () {
        var controller,
                scope,
                mockContextMapping,
                registry,
                mockCard;
        describe('DashboardController.showEditLink', function () {
            beforeEach(inject(function ($controller, $rootScope, $state) {
                scope = $rootScope.$new();
                var DashboardId = "mockId";
                state = $state;
                registry = {title: 'Demo Card', description: 'This card demonstrates how to retrieve data from various sources.',
                    cardType: 'demo', styleClasses: 'od-card col-xs-12', config: [{field: 'sample', fieldName: 'Example field', fieldType: 'text',
                            required: true, translatableLabelKey: 'LABEL_EXAMPLE_FIELD'}], requires: [], uses: ['ROSTER', 'OUTCOME', 'ASSIGNMENT',
                        'FORUM', 'COURSE'], registry: 'mockRegistry'};
                scope.cards = registry.registry;

                mockContextMapping = {
                    id: "mockId",
                    key: "mockKey",
                    dashboards: [{id: 'mockId'}],
                    context: 'mockContext',
                    modified: null
                };
                mockCard = {
                    title: "mockTitle",
                    description: "mockDescription",
                    imgUrl: "test.com/test",
                    cardType: "ASSESSMENT",
                    styleClasses: "mockStyle",
                    config: [{'id': 'mockId'}]
                };
                scope.$parent.contextMapping = mockContextMapping;

                controller = $controller('DashboardController', {$scope: scope, registry: registry, contextMapping: mockContextMapping, dashboardId: DashboardId});
            }));


            it('should use the scope.showEditLink to update dashboards', function () {
                expect(scope.showEditLink(mockCard)).toBe(true);
            });

        });
    });

    describe('the ErrorController tests', function () {
        var controller,
                scope;
        describe('DashboardController.showEditLink', function () {
            beforeEach(inject(function ($controller, $rootScope) {
                scope = $rootScope.$new();
                var errorMessage = "This is an error message";
                controller = $controller('ErrorController', {$scope: scope, errorCode: errorMessage});
            }));


            it('ErrorController should return the error message', function () {
                expect(scope.errorCode).toBe('This is an error message');
            });

        });
    });
});