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
  
  it('activityradar card should be defined with appropriate values', function() {
    var cardType = 'activityradar';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);
    
    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });
  
  it('demo card should be defined with appropriate values', function() {
      var cardType = 'demo';
      var card = reg[cardType];
      expect(card).not.toBe(null);
      expect(card.title).not.toBe(null);
      expect(card.styleClasses).not.toBe(null);
      expect(card.config).not.toBe(null);
      expect(card.requires).not.toBe(null);

      expect(card).toBeDefined();
      expect(card.title).toBeDefined();
      expect(card.styleClasses).toBeDefined();
      expect(card.config).toBeDefined();
      expect(card.requires).toBeDefined();
  });
  
  it('eventviewer card should be defined with appropriate values', function() {
    var cardType = 'eventviewer';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);

    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });

  it('lti card should be defined with appropriate values', function() {
    var cardType = 'lti';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);
    
    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });
  
  it('modelviewer card should be defined with appropriate values', function() {
    var cardType = 'modelviewer';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);
    
    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });
  
  it('roster card should be defined with appropriate values', function() {
    var cardType = 'roster';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);

    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });
  
  it('snapp card should be defined with appropriate values', function() {
    var cardType = 'snapp';
    var card = reg[cardType];
    expect(card).not.toBe(null);
    expect(card.title).not.toBe(null);
    expect(card.styleClasses).not.toBe(null);
    expect(card.config).not.toBe(null);
    expect(card.requires).not.toBe(null);

    expect(card).toBeDefined();
    expect(card.title).toBeDefined();
    expect(card.styleClasses).toBeDefined();
    expect(card.config).toBeDefined();
    expect(card.requires).toBeDefined();
  });
});
