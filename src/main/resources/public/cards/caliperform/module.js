(function(angular) {
	angular
			.module('od.cards.caliperform', [ 'OpenDashboardRegistry' ])
			.config(function(registryProvider) {
				registryProvider.register('caliperform', {
					title : 'Submit Caliper',
					description : 'Form to submit Caliper Data.',
					cardType : 'caliperform',
					styleClasses : 'od-card col-xs-12',
					config : [],
					requires : [],
					uses : []
				});
			})

			.controller(
					'CaliperFormCardController',
					function($scope, $http, $state, $stateParams, $translate,
							$translatePartialLoader, $log, $q, _,
							OpenDashboard_API, ContextMappingService,
							SessionService, EventService, RosterService,
							ModelOutputDataService, CourseDataService) {

						$scope.go = function() {
							$scope.postAway();
						}

						$scope.postAway = function() {
							EventService.postCaliperEvent($scope.contextMapping.tenantId,
									$scope.getCaliperEnvelope()).then(
									function(data) {
										if ($scope.caliperId == null) {
											$scope.caliperId = data;
										} else {
											$scope.caliperId = $scope.caliperId
													+ "\n" + data;
										}
									});
						}

						$scope.resetForm = function() {							
							$scope.caliper_actor = null;
							$scope.caliper_action = null;
							$scope.caliper_object = null;
							$scope.caliper_application = null;
							$scope.caliperForm.$setPristine();
							$scope.caliperJSON = $scope.getCaliperEnvelope();
						};

						$scope.getCaliperJSON = function() {
							$scope.caliperJSON = $scope.getCaliperEnvelope();
						};

						$scope.caliperActorObjects = [ {
							caliperActorObjectId : "554433,TSoprano",
							name : "Tony Soprano"
						}, {
							caliperActorObjectId : "554432,JMelfi",
							name : "Jennifer Melfi"
						}, {
							caliperActorObjectId : "554431,CMoltisanti",
							name : "Christopher Moltisanti"
						}, {
							caliperActorObjectId : "554430,FGuinta",
							name : "Furio Giunta"
						} ];

						$scope.caliperActionObjects = [
								{
									caliperActionObjectId : "http://purl.imsglobal.org/vocab/caliper/v1/action#NavigatedTo",
									name : "Navigated To"
								},
								{
									caliperActionObjectId : "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedIn",
									name : "Logged In"
								},
								{
									caliperActionObjectId : "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedOut",
									name : "Logged out"
								},
								{
									caliperActionObjectId : "http://purl.imsglobal.org/vocab/caliper/v1/action#Viewed",
									name : "Viewed"
								} ];

						$scope.caliperEventObjects = [
								{
									caliperEventObjectId : "https://example.com/viewer/book/34843#epubcfi(/4/3),The Glorious Cause",
									name : "The Glorious Cause: The American Revolution, 1763-1789 (Oxford History of the United States)",
									version : "1st Edition"
								},
								{
									caliperEventObjectId : "https://example.com/viewer/book/28888#epubcfi,Founding Brothers:",
									name : "Founding Brothers: The Revolutionary Generation",
									version : "3rd Edition"
								},
								{
									caliperEventObjectId : "https://example.com/viewer/book/67933#epubcfi(/17/a),Washington: A Life",
									name : "Washington: A Life",
									version : "2nd Edition"
								} ];

						$scope.caliperApplicationObjects = [
								{
									caliperApplicationObjectId : "https://example.com/viewer#123,ePub",
									name : "ePub"
								},
								{
									caliperApplicationObjectId : "https://example.com/textSim#,TextbookSim",
									name : "Textbook Simulator"
								} ];


						$scope.getCaliperEnvelope = function() {

							// The Actor for the Caliper Event
							var actor;
							if ($scope.caliper_actor != null
									&& $scope.caliper_actor != "") {
								actor = new Caliper.Entities.Person(
										$scope.caliper_actor);
							} else {
								actor = new Caliper.Entities.Person(
										"ACTOR_NOT_SET");
							}
							actor.setDateCreated((new Date(
									"2015-08-01T06:00:00Z")).toISOString());
							actor.setDateModified((new Date(
									"2015-09-02T11:30:00Z")).toISOString());

							var action;
							if ($scope.caliper_action != null) {
								// The Action for the Caliper Event
								action = $scope.caliper_action;
							}

							// The Object being interacted with by the Actor
							// var eventObj = new
							// Caliper.EPubVolume("https://example.com/viewer/book/34843#epubcfi(/4/3)");
							var eventObj;
							if ($scope.caliper_object != null) {
								eventObj = new Caliper.Entities.EPubVolume(
										$scope.caliper_object);
								eventObj.setName($scope.caliper_object);
								eventObj.setVersion("2nd ed.");
								eventObj.setDateCreated((new Date(
										"2015-08-01T06:00:00Z")).toISOString());
								eventObj.setDateModified((new Date(
										"2015-09-02T11:30:00Z")).toISOString());
							}

							// The edApp that is part of the Learning Context
							// var edApp = new
							// Caliper.SoftwareApplication("https://example.com/viewer");
							var edApp;
							if ($scope.caliper_application != null) {
								edApp = new Caliper.Entities.SoftwareApplication(
										$scope.caliper_application);
								edApp.setName("ePub");
								edApp.setDateCreated((new Date(
										"2015-08-01T06:00:00Z")).toISOString());
								edApp.setDateModified((new Date(
										"2015-09-02T11:30:00Z")).toISOString());
							}

							/*
							 * // LIS Course Offering //var courseOffering = new
							 * Caliper.CourseOffering("https://example.edu/politicalScience/2015/american-revolution-101");
							 * var courseOffering = new
							 * Caliper.Entities.CourseOffering("https://example.edu/politicalScience/2015/american-revolution-101");
							 * courseOffering.setName("Political Science 101:
							 * The American Revolution");
							 * courseOffering.setCourseNumber("POL101");
							 * courseOffering.setAcademicSession("Fall-2015");
							 * courseOffering.setSubOrganizationOf(null);
							 * courseOffering.setDateCreated((new
							 * Date("2015-08-01T06:00:00Z")).toISOString());
							 * courseOffering.setDateModified((new
							 * Date("2015-09-02T11:30:00Z")).toISOString());
							 *  // LIS Course Section //var courseSection = new
							 * Caliper.CourseSection("https://example.edu/politicalScience/2015/american-revolution-101/section/001");
							 * var courseSection = new
							 * Caliper.Entities.CourseSection("https://example.edu/politicalScience/2015/american-revolution-101/section/001");
							 * courseSection.setName("American Revolution 101");
							 * courseSection.setCourseNumber("POL101");
							 * courseSection.setAcademicSession("Fall-2015");
							 * courseSection.setSubOrganizationOf(courseOffering);
							 * courseSection.setDateCreated((new
							 * Date("2015-08-01T06:00:00Z")).toISOString());
							 * courseSection.setDateModified((new
							 * Date("2015-09-02T11:30:00Z")).toISOString());
							 */

							// Event
							var event = new Caliper.Events.Event();
							if ($scope.caliper_actor != null) {
								event.setActor(actor);
							}
							if ($scope.caliper_action != null) {
								event.setAction(action);
							}
							if ($scope.caliper_object != null) {
								event.setObject(eventObj);
							}
							if ($scope.caliper_application != null) {
								event.setEdApp(edApp);
							}
							event.setFederatedSession("https://example.edu/lms/federatedSession/123456789");

							var currentTimeMillis = (new Date()).getTime().toISOString;

							// Send the Event
							var envelope = new Caliper.Request.Envelope();
							envelope.setSensor("https://example.edu/sensor/001");
							envelope.setSendTime(currentTimeMillis);
							envelope.setData(event);

							// envelope['_id'] = 'caliper-js' +
							// currentTimeMillis;
							// envelope.d = event;
							console.log('created event envelope %O', envelope);

							return envelope;							
						}
					})
})(angular);