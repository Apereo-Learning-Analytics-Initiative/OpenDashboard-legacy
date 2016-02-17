/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
(function(angular, Math, moment) {
'use strict';
    
angular
.module('od.cards.activity', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('activity',{
        title: 'Activity',
        description: '',
        imgUrl: '',
        cardType: 'activity',
        styleClasses: 'od-card col-xs-12',
	    config: [],
	    requires: ['ROSTER'],
	    uses: ['EVENT','MODELOUTPUT']
    });
 })
 .controller('ActivityController', function($scope, $translate, $translatePartialLoader, $log, _, SessionService, EventService, RosterService) {
	 
//	$translatePartialLoader.addPart('roster-card');
//    $translate.refresh();
   
   $scope.events = [{"id":"1"},{"id":"2"},{"id":"3"}]
   
   $scope.refreshActivityStream = function() {
	   
   };
   
});

})(angular, Math, moment);
