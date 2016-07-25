describe('od.cards registry initialization', function () {
  
  var scope,
      reg;

  beforeEach(module('OpenDashboard'));

  beforeEach(inject(function($rootScope, registry){
    scope = $rootScope;
    reg = registry.registry;
  }));
  
  //This will automatically check new cards added to the registry and make sure they have all the required data.
  it('test init of every card in registry', function() {
    angular.forEach(reg, function(card, cardType){
      expect(card).not.toBe(null);
      expect(cardType).not.toBe(null);
      expect(card.title).not.toBe(null);
      expect(card.styleClasses).not.toBe(null);
      expect(card.config).not.toBe(null);
      expect(card.requires).not.toBe(null);

      expect(cardType).toBeDefined();
      expect(card).toBeDefined();
      expect(card.title).toBeDefined();
      expect(card.styleClasses).toBeDefined();
      expect(card.config).toBeDefined();
      expect(card.requires).toBeDefined();
    });
  });
    
});
