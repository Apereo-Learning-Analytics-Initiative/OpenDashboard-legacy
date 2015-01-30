(function() {
'use strict';
    
angular
.module('od.cards.roster', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('roster',{
        title: 'Roster View',
        description: 'Use this card to view your roster',
        imgUrl: '',
        cardType: 'roster',
        styleClasses: 'od-card col-xs-12',
        config: []
    });
 });
})();
