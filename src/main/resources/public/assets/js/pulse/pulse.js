(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('pulseController',[
    '$scope',
    '$http',
    'SessionService',
    'EnrollmentDataService',
    'EventService',
    function($scope, $http, SessionService, EnrollmentDataService, EventService){
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

        console.log(c);

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
      $scope.$broadcast('draw-chart');
    }

    function buildStudentList(id) {
      var maxEvents = 0;

      var course = _.filter(processedClasses, function(c){
        return c.id === id;
      })[0];
      
      $scope.maxEvents = course.studentEventMax;
      $scope.datalist = course.students;

      $scope.$broadcast('draw-chart');
    }

    function handleChartChange(event, data) {
      if ($scope.listType === 'classes') {
        // clicked a class name on the left of the chart
        // set the type to students
        $scope.listType = 'students';
        buildStudentList(data.id);
      }
    }

    function getClassData() {
      var classdatauri = './data/classdata.json';
      var classlabeluri = './data/classtitles.json';

      $http.get(classdatauri).then(function(cdata){
        $http.get(classlabeluri).then(function(clabels){
          classes = cdata.data;
          labels = clabels.data;
          processData();
        });
      });
    }

    function init() {
      //getClassData();
      $scope.$on('chart-change', handleChartChange);
    }

    init();
  }
  ])
  .directive('pulse', function() {
    return {
      replace: true,
      scope: {
        dateRange: '=',
        list: '=',
        chartInfo: '=',
        // maxEvents: '=maxEvents'
      },
      controller: 'pulseController',
      templateUrl: 'assets/js/pulse/pulse.html',
      link: function (scope, element, attrs) {
        var maxEvents = 0;

        function drawChart() {
          // var maxEvents = obj.maxEvents;
          var plots = {};
          // var weeks = moment.duration(moment(scope.dateRange.start) + moment(scope.dateRange.end)).asWeeks();
          var weeks = Math.ceil(moment.duration(moment(scope.coursesStartEnd.end) - moment(scope.coursesStartEnd.start)).asWeeks());

          var plotContainer = $('#pulse-chart');
          var padding = {
            top: 50,
            right: 50,
            bottom: 10,
            left: 250,
            line: 2,
          };
          var lineHeight = 30;

          var height = scope.datalist.length * (lineHeight + padding.line) + (padding.top + padding.bottom);
          var width = plotContainer.width();
          plotContainer.height(height);
          var weekCount = 100/weeks.length;
          var weekWidth = (width - padding.left - padding.right) / weeks;
          var chartOffset = $('#pulse-chart').offset();

          $('#pulse-chart svg').remove();

          var svg = d3.select('#pulse-chart').append('svg');
          var timeScale;
          var xAxis;
          var oddEven = 'odd';

          $('.tool-tip-info').css({
            'top': padding.top - 20,
            'left': padding.left,
          });

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
          svg
          .attr("height", height)
          .attr("width", width);

          // time scale
          timeScale = d3
            .scaleTime()
            .domain([
              moment(scope.coursesStartEnd.start).startOf('week'), 
              moment(scope.coursesStartEnd.end).startOf('week').add(moment.duration(1, 'week'))
            ])
            .range([padding.left, plotContainer.width() - padding.right]);

          // generate x axis
          xAxis = d3.axisBottom()
            .scale(timeScale)
            .tickSize(7, 0)
            .tickFormat(d3.timeFormat('%m-%d'));

          // list text
          var yLabel = svg
            .append('g')
            .attr('class', 'axis')
            .attr('transform', 'translate(0, 10)')
            .call(xAxis);

          // list group
          var list = svg
            .append('g')
            .attr('class', 'yaxis')
            .attr('transform', 'translate(0, 0)');

          _.each(scope.datalist, function(o, i){
            // position of the row
            var linePosition = (i * lineHeight) + padding.top;
            
            // row group
            var row = list
              .append('g')
              .attr('class', 'list')
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
              .attr('class', 'listlabel')
              // .attr('id', function(d) {
              //   console.log(o);
              //   return o.id;
              // })
              .text(o.label)
              .on('click', function () {
                scope.$emit('chart-change', {
                  id: o.id
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
              .attr('r', function (d, i) {
                console.log(scope.maxEvents);
                return d.eventCount*100/scope.maxEvents/10+1;
              })
              .attr('cx', function (d, i) {
                return timeScale(moment(d.date));
              })
              .attr('class', 'dot')
              .style('fill', function (d) {
                return '#0087a7';
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
              });

            oddEven = oddEven == 'odd' ? 'even' : 'odd';
          });
        }

        scope.$on('draw-chart', drawChart);
      }
    };
  });
})(angular);