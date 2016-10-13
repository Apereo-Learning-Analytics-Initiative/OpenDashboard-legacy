/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the Educational Community License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
(function(angular, JSON, Math) {
	'use strict';

	angular
			.module('OpenDashboard')
			.service(
					'UUIDService',
					function() {
						return {
							generate : function() {
								function _p8(s) {
									var p = (Math.random().toString(16) + "000000000")
											.substr(2, 8);
									return s ? "-" + p.substr(0, 4) + "-"
											+ p.substr(4, 4) : p;
								}
								return _p8() + _p8(true) + _p8(true) + _p8();
							}
						};
					})

			.service(
					'LocaleService',
					function($translate, LOCALES, $rootScope) {
						'use strict';
						// VARS
						var localesObj = LOCALES.locales;

						// locales and locales display names
						var _LOCALES = Object.keys(localesObj);
						if (!_LOCALES || _LOCALES.length === 0) {
							// console.error('There are no _LOCALES provided');
						}
						var _LOCALES_DISPLAY_NAMES = [];
						_LOCALES.forEach(function(locale) {
							_LOCALES_DISPLAY_NAMES.push(localesObj[locale]);
						});

						var currentLocale = $translate.proposedLanguage();// because
																			// of
																			// async
																			// loading
						// METHODS
						var checkLocaleIsValid = function(locale) {
							return _LOCALES.indexOf(locale) !== -1;
						};

						var setLocale = function(locale) {
							if (!checkLocaleIsValid(locale)) {
								// console.error('Locale name "' + locale + '"
								// is invalid');
								return;
							}
							startLoadingAnimation();
							currentLocale = locale;
							$translate.use(locale);
						};

						/**
						 * Stop application loading animation when translations
						 * are loaded
						 */
						var $html = angular.element('html');
						var LOADING_CLASS = 'app-loading';

						function startLoadingAnimation() {
							$html.addClass(LOADING_CLASS);
						}

						function stopLoadingAnimation() {
							$html.removeClass(LOADING_CLASS);
						}

						// EVENTS
						$rootScope.$on('$translateChangeSuccess', function(
								event, data) {
							document.documentElement.setAttribute('lang',
									data.language);// sets "lang" attribute to
													// html
						});

						$rootScope.$on('$localeChangeSuccess', function() {
							stopLoadingAnimation();
						});

						return {
							getLocaleDisplayName : function() {
								return localesObj[currentLocale];
							},
							setLocaleByDisplayName : function(localeDisplayName) {
								setLocale(_LOCALES[_LOCALES_DISPLAY_NAMES
										.indexOf(localeDisplayName)// get
																	// locale
																	// index
								]);
							},
							getLocalesDisplayNames : function() {
								return _LOCALES_DISPLAY_NAMES;
							},
							getLocaleForDisplayName : function(
									localeDisplayName) {
								return _LOCALES[_LOCALES_DISPLAY_NAMES
										.indexOf(localeDisplayName)];
							}
						};
					})

			.service(
					'SessionService',
					function($http, $window, OpenDashboard_API, _) {

						var ROLE_ADMIN = 'ROLE_ADMIN';
						var ROLE_INSTRUCTOR = 'ROLE_INSTRUCTOR';
						var ROLE_STUDENT = 'ROLE_STUDENT';

						var authenticated = false;
						var ltiSession = false;
						var authorities = null;
						var loggedOut = false;

						var checkRole = function(role) {
							var hasRole = false;
							if (authorities) {
								var values = _.map(authorities, function(
										authority) {
									return authority['authority'];
								});
								var indexOf = _.indexOf(values, role);
								if (indexOf >= 0) {
									hasRole = true;
								}
							}
							return hasRole;
						}

						return {
							isLoggedOut : function() {
								return loggedOut;
							},
							isAuthenticated : function() {
								return authenticated;
							},
							isLTISession : function() {
								return ltiSession;
							},
							hasAdminRole : function() {
								return checkRole(ROLE_ADMIN);
							},
							hasInstructorRole : function() {
								return checkRole(ROLE_INSTRUCTOR);
							},
							hasStudentRole : function() {
								return checkRole(ROLE_STUDENT);
							},
							authenticate : function(credentials) {

								var headers = {
									'Content-Type' : 'application/json'
								};

								if (credentials) {
									headers['Authorization'] = "Basic "
											+ btoa(credentials.username + ":"
													+ credentials.password);
									if (credentials.tenant) {
										headers['X-OD-TENANT'] = credentials.tenant;
									}
								}

								var promise = $http({
									method : 'GET',
									url : '/user',
									headers : headers
								})
										.then(
												function(response) {
													if (response
															&& response.data
															&& response.data.authenticated
															&& response.data.name) {
														loggedOut = false;
														authenticated = response.data.authenticated;
														authorities = response.data.authorities;
														if (response.data.launchRequest) {
															ltiSession = true;
    														var options = {};
    														options['roles'] = authorities;
    														options['user_id'] = response.data.name;
    														options['tenant_id'] = response.data.principal.tenantId;
    														OpenDashboard_API.setCurrentUser(options);

															OpenDashboard_API
																	.setInbound_LTI_Launch(response.data.launchRequest);
														}
														else {
														  var options = {};
														  options['roles'] = authorities;
														  options['user_id'] = response.data.name;
														  options['tenant_id'] = response.data.principal.tenantId;
														  OpenDashboard_API.setCurrentUser(options);
														}
														

														return authenticated;
													}
													return false;
												}, function(error) {
													return false;
												});
								return promise;
							},
							getCourse : function() {
								return OpenDashboard_API.getCourse();
							},
							getInbound_LTI_Launch : function() {
								return OpenDashboard_API
										.getInbound_LTI_Launch();
							},
							getCurrentUser : function() {
								return OpenDashboard_API.getCurrentUser();
							},
							logout : function() {
								var promise = $http({
									method : 'POST',
									url : '/logout',
									headers : {
										'Content-Type' : 'application/json'
									}
								}).then(function(response) {
									authenticated = false;
									ltiSession = false;
									authorities = null;
									
									if ($window.sessionStorage) {
									  $window.sessionStorage.removeItem('od_current_user');
									  $window.sessionStorage.removeItem('od_current_course');
									}

									return response.data;
								}, function(error) {
									return false;
								});
								return promise;
							}
						}
					})
			.service(
					'FeatureFlagService',
					function($http) {
						return {
							isFeatureActive : function(featureKey) {
								var headers = {
									'Content-Type' : 'application/json'
								};

								var promise = $http({
									method : 'GET',
									url : '/features/' + featureKey,
									headers : headers
								})
										.then(
												function(response) {
													if (response
															&& response.data) {
														var val = response.data[featureKey];
														// some weird handling
														// of booleans vs
														// strings
														if (val
																&& val.constructor == Boolean) {
															return val;
														} else if (val
																&& val.constructor == String) {
															return (val && val
																	.toLowerCase() === 'true');
														} else {
															return false;
														}
													}

													return false;
												}, function(error) {
													return false;
												});
								return promise;
							}
						}
					});
	angular.module('OpenDashboard').service('SettingService', function($http) {
		return {
			createSetting : function(setting) {
				var headers = {
					'Content-Type' : 'application/json'
				};
				var promise = $http({
					method : 'POST',
					url : '/api/setting',
					data : JSON.stringify(setting)
				}).then(function(response) {
					return response.data;
				});
				return promise;
			},
			updateSettings : function(settings) {
				var promise = $http({
					method : 'PUT',
					url : '/api/setting',
					data : JSON.stringify(settings),
					headers : {
						'Content-Type' : 'application/json'
					}
				}).then(function(response) {
					return response.data;
				});
				return promise;
			},
			getSettings : function() {
				var promise = $http({
					method : 'GET',
					url : '/api/setting'
				}).then(function(response) {
					if (response.data) {
						return response.data;
					} else {
						return null;
					}
				});
				return promise;
			},
			removeSetting : function(id) {
				var promise = $http({
					method : 'DELETE',
					url : '/api/setting/' + id,
					headers : {
						'Content-Type' : 'application/json'
					}
				}).then(function(response) {
					return response.data;
				});
				return promise;
			}
		}
	});
	angular.module('OpenDashboard').service(
			'TenantService',
			function($http) {
				return {
					createTenant : function(setting) {
						var headers = {
							'Content-Type' : 'application/json'
						};
						var promise = $http({
							method : 'POST',
							url : '/api/tenant',
							data : JSON.stringify(setting)
						}).then(function(response) {
							return response.data;
						});
						return promise;
					},
					updateTenant : function(tenant) {
						var promise = $http({
							method : 'PUT',
							url : '/api/tenant',
							data : JSON.stringify(tenant),
							headers : {
								'Content-Type' : 'application/json'
							}
						}).then(function(response) {
							return response.data;
						});
						return promise;
					},
					getTenantsMetadata : function() {
						var promise = $http({
							method : 'GET',
							url : '/tenant'
						}).then(function(response) {
							if (response.data) {
								return response.data;
							} else {
								return null;
							}
						});
						return promise;
					},
					getTenants : function() {
						var promise = $http({
							method : 'GET',
							url : '/api/tenant'
						}).then(function(response) {
							if (response.data) {
								return response.data;
							} else {
								return null;
							}
						});
						return promise;
					},
					getTenant : function(id) {
						var promise = $http({
							method : 'GET',
							url : '/api/tenant/' + id
						}).then(function(response) {
							if (response.data) {
								return response.data;
							} else {
								return null;
							}
						});
						return promise;
					},
					removeTenant : function(id) {
						var promise = $http({
							method : 'DELETE',
							url : '/api/tenant/' + id,
							headers : {
								'Content-Type' : 'application/json'
							}
						}).then(function(response) {
							return response.data;
						});
						return promise;
					},
					getProviderDataByTypeAndKey: function (id, type, key) {
						var promise = $http({
							method : 'GET',
							url : '/api/tenant/' + id
						}).then(function(response) {
							if (response.data) {
								var ret = null;
								var pd = response.data.providerData;
								if (pd) {
								  ret = _.find(pd,{ 'providerType': type, 'providerKey': key });
								}
								
								return ret;
							} else {
								return null;
							}
						});
						return promise;
					},
					getPreconfiguredDashboardById: function (id, dashboardId) {
						var promise = $http({
							method : 'GET',
							url : '/api/tenant/' + id
						}).then(function(response) {
							if (response.data) {
								var ret = null;
								var dbs = response.data.dashboards;
								if (dbs) {
								  ret = _.find(dbs,{ 'id': dashboardId });
								}
								
								return ret;
							} else {
								return null;
							}
						});
						return promise;
					}
					
//					getProviderDataByKey : function(id, type, key) {
//
//						var url = '/api/providerdata/' + type + '/' + key;
//						var promise = $http({
//							method : 'GET',
//							url : url,
//							headers : {
//								'Content-Type' : 'application/json'
//							}
//						}).then(
//								function(response) {
//									if (response && response.data) {
//										return response.data;
//									}
//									$log.debug('No provider data found for '
//											+ type + ' ' + key);
//									return null;
//								}, genericHandleError);
//						return promise;
//					},
//					getProviderDataByType : function(type) {
//
//						var url = '/api/providerdata/' + type;
//						var promise = $http({
//							method : 'GET',
//							url : url,
//							headers : {
//								'Content-Type' : 'application/json'
//							}
//						}).then(function(response) {
//							if (response && response.data) {
//								return response.data;
//							}
//							$log.debug('No provider data found for ' + type);
//							return null;
//						}, genericHandleError);
//						return promise;
//					},
//					create : function(providerData) {
//						var promise = $http({
//							method : 'POST',
//							url : '/api/providerdata',
//							data : JSON.stringify(providerData),
//							headers : {
//								'Content-Type' : 'application/json'
//							}
//						}).then(function(response) {
//							return response.data;
//						});
//						return promise;
//					},
//					update : function(providerData) {
//						var promise = $http(
//								{
//									method : 'PUT',
//									url : '/api/providerdata/'
//											+ providerData.type + '/'
//											+ providerData.key,
//									data : JSON.stringify(providerData),
//									headers : {
//										'Content-Type' : 'application/json'
//									}
//								}).then(function(response) {
//							return response.data;
//						});
//						return promise;
//					},
//					remove : function(providerData) {
//						var promise = $http(
//								{
//									method : 'DELETE',
//									url : '/api/providerdata/'
//											+ providerData.type + '/'
//											+ providerData.key,
//									data : JSON.stringify(providerData),
//									headers : {
//										'Content-Type' : 'application/json'
//									}
//								}).then(function(response) {
//							return response.data;
//						});
//						return promise;
//					}
				}
			});
	angular.module('OpenDashboard').service(
			'DashboardService',
			function($q, $http, ContextMappingService, UUIDService, _) {

				return {
					getContextMappingById : function(contextMappingId) {
						var deferred = $q.defer();

						ContextMappingService.getById(contextMappingId).then(
								function(contextMapping) {
									deferred.resolve(contextMapping);
								}, function(error) {
									deferred.reject();
								});
						return deferred.promise;
					},
					checkTitle : function(title) {
						var promise = $http({
							method : 'GET',
							url : '/api/preconfigure/checktitle/' + title
						}).then(function(response) {
							if (response.data) {
								return response.data.exists;
							} else {
								return false;
							}
						});
						return promise;
					}
				}
			});

	
angular
.module('OpenDashboard')
.service('ContextMappingService',
 function($http, UUIDService, OpenDashboard_API, _) {
  return {
	createWithTenantAndCourse : function(tenantId, courseId) {
      var data = {};
      data['tenantId'] = tenantId;
      data['courseId'] = courseId;
      var promise = $http(
        {
          method : 'POST',
          url : '/api/consumer/context',
          data : JSON.stringify(data),
          headers : {'Content-Type' : 'application/json'}
        }
      ).then(function(response) {
    	return response.data;
      });
      return promise;
	},
	update : function(contextMapping) {
	  var promise = $http(
	    {
	      method : 'PUT',
          url : '/api/consumer/context',
          data : JSON.stringify(contextMapping),
          headers : {'Content-Type' : 'application/json'}
	    }
	    ).then(function(response) {
	      return response.data;
		});
		return promise;
	},
	addDashboard : function(contextMapping, dashboard) {
	  if (contextMapping) {
	    dashboard.id = UUIDService.generate();
		contextMapping.addDashboard(dashboard);
		return this.update(contextMapping);
	  }
	},
	removeDashboard : function(contextMapping, dashboardId) {
	  if (contextMapping) {
	    contextMapping.dashboards = 
	      _.reject(contextMapping.dashboards, 
	    	  {'id' : dashboardId });
		return this.update(contextMapping);
	  }
	},
	addCard : function(contextMapping, dashboard, card) {
	  if (contextMapping && contextMapping.dashboards) {
	    card.id = UUIDService.generate();

		var db = _.find(
		  contextMapping.dashboards,
		  {'id' : dashboard.id});

		if (!db.cards) {
		  db.cards = [];
		}
		db.cards.push(card);

		return this.update(contextMapping);
	  }
	},
	removeCard : function(contextMapping, dashboard, card) {
	  if (contextMapping && contextMapping.dashboards) {

	    var db = _.find(
	      contextMapping.dashboards,
	      {'id' : dashboard.id});
		db.cards = _.reject(db.cards, 
		  {'id' : card.id});
		return this.update(contextMapping);
	  }
	},
	getWithTenantAndCourse : function(tenantId, courseId) {
	  var promise = $http(
	    {
	      method : 'GET',
	      url : '/api/tenant/' + tenantId + '/course/' + courseId,
	      headers : {'Content-Type' : 'application/json'}
		})
		.then(function(response) {
		  if (response.data) {
		    return response.data;
		  } else {
		    return null;
		  }
		});
	  return promise;
	},
	getWithKeyAndContext : function(key, context) {
	  var promise = $http(
	    {
	      method : 'GET',
	      url : '/api/consumer/' + key + '/context/' + context,
	      headers : {'Content-Type' : 'application/json'}
		})
		.then(function(response) {
		  if (response.data) {
		    return response.data;
		  } else {
		    return null;
		  }
		});
	  return promise;
	},
	getById : function(id) {
	  var promise = $http({
	    method : 'GET',
	    url : '/api/cm/' + id,
	    headers : {'Content-Type' : 'application/json'}
	  })
	  .then(function(response) {
	    if (response.data) {
	      return response.data;
		} else {
		  return null;
		}
	  });
	  return promise;
    }
  }
});

})(angular, JSON, Math);
