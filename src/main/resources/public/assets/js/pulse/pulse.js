(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('pulseController',[
    '$scope',
    '$http',
    '$q',
    '$timeout',
    '$state',
    'SessionService',
    'EnrollmentDataService',
    'UserDataService',
    'EventService',
    function($scope, $http, $q, $timeout, $state, SessionService, EnrollmentDataService, UserDataService, EventService){
      "use strict";

    // $scope._ = _;
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

    currentUser = SessionService.getCurrentUser();

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
            var statCount = 0;

            console.log(enrollments);

            _.each(labels, function (enrollment) {
              //classes.push(enrollment.class);
              
              EventService.getEventStatisticsForClass(currentUser.tenant_id, enrollment.class.sourcedId)
                  .then(function (statistics) {
                      enrollment.class.statistics = statistics;
                      statCount++;

                      var keys = _.keys(statistics.eventCountGroupedByDateAndStudent);
                      var queue = [];

                       _.each(keys, function (studentId) {
                        queue.push(UserDataService.getUser(currentUser.tenant_id, studentId));
                      });

                      return $q
                        .all(queue)
                        .then(function (response) {
                          console.log(response);
                          students = response;
                        })
                        .finally(function () {
                          processData(enrollment.class);
                        });
                  });
            });
          }   
      });
    }

    function getStudentBySourcedId (id) {
      return _.find(students, function (student) {
        return student.sourcedId === id;
      });
    }

    function processData(c) {
      var maxEvents = 0;
      console.log(classes);
      //_.each(classes, function(c){
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
            label: getStudentBySourcedId(i).familyName + ', ' + getStudentBySourcedId(i).givenName + ' : ' + i,
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
      //});

      console.log('processedClasses', processedClasses);
      $scope.coursesMaxEvents = maxEvents;
      $scope.maxEvents = $scope.coursesMaxEvents;
      $scope.datalist = processedClasses;
      //$scope.$broadcast('draw-chart');

      if ($state.params.studentId && $state.params.groupId) {
        $scope.listType = 'student';
        buildStudent($state.params.groupId, $state.params.studentId);
      } else if ($state.params.groupId) {
        $scope.listType = 'students';
        buildStudentList($state.params.groupId);
      } else {
        $scope.maxEvents = $scope.coursesMaxEvents;
        $scope.datalist = processedClasses;
        $scope.listType = 'classes';  
        $scope.$broadcast('draw-chart');
      }
    }

    function buildStudent(cid, sid) {
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


      $scope.$broadcast('draw-chart');
    }

    function buildStudentList(id) {
      var course = _.filter(processedClasses, function(c){
        return c.id === id;
      })[0];
      
      $scope.classes = processedClasses;
      $scope.currentCourse = course;

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
      console.log(data);
      console.log($state.params.groupId);
      // Go to course list 
      //$state.go('index.courselist', { groupId: null } );
      if (data.type === 'course') {
        // Go to specific course with list of students
        $state.go('index.courselist', { groupId: data.id }, {notify: false});
        // $state.go('index.courselist.empty', { groupId: $state.params.groupId, studentId: data.id }, {notify: false} );
        $scope.listType = 'students';
        buildStudentList(data.id);

      } else if (data.type === 'courselist') {
        // Go to specific student
        $state.go('index.courselist', { groupId: $state.params.groupId, studentId: data.id }, {notify: false} );
        // $state.go('index.courselist.empty', { groupId: $state.params.groupId, studentId: data.id }, {notify: false} );
        $scope.maxEvents = $scope.coursesMaxEvents;
        $scope.datalist = processedClasses;
        $scope.listType = 'classes';  
        $scope.$broadcast('draw-chart');

      } else if (data.type === 'student') {
        // Go to specific student
        $state.go('index.courselist.studentView', { groupId: $state.params.groupId, studentId: data.id }, {reload: false, inherit: true, notify: true} );
        $scope.listType = 'student';  
        buildStudent($scope.currentCourse.id, data.id);
      }
    }

    function init() {
      $scope.$on('chart-change', handleChartChange);
    }

    init();
  }
  ])







  .directive('pulse', [
    '$timeout',
    function($timeout) {
    return {
      controller: 'pulseController',
      templateUrl: 'assets/js/pulse/pulse.html',
      link: function (scope, element, attrs) {
        var maxEvents = 0;


        function drawChart(obj) {
          // var maxEvents = obj.maxEvents;
          var plots = {};
          var zoomed = false;
          var courseStart = moment(scope.coursesStartEnd.start).startOf('week');
          var courseEnd = moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'));
          // moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week')) - moment(scope.coursesStartEnd.start).startOf('week')
          // var weeks = Math.round(moment.duration().asWeeks());
          var weeks = Math.round(moment.duration(courseEnd - courseStart).asWeeks());

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
              courseStart, 
              courseEnd
            ]);

          // generate x axis
          xAxis = d3.axisBottom()
            .scale(timeScale)
            // .tickSize(3)
            .ticks(weeks)
            .tickFormat(d3.timeFormat('%m-%d'));

          function resetZoom() {
            var t0 = svg.transition().duration(750);
            var t1 = svg.transition().duration(750);
            
            timeScale.domain([
                courseStart, 
                courseEnd
              ]);

            xAxis.ticks(weeks);

            t1.selectAll(".xaxis").call(xAxis);

            t0.selectAll('.dot')
            .attr('cx', function (d, index) {
              var placement = timeScale(moment(d.date));
              return placement;
            })
            .attr('opacity', function (d, index) {
              return 0.5;
            });

            zoomgroup
            .attr('class', 'zoom-icon')
            .on('click', null);
          }

          if (inited) {
            // already have a chart. Let's work with it.
            var svg = d3.select('#pulse-chart svg');
            $('#pulse-chart svg .yaxis .pulse-list-item').remove();

            var t1 = svg.transition().duration(750);
            t1.selectAll(".xaxis").call(xAxis);
            var zoomgroup = svg.select('.zoom-icon');

            resetZoom();

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


            // list text
            var xLabel = svg
              .append('g')
              .attr('class', 'xaxis')
              .attr('transform', 'translate(0, 10)')
              .call(xAxis);


            // zoom icon
            var zoomgroup = svg.append('g')
              .attr('class', 'zoom-icon')
              .attr('transform', 'translate(200, 10) scale(.03)')
              

            var circle = zoomgroup
              .append('circle')
              
              .attr('opacity', '1')
              .attr('r', 400)
              .attr('cx', 240)
              .attr('cy', 240)

            var mag = zoomgroup
              .append('path')
              .attr('d', 'M464.524,412.846l-97.929-97.925c23.6-34.068,35.406-72.04,35.406-113.917c0-27.218-5.284-53.249-15.852-78.087c-10.561-24.842-24.838-46.254-42.825-64.241c-17.987-17.987-39.396-32.264-64.233-42.826C254.246,5.285,228.217,0.003,200.999,0.003c-27.216,0-53.247,5.282-78.085,15.847C98.072,26.412,76.66,40.689,58.673,58.676c-17.989,17.987-32.264,39.403-42.827,64.241C5.282,147.758,0,173.786,0,201.004c0,27.216,5.282,53.238,15.846,78.083c10.562,24.838,24.838,46.247,42.827,64.241c17.987,17.986,39.403,32.257,64.241,42.825c24.841,10.563,50.869,15.844,78.085,15.844c41.879,0,79.852-11.807,113.922-35.405l97.929,97.641c6.852,7.231,15.406,10.849,25.693,10.849c10.089,0,18.699-3.566,25.838-10.705c7.139-7.138,10.704-15.748,10.704-25.837S471.567,419.889,464.524,412.846z M291.363,291.358c-25.029,25.033-55.148,37.549-90.364,37.549c-35.21,0-65.329-12.519-90.36-37.549c-25.031-25.029-37.546-55.144-37.546-90.36c0-35.21,12.518-65.334,37.546-90.36c25.026-25.032,55.15-37.546,90.36-37.546c35.212,0,65.331,12.519,90.364,37.546c25.033,25.026,37.548,55.15,37.548,90.36C328.911,236.214,316.392,266.329,291.363,291.358z')

            var minus = zoomgroup
              .append('path')
              .attr('d', 'M283.228,182.728h-164.45c-2.474,0-4.615,0.905-6.423,2.712c-1.809,1.809-2.712,3.949-2.712,6.424v18.271c0,2.475,0.903,4.617,2.712,6.424c1.809,1.809,3.946,2.713,6.423,2.713h164.454c2.478,0,4.612-0.905,6.427-2.713c1.804-1.807,2.703-3.949,2.703-6.424v-18.271c0-2.475-0.903-4.615-2.707-6.424C287.851,183.633,285.706,182.728,283.228,182.728z')

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
          function setToolTipPosition (pos) {
            var posOffset = {
              y: pos.y - chartOffset.top - 20,
              x: pos.x - chartOffset.left + 20,
            };

            $('.tool-tip-info').css({
              'top': posOffset.y,
              'left': posOffset.x,
            });
          }



          // set width n height
          _.each(scope.datalist, function(o, i){
            // position of the row
            var linePosition = (i * lineHeight) + padding.top;
            // row group
            var row = list
              .append('g')
              
              // .attr('style', "clip-path: url(#clip)")
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
                  o.lineType = 'student';
                  return 'pulse-list-item student';
                } else {
                  o.lineType = 'course';
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
              .attr('opacity', 0.5)
              .style('fill', function (d) {
                if (scope.listType === "students" && i > 0) {
                  return '#00a9a7';  
                } else {
                  return '#0087a7';  
                }
              })
              .on('mouseover', function (d, index, node) {
                d3.select(this).attr('opacity','1');
                console.log(d3.event);
                setToolTipPosition({
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
              .on('click', function(d){
                zoomed = true;
                // zoom
                var t0 = svg.transition().duration(750);
                var t1 = svg.transition().duration(750);

                var weeksBack = moment(d.date).startOf('week').subtract(moment.duration(2, 'week'));
                var weeksForward = moment(d.date).startOf('week').add(moment.duration(3, 'week'));
                
                timeScale.domain([
                    weeksBack, 
                    weeksForward
                  ]);

                xAxis.ticks(5);

                t1.selectAll(".xaxis").call(xAxis);

                t0.selectAll('.dot')
                .attr('cx', function (d, index) {
                  var placement = timeScale(moment(d.date));

                  if (placement >= padding.left && placement <= padding.left + plotWidth) {
                    
                  } else {
                    // placement = -999;
                  }

                  return placement;
                })
                .attr('opacity', function (d, index) {
                  var bounds = timeScale(moment(d.date));
                  var optacity = 0.5;

                  if (bounds >= padding.left && bounds <= padding.left + plotWidth) {
                    
                  } else {
                    // optacity = 0;
                  }
                  
                  return optacity;
                });

                zoomgroup
                .attr('class', 'zoom-icon active')
                .on('click', resetZoom);

              });


              // row stripe
              var textg = row.append('g');

              if ((i !== 0 && scope.listType === "students") || scope.listType === "student") {
                var email = row.append('g')
                    .attr('class', 'email-link')
                    .on('click', function(){
                      $timeout(function(){
                        scope.emailstudent.name = o.label;
                        $('#emailModal').modal('show');
                      });
                    });

                    email
                    .append('rect')
                    .attr('width', 500)
                    .attr('height', 500)
                    .attr('opacity', 0)
                    
                    email
                    .append('path')
                    .attr('d', 'M498.208,68.235c-8.945-8.947-19.701-13.418-32.261-13.418H45.682c-12.562,0-23.318,4.471-32.264,13.418C4.471,77.18,0,87.935,0,100.499v310.633c0,12.566,4.471,23.312,13.418,32.257c8.945,8.953,19.701,13.422,32.264,13.422h420.266c12.56,0,23.315-4.469,32.261-13.422c8.949-8.945,13.418-19.697,13.418-32.257V100.499C511.626,87.935,507.158,77.18,498.208,68.235z M475.078,411.125c0,2.475-0.903,4.616-2.714,6.424c-1.81,1.81-3.949,2.706-6.42,2.706H45.679c-2.474,0-4.616-0.896-6.423-2.706c-1.809-1.808-2.712-3.949-2.712-6.424V191.858c6.09,6.852,12.657,13.134,19.7,18.843c51.012,39.209,91.553,71.374,121.627,96.5c9.707,8.186,17.607,14.561,23.697,19.13c6.09,4.571,14.322,9.185,24.694,13.846c10.373,4.668,20.129,6.991,29.265,6.991h0.287h0.284c9.134,0,18.894-2.323,29.263-6.991c10.376-4.661,18.613-9.274,24.701-13.846c6.089-4.569,13.99-10.944,23.698-19.13c30.074-25.126,70.61-57.291,121.624-96.5c7.043-5.708,13.613-11.991,19.694-18.843V411.125L475.078,411.125z M475.078,107.92v3.14c0,11.229-4.421,23.745-13.271,37.543c-8.851,13.798-18.419,24.792-28.691,32.974c-36.74,28.936-74.897,59.101-114.495,90.506c-1.14,0.951-4.474,3.757-9.996,8.418c-5.514,4.668-9.894,8.241-13.131,10.712c-3.241,2.478-7.471,5.475-12.703,8.993c-5.236,3.518-10.041,6.14-14.418,7.851c-4.377,1.707-8.47,2.562-12.275,2.562h-0.284h-0.287c-3.806,0-7.895-0.855-12.275-2.562c-4.377-1.711-9.185-4.333-14.417-7.851c-5.231-3.519-9.467-6.516-12.703-8.993c-3.234-2.471-7.614-6.044-13.132-10.712c-5.52-4.661-8.854-7.467-9.995-8.418c-39.589-31.406-77.75-61.57-114.487-90.506c-27.981-22.076-41.969-49.106-41.969-81.083c0-2.472,0.903-4.615,2.712-6.421c1.809-1.809,3.949-2.714,6.423-2.714h420.266c1.52,0.855,2.854,1.093,3.997,0.715c1.143-0.385,1.998,0.331,2.566,2.138c0.571,1.809,1.095,2.664,1.57,2.57c0.477-0.096,0.764,1.093,0.859,3.571c0.089,2.473,0.137,3.718,0.137,3.718V107.92L475.078,107.92z')

              }

              var rect = textg.append('rect')
                  .attr('y', 0)
                  .attr('fill', '#000')
                  .attr('class', oddEven)
                  .attr('width', padding.left)
                  .attr('height',lineHeight);

              // row label
              var text = textg.append('text')
                .attr('alignment-baseline', 'central')
                .attr('y', lineHeight/2)
                .attr('x', 10)
                .attr('class', function(){
                  if ((i !== 0 && scope.listType === "students") || scope.listType === "student") {
                    return 'listlabel student-list-item';
                  } else {
                    return 'listlabel course-list-item';
                  }
                })
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
                            id: o.id,
                            type: o.lineType
                          });
                        });
                  });
                });

            oddEven = oddEven == 'odd' ? 'even' : 'odd';
          });
        }

        scope.$on('draw-chart', drawChart);
      }
    };
  }]);
})(angular);