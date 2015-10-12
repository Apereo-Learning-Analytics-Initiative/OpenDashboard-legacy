describe('the card-ctrl test', function () {
    beforeEach(module('OpenDashboard'));

    describe('the SelectCardController tests', function () {
        var controller,
                http,
                httpBackend,
                scope;
                
        beforeEach(inject(function (AddDashboardController, $http, $httpBackend, $rootScope, $q) {
            controller = AddDashboardController;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));
     /*   describe('initial setup of AddDashboardController', function () {
            it('should be defined', function () {
                expect(controller).toBeDefined();
            });
        });
        describe('AddDashboardController.save', function () {
            it('save should be defined', function () {
                expect(controller.save()).toBeCalled()
            });*/
        });
        
        describe('the AddCardController tests', function () {
        var controller,
                http,
                httpBackend,
                scope;
                
        beforeEach(inject(function (DashboardController, $http, $httpBackend, $rootScope, $q) {
            controller = DashboardController;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));
       /* describe('initial setup of DashboardController', function () {
            it('should be defined', function () {
                expect(controller).toBeDefined();
            });
        });
        describe('DashboardController.save', function () {
            it('save should be defined', function () {
                expect(controller.save()).toBeCalled()
            });
        });*/
    });
    
    describe('the EditCardController tests', function () {
        var controller,
                http,
                httpBackend,
                scope;
                
        beforeEach(inject(function (DashboardController, $http, $httpBackend, $rootScope, $q) {
            controller = DashboardController;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));
       /* describe('initial setup of DashboardController', function () {
            it('should be defined', function () {
                expect(controller).toBeDefined();
            });
        });
        describe('DashboardController.save', function () {
            it('save should be defined', function () {
                expect(controller.save()).toBeCalled()
            });
        });*/
    });
    
    describe('the RemoveCardController tests', function () {
        var controller,
                http,
                httpBackend,
                scope;
                
        beforeEach(inject(function (DashboardController, $http, $httpBackend, $rootScope, $q) {
            controller = DashboardController;
            http = $http;
            httpBackend = $httpBackend;
            scope = $rootScope.$new();
        }));
       /* describe('initial setup of DashboardController', function () {
            it('should be defined', function () {
                expect(controller).toBeDefined();
            });
        });
        describe('DashboardController.save', function () {
            it('save should be defined', function () {
                expect(controller.save()).toBeCalled()
            });
        });*/
    });
});