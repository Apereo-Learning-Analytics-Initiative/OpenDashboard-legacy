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
    function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, EnrollmentDataService, UserDataService, EventService, LineItemDataService){
      "use strict";

    // $scope._ = _;
    $scope.chartInitialized = false;
    $scope.listType = 'classes';
    $scope.datalist = [];
    $scope.coursesMaxEvents = 0;
    $scope.maxEvents = 0;
    $scope.emailstudent = {};
    $scope.coursesStartEnd = {
      start: '2016-08-30',
      end: '2016-12-13'
    };

    var processedClasses = [];
    var classes = [];
    var labels = [];
    var students = [];
    var currentCourse = null;
    var currentUser = null;
    var currentStudent = null;
    var coursesProcessed = false;

    currentUser = SessionService.getCurrentUser();

    $scope.orderByField = 'label';
    $scope.reverseSort = false;
    $scope.assignmentOverlay = true;

    function getStudentBySourcedId (id) {
      return _.find(students, function (student) {
        return student.sourcedId === id;
      });
    }

    function processData(c) {
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
        var studentSrc = getStudentBySourcedId(i);
        // build student object
        var student = {
          id: i,
          label: studentSrc.familyName + ', ' + studentSrc.givenName,
          firstName: studentSrc.givenName,
          lastName: studentSrc.familyName,
          risk: Math.round(Math.random() * (100 - 0)),
          grade: Math.round(Math.random() * (100 - 0)),
          activity: Math.round(Math.random() * (1000 - 100) + 100),
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

      course.students = _.sortBy(course.students, function (student) {
        return student.label;
      });

      // // add course object to classes array
      processedClasses.push(course);


      // console.log('processedClasses', processedClasses);
      $scope.coursesMaxEvents = maxEvents;
      $scope.maxEvents = $scope.coursesMaxEvents;
      // $scope.datalist = processedClasses;
      //$scope.$broadcast('draw-chart');

      if (coursesProcessed) {
        if ($state.params.studentId && $state.params.groupId) {
          $scope.listType = 'student';
          buildStudent($state.params.groupId, $state.params.studentId);
        } else if ($state.params.groupId) {
          $scope.listType = 'students';
          $scope.orderByField = 'lastName';
          buildStudentList($state.params.groupId);
        } else {
          $scope.maxEvents = $scope.coursesMaxEvents;
          $scope.orderByField = 'label';
          $scope.datalist = processedClasses;
          $scope.listType = 'classes';  
        }
      }
    }

    function buildStudent(cid, sid) {
      console.log('buildStudent');
      var course = _.filter(processedClasses, function(c){
        return c.id === cid;
      })[0];
      
      $scope.classes = processedClasses;
      $scope.currentCourse = course;

      $scope.maxEvents = course.studentEventMax;

      var student = _.filter(course.students, function(s){
        return s.id === sid;
      })[0];

      $scope.currentStudent = student;
      $scope.datalist = [student];
    }

    function buildStudentList(id) {
      console.log('buildStudentList');
      var course = _.filter(processedClasses, function(c){
        return c.id === id;
      })[0];
      
      $scope.classes = processedClasses;
      $scope.currentCourse = course;

      $scope.maxEvents = course.studentEventMax;
      $scope.datalist = course.students;
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

    $scope.drawChart = function(draw) {
      if (draw) {
        $scope.$emit('draw-chart');
      }
    }

    function init() {
      console.log('init');
      // $scope.$on('chart-change', handleChartChange);

      $rootScope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
        if (toState.name === "index.courselist" && toParams.groupId) {

          $scope.listType = 'students';
          $scope.orderByField = 'lastName';
          buildStudentList(toParams.groupId);
        } else if (toState.name === "index.courselist" && !toParams.groupId) {
          $scope.maxEvents = $scope.coursesMaxEvents;
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

        EnrollmentDataService.getEnrollmentsForUser(currentUser.tenant_id, currentUser.user_id)
          .then(function(enrollments){
            if (enrollments.isError) {
              $scope.errorData = {};
              $scope.errorData['userId'] = currentUser.user_id;
              $scope.errorData['errorCode'] = enrollments.errorCode;
              $scope.error = enrollments.errorCode;
            } else {
              labels = enrollments;
              var statCount = 1;

              // console.log(enrollments);

              _.each(labels, function (enrollment) {
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
                            students = response;
                          })
                          .finally(function () {
                            if (statCount === labels.length) {
                              coursesProcessed = true;
                            }
                            processData(enrollment.class);
                            statCount++;
                          });
                    });
              });
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
        link: function (scope, element, attr) {
          function checkFinished() {
            if (scope.$last === true) {
              $timeout(function () {
                console.log(attr.ngRepeatFinish);
                scope.$emit(attr.ngRepeatFinish);
              });
            }
          }
          // checkFinished();
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

        var headerHeight = $('.navbar').height();
        var courseStart = moment(scope.coursesStartEnd.start).startOf('week');
        var courseEnd = moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'));
        var weeks = Math.round(moment.duration(courseEnd - courseStart).asWeeks());

        var timeScale = d3.scaleTime();

        timeScale.domain([
            courseStart, 
            courseEnd
          ]);

        // generate x axis
        var xAxis = d3.axisBottom()
          .scale(timeScale)
          // .tickSize(3)
          .ticks(weeks)
          .tickFormat(d3.timeFormat('%m-%d'));


        function setAssignmentToolTipPosition (pos) {
          var posOffset = {
            y: pos.y - headerHeight,
            x: pos.x ,
          };

          $('.tool-tip-assignment-info').css({
            'top': posOffset.y,
            'left': posOffset.x,
          });
        }

        function setEventToolTipPosition (pos) {
          var posOffset = {
            y: pos.y - 80,
            x: pos.x - 0,
          };

          $('.tool-tip-event-info').css({
            'top': posOffset.y,
            'left': posOffset.x,
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
          var timelineHeader = $('#timeline-heading');
          var tlhHeight = timelineHeader.height();
          var tlhWidth = timelineHeader.width();
          if ($('#timeline-heading svg.timeline-svg').length) {
            var svgTimelineHeader = d3.select('#timeline-heading svg');
          } else {
            var svgTimelineHeader = d3.select('#timeline-heading').append('svg');

            timeScale.range([10, tlhWidth - 50]);

            svgTimelineHeader
            .attr("class", 'timeline-svg')
            .attr("height", tlhHeight)
            .attr("width", tlhWidth);
            var xLabel = svgTimelineHeader
              .append('g')
              .attr('class', 'xaxis')
              .attr('transform', 'translate(10, 10)')
              .call(xAxis);
          }



          if (scope.listType !== "classes") {
            if ($('#timeline-heading svg.overview-svg g').length) {
              var overviewPlot = d3.select('#timeline-heading svg.overview-svg g');

            } else {
              var svgTimeOverview = d3.select('#timeline-heading').insert("svg",":first-child");

              svgTimeOverview
              .attr("class", 'overview-svg')
              .attr("height", tlhHeight)
              .attr("width", tlhWidth);

              var overviewPlot = svgTimeOverview.append('g')
                .attr('transform','translate(0,'+tlhHeight/2+')')
                .attr('class','plot');
            }

            drawPlots(overviewPlot, scope.currentCourse);
          }
        }

        function drawChart(obj) {
          $('.zoom-actions').width($('#timeline-heading').width() + 40)

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
            .attr("width", width);

            // row plot group
            
            var plots = !svg.select('g').empty() ? svg.select('g') : svg.append('g');

            plots
              .attr('transform','translate(0,'+height/2+')')
              .attr('class','plot');
            
            // dot
            drawPlots(plots, o);            
          });

          if (scope.listType !== 'classes') {
            drawAssignments();  
          } else {
            $('#pulse-wrapper').trigger('chart-render-finish');
          }
          
          scope.chartInitialized = true;

        }

        function drawAssignments() {
          var heading = $('#timeline-heading');
          var width = heading.width();
          var height = $('#pulse-table').height();
          var offset = heading.position();
          var overlay = $('#assignment-overlay');
          console.log('assignment height', height);
          overlay.css({
            'left': offset.left,
            'top': offset.top,
            'width': width,
            'height': height
          });

          var assignments = d3.select('#assignment-overlay svg')
            .attr('class', 'assignments-overlay')
            .attr("height", height)
            .attr("width", width);

          assignments.selectAll('line').remove();

          assignments
            .selectAll('line')
            .data(scope.currentCourse.assignments)
            .enter()
            .append('line')
            .attr('class', function (d, index) {
              var placement = timeScale(moment(d.dueDate, moment.ISO_8601));
              var classname = "assignment-marker " + d.category.title;

              // if (placement >= padding.left && placement <= padding.left + plotWidth) {
                
              // } else {
              //   classname = "assignment-marker hide " + d.category.title;
              // }

              return classname;
            })

            // .attr('style', 'stroke:rgb(0,100,0);stroke-width:2;')
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
                    date: moment(d.date),
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
            
            $('#pulse-wrapper').trigger('chart-render-finish');
        }

        $('#pulse-wrapper').on('chart-render-finish', function(){
          $('#pulse-wrapper').fadeTo('slow', 1);
        });

        scope.$on('draw-chart', drawChart);
        scope.$on('draw-assignments', drawAssignments);
        // $(window).resize(redrawChart);
      }
    };
  }]);

})(angular);