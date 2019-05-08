(function(angular) {
    'use strict';

    angular.module('OpenDashboard')
        .controller('pulseController', [
            '$scope',
            '$rootScope',
            '$http',
            '$q',
            '$timeout',
            '$state',
            'SessionService',
            'EnrollmentDataService',
            'UserDataService',
            'EventService',
            'LineItemDataService',
            'PulseApiService',
            function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, EnrollmentDataService, UserDataService, EventService, LineItemDataService, PulseApiService) {
                "use strict";
                
                var processedClasses = [];
                var classes = [];
                var students = [];
                var currentCourse = null;
                var currentUser = null;
                var currentStudent = null;

                currentUser = SessionService.getCurrentUser();

                $scope.chartInitialized = false;
                $scope.listType = 'classes';
                $scope.datalist = [];
                $scope.assignmentsWithoutDueDates = [];
                $scope.coursesMaxEvents = 0;
                $scope.maxEvents = 0;
                $scope.emailstudent = {};
                $scope.studentFilters = {};
                $scope.studentFilters.list = [];
                $scope.orderByField = 'label';
                $scope.reverseSort = false;
                $scope.assignmentOverlay = true;
                $scope.gradeFilter = false;
                $scope.gradeFilterScore = 25;
                $scope.submissionFilter = false;
                $scope.submissionFilterScore = 6;
                $scope.daysSinceLoginFilter = false;
                $scope.daysSinceLoginFilterCount = 0;
                $scope.maxGrade = 100;
                $scope.maxRisk = 100;
                $scope.maxActivity = 0;
                $scope.appHasGradeData = false;
                $scope.appHasMissingSubmissionData = false;
                $scope.riskOverlay = false;
                $scope.numberOfAtRiskStudents = 0;

                var currentDataList = [];

                var riskColorClasses = [{
                        'threshold': [0, .5],
                        'classname': 'high-risk'
                    },
                    {
                        'threshold': [.501, .7],
                        'classname': 'medium-risk'
                    },
                    {
                        'threshold': [.701, 1],
                        'classname': 'no-risk'
                    }
                ];

                $scope.activityOverlay = false;
                var activityTypeCount = 4;
                var activityColorClasses = [];

                function buildActivityColorThreshold() {
                    activityColorClasses = [{
                            'threshold': [$scope.currentCourse.studentEventTotalMax + 1, $scope.currentCourse.studentEventTotalMax / activityTypeCount * 3],
                            'classname': 'no-risk'
                        },
                        {
                            'threshold': [$scope.currentCourse.studentEventTotalMax / activityTypeCount * 3, $scope.currentCourse.studentEventTotalMax / activityTypeCount * 2],
                            'classname': 'medium-risk'
                        },
                        {
                            'threshold': [$scope.currentCourse.studentEventTotalMax / activityTypeCount * 2, $scope.currentCourse.studentEventTotalMax / activityTypeCount],
                            'classname': 'medium-risk'
                        },
                        {
                            'threshold': [$scope.currentCourse.studentEventTotalMax / activityTypeCount, 0],
                            'classname': 'high-risk'
                        }
                    ];
                }

                $scope.gradeOverlay = true;
                var gradeTypeCount = 5;
                var gradeColorClasses = [{
                        'threshold': [$scope.maxGrade + 1, ($scope.maxGrade / 2) / gradeTypeCount * 4 + ($scope.maxGrade / 2)],
                        'classname': 'high-grade'
                    },
                    {
                        'threshold': [($scope.maxGrade / 2) / gradeTypeCount * 4 + ($scope.maxGrade / 2), ($scope.maxGrade / 2) / gradeTypeCount * 3 + ($scope.maxGrade / 2)],
                        'classname': 'high-medium-grade'
                    },
                    {
                        'threshold': [($scope.maxGrade / 2) / gradeTypeCount * 3 + ($scope.maxGrade / 2), ($scope.maxGrade / 2) / gradeTypeCount * 2 + ($scope.maxGrade / 2)],
                        'classname': 'medium-grade'
                    },
                    {
                        'threshold': [($scope.maxGrade / 2) / gradeTypeCount * 2 + ($scope.maxGrade / 2), ($scope.maxGrade / 2) / gradeTypeCount + ($scope.maxGrade / 2)],
                        'classname': 'medium-low-grade'
                    },
                    {
                        'threshold': [($scope.maxGrade / 2) / gradeTypeCount + ($scope.maxGrade / 2), 0],
                        'classname': 'low-grade'
                    }
                ];

                $scope.emailList = [];

                $scope.colorCodeGrade = function(grade) {
                    if ($scope.gradeOverlay) {
                        var colorclass;
                        _.each(gradeColorClasses, function(g, i) {
                            if (grade < g.threshold[0] && grade >= g.threshold[1]) {
                                colorclass = g.classname;
                            }
                        });

                        return colorclass;
                    } else {
                        return "";
                    }
                }


                $scope.toggleAllActivityDetailOff = function() {
                    var elements = document.querySelectorAll('[id^=student-popup-]');

                    elements.forEach(function(element) {
                        element.classList.remove("ng-show");
                        element.classList.add("ng-hide");
                    });
                };

                $scope.toggleActivityDetailOn = function(id) {
                    $scope.toggleAllActivityDetailOff();
                    var element = document.getElementById(id);
                    element.classList.remove("ng-hide");
                    element.classList.add("ng-show");
                };

                $scope.toggleActivityDetailOff = function(id) {
                    var element = document.getElementById(id);
                    element.classList.remove("ng-show");
                    element.classList.add("ng-hide");
                };

                $scope.toggleActivityDetail = function(id) {
                    var element = document.getElementById(id);
                    if (element.classList.contains("ng-show")) {
                        element.classList.remove("ng-show");
                        element.classList.add("ng-hide");
                    } else {
                        element.classList.remove("ng-hide");
                        element.classList.add("ng-show");
                    }
                };


                function sumStudentsAtRisk() {
                    var sum = 0;
                    _.each($scope.datalist, function(r, i) {
                        if (r.risk <= riskColorClasses[1].threshold[1]) {
                            sum = sum + 1;
                        }
                    })
                    $scope.numberOfAtRiskStudents = sum;
                }



                $scope.colorCodeRiskForBubble = function(risk) {

                    var colorclass;

                    _.each(riskColorClasses, function(r, i) {
                        if (r.threshold[0] <= risk && risk <= r.threshold[1]) {
                            colorclass = r.classname;
                        }
                    });

                    if (colorclass == 'no-risk') {
                        return "rgba(127, 191, 63, 1)";
                    } else if (colorclass == 'medium-risk') {
                        return "rgba(246, 178, 8, 1)";
                    } else if (colorclass == 'high-risk') {
                        return "rgba(234, 14, 14, 1)";
                    } else {
                        return "";
                    }
                }



                $scope.colorCodeRisk = function(risk) {
                    if (true) {
                        var colorclass;
                        _.each(riskColorClasses, function(r, i) {
                            if (r.threshold[0] <= risk && risk <= r.threshold[1]) {
                                colorclass = r.classname;
                            }
                        });
                        return colorclass;
                    } else {
                        return "";
                    }
                }

                $scope.colorCodeActivity = function(activity) {
                    if ($scope.activityOverlay) {
                        var colorclass;
                        _.each(activityColorClasses, function(r, i) {
                            if (activity < r.threshold[0] && activity >= r.threshold[1]) {
                                colorclass = r.classname;
                            }
                        });
                        return colorclass;
                    } else {
                        return "";
                    }
                }

                function filterByGrade(nv) {
                    if ($scope.currentCourse) {
                        if ($scope.gradeFilter) {
                            var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;

                            currentDataList = _.filter(tempDataList, function(o) {
                                return o.grade < $scope.gradeFilterScore;
                            });
                        }
                    }
                }

                function filterByMissingSubmissions(nv, ov) {
                    if ($scope.currentCourse) {
                        if ($scope.submissionFilter) {
                            var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;

                            currentDataList = _.filter(tempDataList, function(o) {
                                return o.missingSubmission >= $scope.submissionFilterScore;
                            });
                        }
                    }
                }

                function filterLastLoginCount(nv, ov) {
                    if ($scope.currentCourse) {
                        if ($scope.daysSinceLoginFilter) {
                            var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;

                            currentDataList = _.filter(tempDataList, function(o) {
                                return o.daysSinceLogin >= $scope.daysSinceLoginFilterCount;
                            });
                        }
                    }
                }

                function runFilters() {
                    if ($scope.currentCourse) {
                        filterByGrade();
                        filterByMissingSubmissions();
                        filterLastLoginCount();

                        if (currentDataList.length) {
                            $scope.datalist = currentDataList;
                            currentDataList = [];
                        } else {
                            $scope.datalist = $scope.currentCourse.students;
                        }

                        sumStudentsAtRisk();
                    }
                }

                $scope.$watchGroup(['submissionFilterScore', 'submissionFilter', 'gradeFilterScore', 'gradeFilter', 'daysSinceLoginFilter', 'daysSinceLoginFilterCount'], runFilters);

                $scope.handleEmail = function(o, bulk) {
                    $scope.emailList = [];
                    if (bulk) {
                        _.each($scope.datalist, function(s) {
                            $scope.emailList.push({
                                label: s.label,
                                email: s.email
                            });
                        });
                    } else {
                        $scope.emailList.push({
                            label: o.label,
                            email: o.email
                        });
                    }
                    $('#emailModal').modal('show');
                }


                function buildStudent(cid, sid) {
                    var course = _.filter($scope.processedClasses, function(c) {
                        return c.id === cid;
                    })[0];

                    $scope.classes = $scope.processedClasses;
                    $scope.currentCourse = course;

                    $scope.maxEvents = course.studentEventMax;

                    var student = _.filter(course.students, function(s) {
                        return s.id === sid;
                    })[0];

                    $scope.currentStudent = student;
                    $scope.datalist = [];
                    $scope.$broadcast('draw-chart');
                }

                function buildStudentList(id) {
                    var course = _.filter($scope.processedClasses, function(c) {
                        return c.id === id;
                    })[0];

                    $scope.classes = $scope.processedClasses;
                    $scope.currentCourse = course;
                    $scope.maxEvents = course.studentEventMax;
                    buildActivityColorThreshold();
                    runFilters();

                    buildWholeClassRiskChart();
                }

                function buildWholeClassRiskChart() {
                    var chartData = {
                        labels: [],
                        datasets: []
                    };
                    var i = 0;

                    var eventMax = $scope.currentCourse.studentEventTotalMax;


                    _.each($scope.currentCourse.students, function(student) {

                        (student.activity / eventMax) * 25;
                        chartData.labels.push(student.label);
                        chartData.datasets.push(

                            {
                                label: [student.label],
                                data: [{
                                    x: i,
                                    y: student.risk,
                                    r: (student.activity / eventMax) * 20
                                }],
                                backgroundColor: [$scope.colorCodeRiskForBubble(student.risk)],
                                borderColor: [$scope.colorCodeRiskForBubble(student.risk)]
                            }

                        );
                        i++;
                    })


                    new Chart(document.getElementById("wholeClassRisk-chart"), {
                        type: 'bubble',
                        data: chartData,

                        options: {

                            tooltips: {
                                mode: 'nearest'
                            },

                            title: {
                                display: false,
                                text: 'Probability of Success'
                            },
                            scales: {
                                yAxes: [{
                                    scaleLabel: {
                                        display: true,
                                        labelString: "Probability of Success"
                                    }
                                }],
                                xAxes: [{
                                    display: false,
                                    scaleLabel: {
                                        display: true,
                                        labelString: "Students"
                                    }
                                }]
                            }
                        }
                    });


                }

                $scope.$on('leafletDirectiveMarker.mouseover', function(event, args) {
                    var popup = L.popup({
                            offset: L.point(0, -28)
                        })
                        .setLatLng([args.model.lat, args.model.lng])
                        .setContent(args.model.message)
                    leafletData.getMap().then(function(map) {
                        popup.openOn(map);
                    });
                });

                $scope.$on('leafletDirectiveMarker.mouseout', function(event) {
                    leafletData.getMap().then(function(map) {
                        map.closePopup();
                    });
                });


                function addMissingData() {
                    // set app optional data configs
                    $scope.config.hasRisk = $scope.config.hasRisk ? $scope.config.hasRisk : false;
                    $scope.config.hasGrade = $scope.config.hasGrade ? $scope.config.hasGrade : false;
                    $scope.config.hasEmail = $scope.config.hasEmail ? $scope.config.hasEmail : false;
                    $scope.config.hasMissingSubmissions = $scope.config.hasMissingSubmissions ? $scope.config.hasMissingSubmissions : false;
                    $scope.config.hasLastLogin = $scope.config.hasLastLogin ? $scope.config.hasLastLogin : false;

                    $scope.coursesMaxEvents = $scope.config.classEventMax;

                    // fake email data
                    if ($scope.config.hasRisk && !$scope.processedClasses[0].students[0].email) {
                        _.each($scope.processedClasses, function(c) {
                            _.each(c.students, function(s) {
                                s.email = s.email ? s.email : s.givenName + '@' + s.givenName + s.familyName + '.com';
                            });
                        });
                    }

                    // fake risk data
                    if ($scope.config.hasGrade && !$scope.processedClasses[0].students[0].grade) {
                        _.each($scope.processedClasses, function(c) {
                            _.each(c.students, function(s) {
                                s.grade = s.grade ? s.grade : Math.round(Math.random() * (100 - 0));
                            });
                        });
                    }


                    // fake missing submission data
                    if ($scope.config.hasMissingSubmissions && $scope.processedClasses[0].students[0].missingSubmission !== 0) {
                        _.each($scope.processedClasses, function(c) {
                            _.each(c.students, function(s) {
                                s.missingSubmission = s.missingSubmission ? s.missingSubmission : Math.round(Math.random() * (50 - 0));
                            });
                        });
                    }

                }

                function setCoursesTerm() {
                    $scope.coursesStartEnd = {
                        start: $scope.config.startDate,
                        end: $scope.config.endDate
                    };

                }

                function handleAssignmentsWithoutDueDates() {
                    _.each($scope.processedClasses, function(c) {
                        _.forEachRight(c.assignments, function(a, i) {
                            if (!a.dueDate) {
                                $scope.assignmentsWithoutDueDates.push(a);
                                c.assignments.splice(i, 1);
                            }
                        });
                    });
                }

                $scope.updateStudentCharts = function(data) {
                    $scope.$broadcast('updateTable', data);
                };

                function init() {
                    $rootScope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
                        if (toState.name === "index.courselist" && toParams.groupId) {
                            $scope.listType = 'students';
                            $scope.orderByField = 'lastName';
                            buildStudentList(toParams.groupId);
                        } else if (toState.name === "index.courselist" && !toParams.groupId) {
                            // $scope.maxEvents = pulseDataService.coursesMaxEvents;
                            $scope.datalist = processedClasses;
                            $scope.listType = 'classes';
                            $scope.orderByField = 'label';
                        } else if (toState.name === "index.courselist.studentView") {
                            $scope.listType = 'student';
                            buildStudent($scope.currentCourse.id, toParams.studentId);
                        }
                    });

                    if (currentUser) {
                        PulseApiService
                            .getPulseData(currentUser.tenant_id, currentUser.user_id)
                            .then(function(data) {

                                $scope.config = _.clone(data);
                                delete $scope.config.pulseClassDetails;
                                $scope.processedClasses = data.pulseClassDetails;

                                addMissingData();
                                setCoursesTerm();
                                handleAssignmentsWithoutDueDates();

                                if ($state.params.studentId && $state.params.groupId) {
                                    $scope.listType = 'student';
                                    buildStudent($state.params.groupId, $state.params.studentId);
                                } else if ($state.params.groupId) {
                                    $scope.listType = 'students';
                                    $scope.orderByField = 'lastName';
                                    buildStudentList($state.params.groupId);
                                } else {
                                    $scope.orderByField = 'label';
                                    $scope.datalist = $scope.processedClasses;
                                    $scope.listType = 'classes';
                                }
                            });
                    }
                }
                init();
            }
        ])

        .directive('ngRepeatFinish', [
            '$timeout',
            function($timeout) {
                return {
                    link: function(scope, element, attr) {
                        function checkFinished() {
                            if (scope.$last === true) {
                                $timeout(function() {
                                    scope.$emit(attr.ngRepeatFinish);
                                });
                            }
                        }

                        scope.$watch(attr.watchForChanges, checkFinished);
                    }
                }
            }
        ])




        .directive('pulse', [
            '$timeout',
            '$window',
            function($timeout, $window) {
                return {
                    controller: 'pulseController',
                    templateUrl: 'assets/js/pulse/pulse.html',
                    link: function(scope, element, attrs) {

                        // elements
                        var floatingHeaderTable = $('#pulse-table-header');
                        var dataTable = $('#pulse-table-data');
                        var headerHeight = $('.navbar').height();

                        // default variables
                        var courseStart;
                        var courseEnd;
                        var weeks;
                        var xAxis;

                        // d3 timescale
                        var timeScale = d3.scaleTime();

                        function setDates() {
                            courseStart = moment(scope.coursesStartEnd.start).startOf('week');
                            courseEnd = moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'));
                            weeks = Math.round(moment.duration(courseEnd - courseStart).asWeeks());

                            // generate x axis
                            xAxis = d3.axisBottom()
                                .scale(timeScale)
                                .ticks(weeks)
                                .tickFormat(d3.timeFormat('%m-%d'));
                        }

                        function setAssignmentToolTipPosition(pos) {
                            var tip = $('.tool-tip-assignment-info');
                            var posOffset = {
                                y: pos.y,
                                x: pos.x,
                            };

                            $timeout(function() {
                                if (pos.x + tip.width() + 25 > $(document).width()) {
                                    tip.css({
                                        'top': posOffset.y - 20,
                                        'left': posOffset.x - tip.width() - 65,
                                        'visibility': 'visible'
                                    });
                                } else {
                                    tip.css({
                                        'top': posOffset.y - 20,
                                        'left': posOffset.x - 15,
                                        'visibility': 'visible'
                                    });
                                }
                            });
                        }

                        function setEventToolTipPosition(pos) {
                            var tip = $('.tool-tip-event-info');

                            var posOffset = {
                                y: pos.y,
                                x: pos.x,
                            };

                            $timeout(function() {
                                if (pos.x + tip.width() + 25 > $(document).width()) {
                                    tip.css({
                                        'top': posOffset.y - 20,
                                        'left': posOffset.x - tip.width() - 65,
                                        'visibility': 'visible'
                                    });
                                } else {
                                    tip.css({
                                        'top': posOffset.y - 20,
                                        'left': posOffset.x - 15,
                                        'visibility': 'visible'
                                    });
                                }
                            });
                        }

                        function hideToolTips() {
                            $('.tool-tip-assignment-info').css({
                                'visibility': 'hidden'
                            })
                            $('.tool-tip-event-info').css({
                                'visibility': 'hidden'
                            })
                        }


                        function drawPlots(plots, o) {
                            plots.selectAll('circle').remove();
                            plots
                                .selectAll('circle')
                                .data(o.events)
                                .enter()
                                .append('circle')
                                .attr('r', function(d) {
                                    var count;

                                    if (o.students) {
                                        count = d.eventCount * 100 / scope.coursesMaxEvents / 10 + 1;
                                    } else {
                                        count = d.eventCount * 100 / scope.maxEvents / 10 + 1;
                                    }

                                    return count;
                                })
                                .attr('cx', function(d) {
                                    return timeScale(moment(d.date));
                                })
                                .attr('class', 'dot')
                                .attr('opacity', 0.5)
                                .style('fill', function(d) {
                                    if (o.students) {
                                        return '#00a9a7';
                                    } else {
                                        return '#0087a7';
                                    }

                                })
                                .on('mouseover', function(d, index, node) {
                                    d3.select(this).attr('opacity', '1');
                                    setEventToolTipPosition({
                                        x: d3.event.pageX,
                                        y: d3.event.pageY
                                    });
                                    scope.$apply(function() {
                                        scope.chartInfo = {
                                            date: moment(d.date),
                                            events: d.eventCount
                                        };
                                    });

                                })
                                .on('mouseout', function(d) {
                                    d3.select(this).attr('opacity', '0.5');
                                    scope.$apply(function() {
                                        scope.chartInfo = undefined;
                                    });
                                    hideToolTips();
                                })
                                .on('click', zoomIn);
                        }

                        function zoomIn(d) {

                            var date = d.date;
                            if (d.hasOwnProperty('dueDate')) {
                                date = d.dueDate ? d.dueDate : courseStart;
                            }

                            d3.select('.zoom-out')
                                .classed('active', true)
                                .on('click', zoomOut);

                            if (weeks <= 6) {
                                var daysBack = moment(date).subtract(moment.duration(3, 'day'));
                                var daysForward = moment(date).add(moment.duration(4, 'day'));
                                xAxis.ticks(7);

                                timeScale.domain([
                                    daysBack,
                                    daysForward
                                ]);

                            } else {
                                var weeksBack = moment(date).startOf('week').subtract(moment.duration(2, 'week'));
                                var weeksForward = moment(date).startOf('week').add(moment.duration(3, 'week'));
                                xAxis.ticks(5);

                                timeScale.domain([
                                    weeksBack,
                                    weeksForward
                                ]);

                            }

                            d3.selectAll("svg.timeline-svg .xaxis")
                                .transition()
                                .duration(750)
                                .call(xAxis);

                            d3.selectAll("svg .dot")
                                .transition()
                                .duration(750)
                                .attr('cx', function(d, index) {
                                    var placement = timeScale(moment(d.date));
                                    return placement;
                                });

                            var assignmentDatesX1 = [];
                            var assignmentDatesX2 = [];
                            d3.selectAll('#assignment-overlay svg .assignment-marker')
                                .transition()
                                .duration(750)
                                .attr('x1', function(d) {
                                    return assignmentPositon(d, assignmentDatesX1);
                                })
                                .attr('x2', function(d) {
                                    return assignmentPositon(d, assignmentDatesX2);
                                });


                        }

                        function zoomOut(d) {
                            d3.select('.zoom-out')
                                .classed('active', false);

                            timeScale.domain([
                                courseStart,
                                courseEnd
                            ]);

                            xAxis.ticks(weeks);

                            d3.selectAll("svg.timeline-svg .xaxis")
                                .transition()
                                .duration(750)
                                .call(xAxis);

                            d3.selectAll("svg .dot")
                                .transition()
                                .duration(750)
                                .attr('cx', function(d, index) {
                                    var placement = timeScale(moment(d.date));
                                    return placement;
                                });

                            var assignmentDatesX1 = [];
                            var assignmentDatesX2 = [];
                            d3.selectAll('#assignment-overlay svg .assignment-marker')
                                .transition()
                                .duration(750)
                                .attr('x1', function(d) {
                                    return assignmentPositon(d, assignmentDatesX1);
                                })
                                .attr('x2', function(d) {
                                    return assignmentPositon(d, assignmentDatesX2);
                                });

                        }


                        function drawTimeline() {
                            var timelineHeader = floatingHeaderTable.find('.timeline-heading');
                            var tlhHeight = timelineHeader.height();
                            var tlhWidth = timelineHeader.width();

                            timeScale.domain([
                                courseStart,
                                courseEnd
                            ]);

                            if ($('#floating-header .timeline-heading svg.timeline-svg').length) {
                                var svgTimelineHeader = d3.select('#floating-header .timeline-heading svg');
                            } else {
                                var svgTimelineHeader = d3.select('#floating-header .timeline-heading').append('svg');
                                timeScale.range([50, tlhWidth - 50]);

                                svgTimelineHeader
                                    .attr("class", 'timeline-svg')
                                    .attr("height", tlhHeight)
                                    .attr("width", "100%")
                                var xLabel = svgTimelineHeader
                                    .append('g')
                                    .attr('class', 'xaxis')
                                    .attr('transform', 'translate(0, 10)')
                                    .call(xAxis);
                            }



                            if (scope.listType !== "classes") {
                                if ($('#floating-header .timeline-heading svg.overview-svg g').length) {
                                    var overviewPlot = d3.select('#floating-header .timeline-heading svg.overview-svg g');

                                } else {
                                    var svgTimeOverview = d3.select('#floating-header .timeline-heading').insert("svg", ":first-child");

                                    svgTimeOverview
                                        .attr("class", 'overview-svg')
                                        .attr("height", tlhHeight)
                                        .attr("width", "100%");

                                    var overviewPlot = svgTimeOverview.append('g')
                                        .attr('transform', 'translate(0,' + tlhHeight / 2 + ')')
                                        .attr('class', 'plot');
                                }
                                if (scope.listType === "students") {
                                    drawPlots(overviewPlot, scope.currentCourse);
                                }
                                if (scope.listType === "student") {
                                    drawPlots(overviewPlot, scope.currentStudent);
                                }
                            }
                        }

                        function drawChart(obj) {

                            drawTimeline();

                            var charts = {};
                            var height, width;

                            // set width n height
                            _.each(scope.datalist, function(o, i) {

                                if (i == 0) {
                                    height = $('#pulse-chart-' + o.id).height();
                                    width = $('#pulse-chart-' + o.id).width();
                                }

                                charts[o.id] = !d3.select('#pulse-chart-' + o.id + ' svg').empty() ? d3.select('#pulse-chart-' + o.id + ' svg') : d3.select('#pulse-chart-' + o.id).append('svg');
                                var svg = charts[o.id];


                                svg
                                    .attr("height", height)
                                    .attr("width", "100%");

                                // row plot group
                                var plots = !svg.select('g').empty() ? svg.select('g') : svg.append('g');

                                plots
                                    .attr('transform', 'translate(0,' + height / 2 + ')')
                                    .attr('class', 'plot');

                                // dot
                                drawPlots(plots, o);
                            });

                            // align elements based on layout
                            $('#pulse-data').css({
                                'padding-top': $('.pulse-header').height() - $('#hidden-header').height() + ($('body').hasClass('isLTI') ? 0 : 9)
                            });
                            if (scope.listType === 'classes') {
                                $('.zoom-actions').css({
                                    'width': $('#floating-header .timeline-heading').width() + 20,
                                    'top': $('.pulse-header').height() / 2 + ($('body').hasClass('isLTI') ? 0 : 35)
                                });
                            } else {
                                $('.zoom-actions').css({
                                    'width': $('#floating-header .timeline-heading').width() + 20,
                                    'top': $('.pulse-header').height() / 2 + ($('body').hasClass('isLTI') ? 0 : 50)
                                });
                            }

                            $('.pulse-header .student-details').width($('#floating-header .timeline-heading').position().left - 20);

                            if (scope.listType !== 'classes') {
                                drawAssignments();
                            } else {
                                $('#pulse-data').trigger('chart-render-finish');
                            }

                            scope.chartInitialized = true;

                        }


                        function assignmentPositon(d, arr) {
                            var date = d.dueDate;

                            var search = date.split('T')[0];
                            var count = arr.reduce(function(n, val) {
                                return n + (val === search);
                            }, 0);

                            arr.push(date.split('T')[0]);

                            if (count > 0) {
                                return timeScale(moment(date, moment.ISO_8601)) + (4 * count);
                            } else {
                                return timeScale(moment(date, moment.ISO_8601));
                            }
                        }

                        function drawAssignments() {
                            var heading = floatingHeaderTable.find('.timeline-heading');
                            var width = heading.width();
                            var height = floatingHeaderTable.height() + dataTable.height() - $('#hidden-header').height();
                            var offset = heading.offset();
                            var overlay = $('#assignment-overlay');
                            var assignmentDatesX1 = [];
                            var assignmentDatesX2 = [];

                            overlay.css({
                                'left': offset.left,
                                'top': offset.top,
                                'width': width,
                                'height': height
                            });

                            var assignments = d3.select('#assignment-overlay svg')
                                .attr('class', 'assignments-overlay')
                                .attr("height", height)
                                .attr("width", "100%");

                            assignments.selectAll('line').remove();
                            assignments
                                .selectAll('line')
                                .data(scope.currentCourse.assignments)
                                .enter()
                                .append('line')
                                .attr('class', function(d, index) {
                                    var placement = timeScale(moment(d.dueDate, moment.ISO_8601));
                                    var classname = "assignment-marker " + d.category.title;
                                    return classname;
                                })
                                .attr('x1', function(d, i) {
                                    return assignmentPositon(d, assignmentDatesX1);
                                })
                                .attr('x2', function(d) {
                                    return assignmentPositon(d, assignmentDatesX2);;
                                })
                                .attr('y1', 0)
                                .attr('y2', height)
                                .attr('opacity', 0.3)
                                .on('mouseover', function(d) {
                                    d3.select(this)
                                        .attr('opacity', 1);

                                    setAssignmentToolTipPosition({
                                        x: d3.event.pageX,
                                        y: d3.event.pageY
                                    });

                                    scope.$apply(function() {
                                        scope.assignmentInfo = {
                                            label: d.title,
                                            events: d.category.title
                                        };
                                        if (d.dueDate) {
                                            scope.assignmentInfo.date = moment(d.dueDate, moment.ISO_8601)
                                        }
                                    });

                                })
                                .on('mouseout', function(d) {
                                    d3.select(this)
                                        .attr('opacity', 0.3);

                                    scope.$apply(function() {
                                        scope.assignmentInfo = undefined;
                                    });
                                    hideToolTips();
                                })
                                .on('click', zoomIn);

                            $('#pulse-data').trigger('chart-render-finish');
                        };

                        function alignTables(callback) {
                            $timeout(function() {
                                var fPlot = floatingHeaderTable.find('.timeline-heading');
                                var dPlot = dataTable.find('.timeline-heading');

                                fPlot.removeAttr('style');
                                dPlot.removeAttr('style');

                                var hw = fPlot.width();
                                var dw = dPlot.width();
                                var w = hw < dw ? hw : dw;

                                fPlot.width(w);
                                dPlot.width(w);
                                if (callback) {
                                    callback();
                                }
                            });
                        }

                        function handleResize() {
                            alignTables();

                            var fPlot = floatingHeaderTable.find('.timeline-heading');
                            var width = fPlot.width();
                            var height = $('#pulse-table-header').height() + $('#pulse-table-data').height() - $('#hidden-header').height();
                            var offset = fPlot.offset();
                            var overlay = $('#assignment-overlay');

                            $('.zoom-actions').width(width + 20);
                            $('.pulse-header .student-details').width(offset.left);

                            timeScale.range([50, width - 50]);

                            d3.selectAll("svg.timeline-svg .xaxis")
                                .transition()
                                .duration(750)
                                .call(xAxis);

                            d3.selectAll("svg .dot")
                                .transition()
                                .duration(750)
                                .attr('cx', function(d, index) {
                                    var placement = timeScale(moment(d.date));
                                    return placement;
                                });

                            d3.selectAll("svg .assignment-marker")
                                .transition()
                                .duration(750)
                                .attr('x1', function(d) {
                                    return timeScale(moment(d.dueDate, moment.ISO_8601));
                                })
                                .attr('x2', function(d) {
                                    return timeScale(moment(d.dueDate, moment.ISO_8601));
                                });

                            overlay.css({
                                'left': offset.left,
                                'top': offset.top,
                                'width': width,
                                'height': height
                            });
                        }

                        // events / listeners
                        $('.pulse-charts').on('chart-render-finish', function() {
                            $('.hide-until-ready').fadeTo('slow', 1);
                        });

                        scope.$on('draw-chart', function() {
                            setDates();
                            alignTables(drawChart);
                        });

                        scope.$on('draw-assignments', drawAssignments);

                        var resizeTimer;

                        $(window).on('resize', function(e) {
                            floatingHeaderTable.find('.timeline-heading').removeAttr('style');
                            clearTimeout(resizeTimer);
                            resizeTimer = setTimeout(function() {

                                // Run code here, resizing has "stopped"
                                handleResize();

                            }, 250);

                        });
                    }
                };
            }
        ]);

})(angular);