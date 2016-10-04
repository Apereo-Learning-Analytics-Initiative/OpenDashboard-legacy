(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('pulseController',[
    '$scope',
    '$http',
    '$timeout',
    '$state',
    'SessionService',
    'EnrollmentDataService',
    'EventService',
    function($scope, $http, $timeout, $state, SessionService, EnrollmentDataService, EventService){
      "use strict";

    // $scope._ = _;
    $scope.listType = 'classes';
    $scope.datalist = [];
    $scope.coursesMaxEvents = 0;
    $scope.maxEvents = 0;
    $scope.coursesStartEnd = {
      start: '2016-08-30',
      end: '2016-12-13'
    };


    var processedClasses = [];
    var classes = [];
    var labels = [];
    var currentUser = null;

    currentUser = SessionService.getCurrentUser();

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
            var statCount = 0;

            _.each(labels, function (enrollment) {
              classes.push(enrollment.class);
              EventService.getEventStatisticsForClass(currentUser.tenant_id, enrollment.class.sourcedId)
                  .then(function (statistics) {
                      enrollment.class.statistics = statistics;
                      statCount++;
                      if (statCount === labels.length) {
                        processData();
                      }
                  });
            });
          }   
      });
    }

    function processData() {
      var maxEvents = 0;
      _.each(classes, function(c){
        // filter class label
        // var l = _.filter(labels, function(label) { 
        //   return label.class.sourcedId === c.sourcedId;
        // })[0];

        // // build course object
        var course = {
          id: c.sourcedId,
          label: c.title,
          studentEventMax: 0,
          events: [],
          students: []
        };

        // // process events object for the class
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
          // build student object
          var student = {
            id: i,
            label: i,
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

        // // add course object to classes array
        processedClasses.push(course);
      });

      console.log('processedClasses', processedClasses);
      $scope.coursesMaxEvents = maxEvents;
      $scope.maxEvents = $scope.coursesMaxEvents;
      $scope.datalist = processedClasses;
      //$scope.$broadcast('draw-chart');

      if ($state.params.groupId) {
        $scope.listType = 'students';
        buildStudentList($state.params.groupId);
      } else {
        $scope.maxEvents = $scope.coursesMaxEvents;
        $scope.datalist = processedClasses;
        $scope.listType = 'classes';  
        $scope.$broadcast('draw-chart');
      }

      /*if ($scope.listType === 'classes') {
        // clicked a class name on the left of the chart
        // set the type to students
        
        $scope.listType = 'students';
        buildStudentList($state.params.groupId);

      } else if ($scope.listType === 'students') {
        // clicked a class name on the left of the chart
        // set the type to students
        
        $scope.maxEvents = $scope.coursesMaxEvents;
        $scope.datalist = processedClasses;
        $scope.listType = 'classes';  
        $scope.$broadcast('draw-chart');
      }*/
    }

    function buildStudentList(id) {
      var course = _.filter(processedClasses, function(c){
        return c.id === id;
      })[0];
      
      console.log(course);

      $scope.maxEvents = course.studentEventMax;
      $scope.datalist = course.students;
      if (!$scope.datalist[0].isClass) {
        $scope.datalist.unshift({
          isClass: true,
          id: course.id,
          label: course.label,
          events: course.events
        });        
      }

      $scope.$broadcast('draw-chart');
    }

    function handleChartChange(event, data) {
      // Go to course list 
      //$state.go('index.courselist', { groupId: null } );
      // Go to specific course with list of students
      $state.go('index.courselist', { groupId: data.id });
      // Go to specific student
      //$state.go('index.courselist.student', { studentId: data.id } );
    }

    function init() {
      $scope.$on('chart-change', handleChartChange);
    }

    init();
  }
  ])
  .directive('pulse', function() {
    return {
      controller: 'pulseController',
      templateUrl: 'assets/js/pulse/pulse.html',
      link: function (scope, element, attrs) {
        var maxEvents = 0;

        function drawChart(obj) {
          // var maxEvents = obj.maxEvents;
          var plots = {};
          // var weeks = moment.duration(moment(scope.dateRange.start) + moment(scope.dateRange.end)).asWeeks();
          var weeks = Math.round(moment.duration(moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week')) - moment(scope.coursesStartEnd.start).startOf('week')).asWeeks());

          var plotContainer = $('#pulse-chart');
          var padding = {
            top: 50,
            right: 30,
            bottom: 10,
            left: 250,
            line: 2,
          };
          var plotWidth = 950;
          var lineHeight = 30;
          var weekWidth = 75;

          var height = scope.datalist.length * (lineHeight + padding.line) + (padding.top + padding.bottom);
          // var width = plotContainer.width();
          var width = padding.left + padding.right + plotWidth;

          plotContainer.height(height);
          // var weekWidth = (width - padding.left - padding.right) / weeks;
          var chartOffset = $('#pulse-chart').offset();
          var timeScale;
          var xAxis;
          var oddEven = 'odd';
          var inited = $('#pulse-chart svg').length;


          // time scale
          timeScale = d3
            .scaleTime()
            .range([padding.left, plotWidth + padding.left]);

          timeScale.domain([
              moment(scope.coursesStartEnd.start).startOf('week'), 
              moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'))
            ]);

          if (inited) {
            // already have a chart. Let's work with it.
            var svg = d3.select('#pulse-chart svg');
            $('#pulse-chart svg .yaxis .pulse-list-item').remove();
          } else {
            // No chart yet, let's make one.
            var svg = d3.select('#pulse-chart').append('svg');

            svg
            .attr("height", height)
            .attr("width", width);

            $('.tool-tip-info').css({
              'top': padding.top - 20,
              'left': padding.left,
            });


            // generate x axis
            xAxis = d3.axisBottom()
              .scale(timeScale)
              // .tickSize(3)
              .ticks(weeks)
              .tickFormat(d3.timeFormat('%m-%d'));

            // list text
            var xLabel = svg
              .append('g')
              .attr('class', 'xaxis')
              .attr('transform', 'translate(0, 10)')
              .call(xAxis);


          }

          svg
          .attr("height", height)
          .attr("width", width);


          // list group
          var list = svg
            .append('g')
            .attr('class', 'yaxis')
            .attr('transform', 'translate(0, 0)');


          // set tooltip position based on cursor
          // function setToolTipPosition (pos) {
          //   var posOffset = {
          //     y: pos.y - chartOffset.top - 20,
          //     x: pos.x - chartOffset.left + 20,
          //   };

          //   $('.tool-tip-info').css({
          //     'top': posOffset.y,
          //     'left': posOffset.x,
          //   });
          // }



          // set width n height
          _.each(scope.datalist, function(o, i){
            // position of the row
            var linePosition = (i * lineHeight) + padding.top;
            // row group
            var row = list
              .append('g')
              .attr('id', function(d){
                if (scope.listType === "students" && i > 0) {
                  var id = "student-" + o.id;
                } else {
                  var id = "course-" + o.id;
                }
                return id;
              })
              .attr('class', function(d){
                if (scope.listType === "students" && i > 0) {
                  return 'pulse-list-item student';
                } else {
                  return 'pulse-list-item course';
                }
              })
              // .attr('y' (linePosition + padding.line));
              .attr('transform', 'translate(0, ' + (linePosition + (padding.line * i)) + ')');
            
            // row stripe
            var rect = row.append('rect')
                .attr('y', 0)
                .attr('class', oddEven)
                .attr('width',plotContainer.width())
                .attr('height',lineHeight);

            // row label
            var text = row.append('text')
              .attr('alignment-baseline', 'central')
              .attr('y', lineHeight/2)
              .attr('x', 10)
              .attr('class', function(){
                if (scope.listType === "students") {
                  return 'listlabel student-list-item';
                } else {
                  return 'listlabel course-list-item';
                }
              })
              // .attr('id', function(d) {
              //   console.log(o);
              //   return o.id;
              // })
              .text(o.label)
              .on('click', function (d) {
                var clickTarget = $(this).parents(".pulse-list-item");
                $('.pulse-list-item:not(#'+clickTarget.attr('id')+')').fadeOut(300, function() { 
                  $(this).remove(); 
                  
                  var top = padding.top;
                  d3.select('#'+clickTarget.attr('id'))
                    .transition()
                      .duration(750)
                      .attr("transform", "translate(0,"+ top +")")
                      .on("end", function(){
                        d3.select('#'+clickTarget.attr('id')+' rect')
                        .attr('class', 'odd');

                        scope.$emit('chart-change', {
                          id: o.id
                        });

                      });
                });
              });

            // row plot group
            var plots = row.append('g')
              .attr('transform','translate(0,'+lineHeight/2+')')
              .attr('class','plot');
            
            // dot
            plots
              .selectAll('circle')
              .data(o.events)
              .enter()
              .append('circle')
              // .attr('r', 2)
              .attr('r', function (d) {
                var count;
                if (scope.listType === "students" && i==0) {
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
              .style('fill', function (d) {
                if (scope.listType === "students" && i > 0) {
                  return '#00a9a7';  
                } else {
                  return '#0087a7';  
                }
              })
              .on('mouseover', function (d, index, node) {

                // setToolTipPosition({
                //   x: d3.event.clientX,
                //   y: d3.event.clientY
                // });
                scope.$apply(function () {
                    scope.chartInfo = {
                      date: moment(d.date),
                      events: d.eventCount
                    };
                });
              })
              .on('mouseout', function (d) {
                scope.$apply(function () {
                    scope.chartInfo = undefined;
                });
              })
              .on('click', function(d){
                var t0 = svg.transition().duration(750);
                var t1 = t0.transition();

                var weeksBack = moment(d.date).startOf('week').subtract(moment.duration(3, 'week'));
                var weeksForward = moment(d.date).startOf('week').add(moment.duration(2, 'week'));
                
                timeScale.domain([
                    weeksBack, 
                    weeksForward
                  ]);
                xAxis.ticks(5);
                t1.selectAll(".xaxis").call(xAxis);

                t0.selectAll('.dot').attr('cx', function (d) {
                  console.log(d);
                  return timeScale(moment(d.date));
                });

              });

            oddEven = oddEven == 'odd' ? 'even' : 'odd';
          });
        }

        scope.$on('draw-chart', drawChart);
      }
    };
  });
})(angular);