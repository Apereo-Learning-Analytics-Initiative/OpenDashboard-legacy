(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('pulseController',[
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
    'pulseDataService',
    function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, EnrollmentDataService, UserDataService, EventService, LineItemDataService, pulseDataService){
      "use strict";

    $scope.chartInitialized = false;
    $scope.listType = 'classes';
    $scope.datalist = [];
    $scope.coursesMaxEvents = 0;
    $scope.maxEvents = 0;
    $scope.emailstudent = {};
    $scope.studentFilters = {};
    $scope.studentFilters.list = [];

    $scope.coursesStartEnd = {
      start: '2016-08-30',
      end: '2016-12-13'
    };

    var processedClasses = [];
    var classes = [];
    var students = [];
    var currentCourse = null;
    var currentUser = null;
    var currentStudent = null;


    currentUser = SessionService.getCurrentUser();

    $scope.orderByField = 'label';
    $scope.reverseSort = false;
    $scope.assignmentOverlay = true;
    $scope.gradeFilter = false;
    $scope.gradeFilterScore = 25;
    $scope.submissionFilterScore = 6;

    $scope.appHasRiskData = false;
    $scope.riskOverlay = true;
    var riskColorClasses = [
      'no-risk',
      'low-risk',
      'medium-risk',
      'high-risk',
    ];
    
    $scope.emailList = [];

    $scope.colorCodeRisk = function(risk){
      if ($scope.riskOverlay) {
        var colorclass;
        // var riskDivided = 100/riskColorClasses.length;
        // console.log(Math.round(riskDivided/(riskDivided+)));
        _.each(riskColorClasses, function(r, i){
          console.log('between: ' + 100/riskColorClasses.length*(i) + " & " + 100/riskColorClasses.length*(i+1));
          if (risk >= 100/riskColorClasses.length*(i) && risk <= 100/riskColorClasses.length*(i+1)) {
            colorclass = riskColorClasses[i];
          }
        });
        return colorclass;
      } else {
        return "";
      }
    }

    function filterByGrade(nv){
      if ($scope.currentCourse) {
        if ($scope.gradeFilter) {
          $scope.datalist = _.filter($scope.currentCourse.students, function(o){
            return o.grade < $scope.gradeFilterScore;
          });
          
        } else {
          $scope.datalist = $scope.currentCourse.students;
        }
      }
    }

    function filterByMissingSubmissions(nv, ov){
      console.log('filterByMissingSubmissions');
      if ($scope.currentCourse) {
        if ($scope.submissionFilter) {
          $scope.datalist = _.filter($scope.currentCourse.students, function(o){
            return o.missingSubmission < $scope.submissionFilterScore;
          });
          
        } else {
          $scope.datalist = $scope.currentCourse.students;
        }
      }
    }

    function runFilters() {
      filterByGrade();
      filterByMissingSubmissions();
    }

    $scope.$watchGroup(['gradeFilterScore', 'gradeFilter'], filterByGrade);
    $scope.$watchGroup(['submissionFilterScore', 'submissionFilter'], filterByMissingSubmissions);

    $scope.handleEmail = function(o, bulk) {
      $scope.emailList = [];
      if (bulk) {
        _.each($scope.datalist, function(s){
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
      var course = _.filter($scope.processedClasses, function(c){
        return c.id === cid;
      })[0];

      $scope.classes = $scope.processedClasses;
      $scope.currentCourse = course;

      $scope.maxEvents = course.studentEventMax;

      var student = _.filter(course.students, function(s){
        return s.id === sid;
      })[0];

      $scope.currentStudent = student;
      $scope.datalist = [];
      $scope.$broadcast('draw-chart');
    }

    function buildStudentList(id) {
      console.log('buildStudentList');
      var course = _.filter($scope.processedClasses, function(c){
        return c.id === id;
      })[0];
      
      $scope.classes = $scope.processedClasses;
      $scope.currentCourse = course;

      $scope.appHasRiskData = $scope.currentCourse.students[0].risk ? true : false;

      $scope.maxEvents = course.studentEventMax;
      runFilters();
    }

    // function handleChartChange(event, data) {
    //   console.log('handleChartChange');
    //   if (data.type === 'course') {
    //     // Go to specific course with list of students
    //     $state.go('index.courselist', { groupId: data.id });
    //     // $state.go('index.courselist', { groupId: data.id }, {notify: false});
    //     // $state.go('index.courselist.empty', { groupId: $state.params.groupId, studentId: data.id }, {notify: false} );
    //     $scope.listType = 'students';
    //     $scope.orderByField = 'lastName';

    //     buildStudentList(data.id);

    //   } else if (data.type === 'courselist') {
    //     // Go to specific student
    //     $state.go('index.courselist', { groupId: $state.params.groupId, studentId: data.id });
    //     // $state.go('index.courselist.empty', { groupId: $state.params.groupId, studentId: data.id }, {notify: false} );
    //     $scope.maxEvents = $scope.coursesMaxEvents;
    //     $scope.datalist = processedClasses;
    //     $scope.listType = 'classes';  
    //     $scope.orderByField = 'label';
    //     $scope.$broadcast('draw-chart');

    //   } else if (data.type === 'student') {
    //     // $state.go('index.courselist.studentView', { groupId: $state.params.groupId, studentId: data.id }, {reload: false, inherit: true, notify: true} );
    //     $scope.listType = 'student';  
    //     buildStudent($scope.currentCourse.id, data.id);
    //     // Go to specific student
    //     $state.go('index.courselist.studentView', { groupId: $state.params.groupId, studentId: data.id });

    //   }
    // }

    // $scope.drawChart = function(draw) {
    //   console.log(draw);
    //   if (draw) {
    //     $scope.$emit('draw-chart');
    //   }
    // }

    $scope.updateStudentCharts = function (data) {
      // $timeout(function(){
        $scope.$broadcast('updateTable', data);
      // });
    };

    function init() {
      console.log('init');
      // $scope.$on('chart-change', handleChartChange);

      $rootScope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
        if (toState.name === "index.courselist" && toParams.groupId) {

          $scope.listType = 'students';
          $scope.orderByField = 'lastName';
          buildStudentList(toParams.groupId);
        } else if (toState.name === "index.courselist" && !toParams.groupId) {
          $scope.maxEvents = pulseDataService.coursesMaxEvents;
          $scope.datalist = processedClasses;
          $scope.listType = 'classes';  
          $scope.orderByField = 'label';
        } else if (toState.name === "index.courselist.studentView") {
          $scope.listType = 'student';  
          buildStudent($scope.currentCourse.id, toParams.studentId);
        } 
      });

      /**
        * TODO: Move data loading to payload resolver
      */

      if (currentUser) {
        pulseDataService.initData(currentUser).then(function(data){
            $scope.processedClasses = data;
            $scope.coursesMaxEvents = pulseDataService.coursesMaxEvents;
            if ($state.params.studentId && $state.params.groupId) {
              $scope.listType = 'student';
              buildStudent($state.params.groupId, $state.params.studentId);
            } else if ($state.params.groupId) {
              $scope.listType = 'students';
              $scope.orderByField = 'lastName';
              buildStudentList($state.params.groupId);
            } else {
              $scope.maxEvents = pulseDataService.coursesMaxEvents;
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

  .factory('pulseDataService',[
      '$q',
      'SessionService',
      'EnrollmentDataService',
      'UserDataService',
      'EventService',
      'LineItemDataService',
      function($q, SessionService, EnrollmentDataService, UserDataService, EventService, LineItemDataService) {
        var service = {
          coursesProcessed: false,
          processedClasses: [],
          processData: function(c) {
            var maxEvents = 0;

            // // build course object
            var course = {
              id: c.sourcedId,
              label: c.title,
              studentEventMax: 0,
              events: [],
              students: [],
              assignments: c.assignments
            };

            // process events object for the class
            _.each(c.statistics.eventCountGroupedByDate, function(event, index){
              if (maxEvents < event) {
                maxEvents = event;
              }

              course.events.push({
                date: index,
                eventCount: event
              });
            });

            var students = c.statistics.eventCountGroupedByDateAndStudent;
            _.each(students, function(s, i) {
              var studentSrc = _.filter(service.students, function(student){ 
                return student.sourcedId === i;
              })[0];

              // build student object
              var student = {
                id: i,
                label: studentSrc.familyName + ', ' + studentSrc.givenName,
                firstName: studentSrc.givenName,
                lastName: studentSrc.familyName,
                email: studentSrc.givenName + '@' + studentSrc.givenName+studentSrc.familyName + '.com',
                risk: Math.round(Math.random() * (100 - 0)),
                grade: Math.round(Math.random() * (100 - 0)),
                activity: Math.round(Math.random() * (1000 - 100) + 100),
                missingSubmission: Math.round(Math.random() * 6),
                events: []
              };

              _.each(s, function(e, j) {
                if (course.studentEventMax < e) {
                  course.studentEventMax = e;
                }

                student.events.push({
                  date: j,
                  eventCount: e
                });
              });

              course.students.push(student);

            });


            // console.log('course.students', course.students);
            // course.students = _.sortBy(course.students, function (student) {
            //   return student.label;
            // });

            // // add course object to classes array
            service.processedClasses.push(course);


            // console.log('processedClasses', processedClasses);
            service.coursesMaxEvents = maxEvents;
          },
          initData: function(currentUser) {
            var deferred = $q.defer();

            if (service.processedClasses.length) {
              deferred.resolve(service.processedClasses);
            } else {
              EnrollmentDataService.getEnrollmentsForUser(currentUser.tenant_id, currentUser.user_id)
                .then(function(enrollments){
                  if (enrollments.isError) {
                    // $scope.errorData = {};
                    // $scope.errorData['userId'] = currentUser.user_id;
                    // $scope.errorData['errorCode'] = enrollments.errorCode;
                    // $scope.error = enrollments.errorCode;
                  } else {
                    var statCount = 1;

                    // console.log(enrollments);

                    _.each(enrollments, function (enrollment) {
                      //classes.push(enrollment.class);

                      LineItemDataService.getLineItemsForClass(currentUser.tenant_id, enrollment.class.sourcedId)
                      .then(function(data){
                        enrollment.class.assignments = data;
                      });

                      
                      EventService.getEventStatisticsForClass(currentUser.tenant_id, enrollment.class.sourcedId)
                          .then(function (statistics) {
                              enrollment.class.statistics = statistics;

                              var keys = _.keys(statistics.eventCountGroupedByDateAndStudent);
                              var queue = [];


                               _.each(keys, function (studentId) {
                                queue.push(UserDataService.getUser(currentUser.tenant_id, studentId));
                              });

                              return $q
                                .all(queue)
                                .then(function (response) {
                                  service.students = response;
                                })
                                .then(function () {
                                  service.processData(enrollment.class);
                                })
                                .finally(function(){
                                  if (statCount === enrollments.length) {
                                    service.coursesProcessed = true;
                                    deferred.resolve(service.processedClasses);
                                  }
                                  statCount++;
                                });
                          });
                    });
                  }   
              });
            }



            return deferred.promise;
          }
        };
        return service;
      }
    ]
  )


  .directive('ngRepeatFinish', [
    '$timeout',
    function($timeout) {
      return {
        link: function (scope, element, attr) {
          function checkFinished() {
            if (scope.$last === true) {
              $timeout(function () {
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
    function($timeout) {
    return {
      controller: 'pulseController',
      templateUrl: 'assets/js/pulse/pulse.html',
      link: function (scope, element, attrs) {

        // elements
        var floatingHeaderTable = $('#pulse-table-header');
        var dataTable = $('#pulse-table-data');
        var headerHeight = $('.navbar').height();

        // default variables
        var courseStart = moment(scope.coursesStartEnd.start).startOf('week');
        var courseEnd = moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'));
        var weeks = Math.round(moment.duration(courseEnd - courseStart).asWeeks());

        // d3 timescale
        var timeScale = d3.scaleTime();

        // generate x axis
        var xAxis = d3.axisBottom()
          .scale(timeScale)
          .ticks(weeks)
          .tickFormat(d3.timeFormat('%m-%d'));


        function setAssignmentToolTipPosition (pos) {
          var posOffset = {
            y: pos.y,
            x: pos.x ,
          };

          $('.tool-tip-assignment-info').css({
            'top': posOffset.y - 80,
            'left': posOffset.x - 15,
          });
        }

        function setEventToolTipPosition (pos) {
          var posOffset = {
            y: pos.y,
            x: pos.x - 0,
          };

          $('.tool-tip-event-info').css({
            'top': posOffset.y - 80,
            'left': posOffset.x - 15,
          });
        }


        function drawPlots(plots, o) {
          plots.selectAll('circle').remove();
          plots
            .selectAll('circle')
            .data(o.events)
            .enter()
            .append('circle')
            // .attr('r', 2)
            .attr('r', function (d) {
              var count;

              if (o.students) {
                count = d.eventCount*100/scope.coursesMaxEvents/10+1;
              } else {
                count = d.eventCount*100/scope.maxEvents/10+1;
              }
              return count;
            })
            .attr('cx', function (d) {
              return timeScale(moment(d.date));
            })
            .attr('class', 'dot')
            .attr('opacity', 0.5)
            .style('fill', function (d) {
              if (o.students) {
                return '#00a9a7';
              } else {
                return '#0087a7';  
              }

            })
            .on('mouseover', function (d, index, node) {
              d3.select(this).attr('opacity','1');
              // console.log(d3.event);
              setEventToolTipPosition({
                x: d3.event.pageX,
                y: d3.event.pageY
                // x: d3.event.clientX,
                // y: d3.event.clientY
              });
              scope.$apply(function () {
                  scope.chartInfo = {
                    date: moment(d.date),
                    events: d.eventCount
                  };
              });

            })
            .on('mouseout', function (d) {
              d3.select(this).attr('opacity','0.5');
              scope.$apply(function () {
                  scope.chartInfo = undefined;
              });
            })
            .on('click', zoomIn);
        }

        function zoomIn(d) {
          
          d3.select('.zoom-out')
          .classed('active', true)
          .on('click', zoomOut);

          var weeksBack = moment(d.date).startOf('week').subtract(moment.duration(2, 'week'));
          var weeksForward = moment(d.date).startOf('week').add(moment.duration(3, 'week'));
          
          timeScale.domain([
              weeksBack, 
              weeksForward
            ]);

          xAxis.ticks(5);

          d3.selectAll("svg.timeline-svg .xaxis")
            .transition()
            .duration(750)
            .call(xAxis);

          d3.selectAll("svg .dot")
            .transition()
            .duration(750)
            .attr('cx', function (d, index) {
              var placement = timeScale(moment(d.date));
              return placement;
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
            .attr('cx', function (d, index) {
              var placement = timeScale(moment(d.date));
              return placement;
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
              var svgTimeOverview = d3.select('#floating-header .timeline-heading').insert("svg",":first-child");

              svgTimeOverview
              .attr("class", 'overview-svg')
              .attr("height", tlhHeight)
              .attr("width", "100%");

              var overviewPlot = svgTimeOverview.append('g')
                .attr('transform','translate(0,'+tlhHeight/2+')')
                .attr('class','plot');
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
          _.each(scope.datalist, function(o, i){
            if (i==0) {
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
              .attr('transform','translate(0,'+height/2+')')
              .attr('class','plot');
            
            // dot
            drawPlots(plots, o);
          });

          // align elements based on layout
          $('#pulse-data').css({'padding-top':$('.pulse-header').height() - $('#hidden-header').height() + 9});
          $('.zoom-actions').width($('#floating-header .timeline-heading').width() + 20);
          $('.pulse-header .student-details').width($('#floating-header .timeline-heading').position().left - 20);

          
          // $('#pulse-data').css({'padding-top':$('.pulse-header').height() + 9});

          if (scope.listType !== 'classes') {
            drawAssignments();
          } else {
            $('#pulse-data').trigger('chart-render-finish');
          }
          
          scope.chartInitialized = true;

        }

        function drawAssignments() {
          var heading = floatingHeaderTable.find('.timeline-heading');
          var width = heading.width();
          var height = floatingHeaderTable.height() + dataTable.height() - $('#hidden-header').height();
          var offset = heading.offset();
          var overlay = $('#assignment-overlay');
          
          console.log($('#pulse-table-header .timeline-heading').offset());
          console.log(offset);

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
            .attr('class', function (d, index) {
              var placement = timeScale(moment(d.dueDate, moment.ISO_8601));
              var classname = "assignment-marker " + d.category.title;
              return classname;
            })
            .attr('x1', function(d){
              return timeScale(moment(d.dueDate, moment.ISO_8601));
            })
            .attr('x2', function(d){
              return timeScale(moment(d.dueDate, moment.ISO_8601));
            })
            .attr('y1', 0)
            .attr('y2', height)
            .attr('opacity', 0.3)
            .on('mouseover', function(d){
              d3.select(this)
              // .attr('style', 'stroke:rgb(0,100,0);stroke-width:6;')
              .attr('opacity',1);

              setAssignmentToolTipPosition({
                x: d3.event.pageX,
                y: d3.event.pageY
                // x: d3.event.clientX,
                // y: d3.event.clientY
              });

              scope.$apply(function () {
                  scope.assignmentInfo = {
                    label: d.title,
                    date: moment(d.dueDate, moment.ISO_8601),
                    events: d.category.title
                  };
              });

            })
            .on('mouseout', function(d){
              d3.select(this)
              // .attr('style', 'stroke:rgb(0,100,0);stroke-width:2;')
              .attr('opacity',0.3);

              scope.$apply(function () {
                  scope.assignmentInfo = undefined;
              });

            });  
            
            $('#pulse-data').trigger('chart-render-finish');
        };

        function alignTables(callback) {
          $timeout(function(){
            var fPlot = floatingHeaderTable.find('.timeline-heading');
            var dPlot = dataTable.find('.timeline-heading');

            fPlot.removeAttr('style');
            dPlot.removeAttr('style');

            var hw = fPlot.width();
            var dw = dPlot.width();
            var w = hw < dw ? hw : dw;

            fPlot.width(w);
            dPlot.width(w);

            callback();
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
            .attr('cx', function (d, index) {
              var placement = timeScale(moment(d.date));
              return placement;
            });



          d3.selectAll("svg .assignment-marker")
            .transition()
            .duration(750)
            .attr('x1', function(d){
              return timeScale(moment(d.dueDate, moment.ISO_8601));
            })
            .attr('x2', function(d){
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
        $('.pulse-charts').on('chart-render-finish', function(){
          $('.hide-until-ready').fadeTo('slow', 1);
        });

        scope.$on('draw-chart', function(){
          alignTables(drawChart);
        });

        scope.$on('draw-assignments', drawAssignments);
        $(window).resize(handleResize);
      }
    };
  }]);

})(angular);