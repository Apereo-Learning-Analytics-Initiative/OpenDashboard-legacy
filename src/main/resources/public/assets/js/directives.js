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
(function(angular) {
	'use strict';

	angular
			.module('OpenDashboard')
			.directive(
					'loadingWidget',
					function($timeout, requestNotification) {
						return {
							restrict : "AC",
							link : function(scope, element) {
								// hide the element initially
								element.hide();

								var promise = null;

								// subscribe to listen when a request starts
								requestNotification
										.subscribeOnRequestStarted(function() {
											// show the spinner after a delay to
											// avoid screen flashing
											if (!promise) {
												promise = $timeout(function() {
													element.show();
												}, 500);
											}
										});

								requestNotification
										.subscribeOnRequestEnded(function() {
											// hide the spinner if there are no
											// more pending requests
											if (requestNotification
													.getRequestCount() === 0) {
												if (promise) {
													$timeout.cancel(promise);
													promise = null;
												}
												element.hide();
											}
										});
							}
						};
					})
				.directive('titleUnique', function(DashboardService) {
				      return {
				           restrict: 'A',
				           require: 'ngModel',
				           link: function (scope, element, attrs, ngModel) {
				                element.bind('blur', function (e) {
				                     //ngModel.$loading = true;
				                     DashboardService
				                     .checkTitle(element.val())
				                     .then(function(exists){
				                       //ngModel.$loading = false;
				                       ngModel.$setValidity('unique', !exists);
				                     });
				                });
				           }
				      };
				})
				.directive('bubbleCloud', function() {

				return {

					restrict : 'E',

					scope : {

						// Either an array of data objects (with groups, labels,
						// and
						// values),
						// or a dictionary of groups of data objects (with
						// labels and
						// values)
						data : '=',

						// Set to 'true' to automatically update when data does
						watch : '@',

						// The attribute containing a data object's value
						valueAttr : '@',

						// The attribute containing a data object's label
						// (optional)
						labelAttr : '@',

						// The attribute containing a data object's group
						// (required if data is an array)
						groupAttr : '@',

						// A function which takes a group name and returns the
						// desired
						// fill color. Optional. The default is
						// d3.scale.category20c()
						fillColorFn : '@',

						// A function which takes a group name and returns the
						// desired
						// label color. Optional. The default is black.
						labelColorFn : '@',

						// A function which takes a data object and returns the
						// tooltip
						// text. The default is to combine the label and the
						// value.
						tooltipFormatFn : '@',

						// Overall diameter of the chart, in pixels
						diameter : '@',

						// A function name to publish into the parent scope, to
						// allow
						// reloading the chart (optional)
						renderChartFn : '=?'

					},

					link : function(scope, element, attrs, ctrl) {

						// Set size of element
						element.css('height', scope.diameter + 'px');

						// Publish renderChart into the parent scope
						scope.renderChartFn = ctrl.renderChart;

						// Watch the data, if desired
						if (scope.watch === 'true') {
							scope.$watch('data', ctrl.renderChart, true);
						}

						// Set up the controller
						ctrl.init(element.find('svg'));

						// Render the chart
						ctrl.renderChart();
					},

					template : '<svg id="mychart"></svg>',

					controller : 'chartController'

				};

			})

			.controller(
					'chartController',
					function($scope) {

						// Return a flattened array of objects of this form:
						//
						// { group: 'Rock', name: 'Rolling Stones, The', value:
						// 12 }
						//
						function get_flattened_data() {

							var data = $scope.data;
							if (_.isArray(data)) {

								var groupAttr = $scope.groupAttr;
								if (!groupAttr)
									throw new Error(
											'group-attr is required on <bubble>');

								data = _.groupBy($scope.data, function(item) {
									return item[groupAttr];
								});
							}

							var flattened_data = [];

							_.forOwn(data, function(items,group) {
								_.forEach(items, function(item) {
									flattened_data.push({
										group : group,
										object : item
									});
								});

							});
							return flattened_data;

						}

						// Initialize the controller with the given SVG element
						// (wrapped in an array)
						this.init = function(svg_element) {

							var valueAttr = $scope.valueAttr;
							if (!valueAttr)
								throw new Error(
										'value-attr is required on <bubble>');

							var diameter = parseInt($scope.diameter);

							svg_element.attr('width', diameter).attr('height',
									diameter).attr('class', 'bubble');

							$scope.selection = d3.selectAll(svg_element);

							// Create a pack layout
							$scope.pack_layout = d3.layout.pack().sort(null)
									.value(function(datum) {
										return datum.object[valueAttr];
									}).size([ diameter, diameter ])
									.padding(1.5);

							if ($scope.fillColorFn) {
								var fillColorFn = $scope.$parent[$scope.fillColorFn];
								if (!_.isFunction(fillColorFn))
									throw new Error(
											'fill-color-fn attr must be a function in the parent scope');
								$scope.fill_color_fn = fillColorFn;
							} else {
								$scope.fill_color_fn = d3.scale.category20c();
							}

							if ($scope.labelColorFn) {
								var labelColorFn = $scope.$parent[$scope.labelColorFn];
								if (!_.isFunction(labelColorFn))
									throw new Error(
											'label-color-fn attr must be a function in the parent scope');
								$scope.label_color_fn = labelColorFn;
							} else {
								$scope.label_color_fn = function() {
									return 'black';
								};
							}

							if ($scope.tooltipFormatFn) {
								var tooltipFormatFn = $scope.$parent[$scope.tooltipFormatFn];
								if (!_.isFunction(tooltipFormatFn))
									throw new Error(
											'tool-tip-format-fn attr must be a function in the parent scope');
								$scope.tooltip_format_fn = tooltipFormatFn;
							}
						};

						// Get the latest data and render the chart
						this.renderChart = function() {

							// Get the latest data

							var data = {
								children : get_flattened_data()
							};

							// Lay out the data

							var node = $scope.selection.selectAll('.node')
									.data(
											$scope.pack_layout.nodes(data)
													.filter(function(d) {
														return !d.children;
													}));

							// Handle added nodes

							var enter = node.enter().append('g').attr('class',
									'node')
									.on('click',function(d){}); // TODO
							enter.append('title');
							enter.append('circle');
							enter.append('text').attr('dy', '.3em').style(
									'text-anchor', 'middle')

							// Handle each node

							var valueAttr = $scope.valueAttr;
							var labelAttr = $scope.labelAttr;
							var label_format_fn = $scope.label_format_fn;
							var fill_color_fn = $scope.fill_color_fn;
							var label_color_fn = $scope.label_color_fn;
							var tooltip_format_fn = $scope.tooltip_format_fn;

							node.attr('transform', function(datum) {
								return 'translate(' + datum.x + ',' + datum.y
										+ ')';
							});

							node
									.select('title')
									.text(
											function(datum) {
												if (tooltip_format_fn) {
													return tooltip_format_fn(datum);
												} else {
													return datum.object[labelAttr]
															+ ': '
															+ d3
																	.format(
																			',d')
																	(
																			datum.object[valueAttr]);
												}
											});

							node.select('circle').attr('r', function(datum) {
								return datum.r;
							}).style('fill', function(datum) {
								return fill_color_fn(datum.group);
							});

							node.select('text').style('fill', function(datum) {
								return label_color_fn(datum.group);
							}).text(
									function(datum) {
										var label = datum.object[labelAttr];
										return label ? label.substring(0,
												datum.r / 3) : '';
									});

							// Handle removed nodes

							node.exit().remove();
						};

					})

})(angular);