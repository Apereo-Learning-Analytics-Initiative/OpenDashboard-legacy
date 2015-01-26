describe('the LtiProxyService', function() {
    beforeEach(module('od.cards.lti'));
     
    var ltiProxyService, $httpBackend, $rootScope;
    
    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
      });
    
    beforeEach(inject(function ($injector) {
        // Actual Service
        ltiProxyService = $injector.get( 'LtiProxyService' );
        // Set up the mock http service responses
        $httpBackend = $injector.get('$httpBackend');
        // Required to force async stuff to finish
        $rootScope = $injector.get('$rootScope');
        // Our call's mocked response
        $httpBackend.when('POST', '/api/contextMappingId/lti/launch/cardId').respond({proxiedLaunch:"test"});
        }));

        it("the LtiProxyService", function (done) {
        var contextMappingId = 'contextMappingId';
        var cardId = 'cardId';
        var url = '/api/contextMappingId/lti/launch/cardId';
        expect(ltiProxyService).toBeDefined();      
        $httpBackend.expectPOST(url);

        var promise = ltiProxyService.post(contextMappingId, cardId, "");
        promise.then(function(resp) {
            expect(resp).toEqual({proxiedLaunch:"test"});    
        });
        // The implementation returns a promise(async response)
        // this method forces the return of data instead of just accepting the promise object
        $rootScope.$digest();

        $httpBackend.flush();
        });

    });