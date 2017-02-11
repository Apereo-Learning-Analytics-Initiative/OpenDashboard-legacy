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
    'PulseApiService',
    function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, EnrollmentDataService, UserDataService, EventService, LineItemDataService, pulseDataService, PulseApiService){
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
    $scope.coursesMaxEvents = 0;
    $scope.maxEvents = 0;
    $scope.emailstudent = {};
    $scope.studentFilters = {};
    $scope.studentFilters.list = [];

    // $scope.coursesStartEnd = {
    //   start: '2016-10-30',
    //   end: '2016-11-13'
    // };

    $scope.orderByField = 'label';
    $scope.reverseSort = false;
    $scope.assignmentOverlay = true;
    $scope.gradeFilter = false;
    $scope.gradeFilterScore = 25;
    $scope.submissionFilter = false;
    $scope.submissionFilterScore = 6;
    $scope.daysSinceLoginFilter = false;
    $scope.daysSinceLoginFilterCount = 0;
    var currentDataList = [];

    $scope.maxGrade = 100;
    $scope.maxRisk = 100;
    $scope.maxActivity = 0;
    $scope.appHasRiskData = false;
    $scope.appHasGradeData = false;
    $scope.appHasMissingSubmissionData = false;
    $scope.riskOverlay = true;
    var riskTypeCount = 4;
    var riskColorClasses = [
      {
        'threshold':[$scope.maxRisk + 1, $scope.maxRisk/riskTypeCount*3],
        'classname': 'high-risk'
      },
      {
        'threshold':[$scope.maxRisk/riskTypeCount*3, $scope.maxRisk/riskTypeCount*2],
        'classname': 'medium-risk'
      },
      {
        'threshold':[$scope.maxRisk/riskTypeCount*2, $scope.maxRisk/riskTypeCount],
        'classname': 'medium-risk'
      },
      {
        'threshold':[$scope.maxRisk/riskTypeCount, 0],
        'classname': 'no-risk'
      }
    ];

    $scope.activityOverlay = true;
    var activityTypeCount = 4;
    var activityColorClasses = [];
    function buildActivityColorThreshold() {
      activityColorClasses = [
        {
          'threshold':[$scope.currentCourse.studentActivityMax + 1, $scope.currentCourse.studentActivityMax/activityTypeCount*3],
          'classname': 'no-risk'
        },
        {
          'threshold':[$scope.currentCourse.studentActivityMax/activityTypeCount*3, $scope.currentCourse.studentActivityMax/activityTypeCount*2],
          'classname': 'medium-risk'
        },
        {
          'threshold':[$scope.currentCourse.studentActivityMax/activityTypeCount*2, $scope.currentCourse.studentActivityMax/activityTypeCount],
          'classname': 'medium-risk'
        },
        {
          'threshold':[$scope.currentCourse.studentActivityMax/activityTypeCount, 0],
          'classname': 'high-risk'
        }
      ];
    }

    $scope.gradeOverlay = true;
    var gradeTypeCount = 5;
    var gradeColorClasses = [
      {
        'threshold':[$scope.maxGrade + 1, ($scope.maxGrade/2)/gradeTypeCount*4+($scope.maxGrade/2)],
        'classname': 'high-grade'
      },
      {
        'threshold':[($scope.maxGrade/2)/gradeTypeCount*4+($scope.maxGrade/2), ($scope.maxGrade/2)/gradeTypeCount*3+($scope.maxGrade/2)],
        'classname': 'high-medium-grade'
      },
      {
        'threshold':[($scope.maxGrade/2)/gradeTypeCount*3+($scope.maxGrade/2), ($scope.maxGrade/2)/gradeTypeCount*2+($scope.maxGrade/2)],
        'classname': 'medium-grade'
      },
      {
        'threshold':[($scope.maxGrade/2)/gradeTypeCount*2+($scope.maxGrade/2), ($scope.maxGrade/2)/gradeTypeCount+($scope.maxGrade/2)],
        'classname': 'medium-low-grade'
      },
      {
        'threshold':[($scope.maxGrade/2)/gradeTypeCount+($scope.maxGrade/2), 0],
        'classname': 'low-grade'
      }
    ];
    
    $scope.emailList = [];

    $scope.colorCodeGrade = function(grade){
      if ($scope.gradeOverlay) {
        var colorclass;
        _.each(gradeColorClasses, function(g, i){
          if (grade < g.threshold[0] && grade >= g.threshold[1]) {
            colorclass = g.classname;
          }
        });

        return colorclass;
      } else {
        return "";
      }
    }

    $scope.colorCodeRisk = function(risk){
      if ($scope.riskOverlay) {
        var colorclass;
        // var riskDivided = 100/riskColorClasses.length;
        // console.log(Math.round(riskDivided/(riskDivided+)));
        _.each(riskColorClasses, function(r, i){
          if (risk < r.threshold[0] && risk >= r.threshold[1]) {
            colorclass = r.classname;
          }
        });
        return colorclass;
      } else {
        return "";
      }
    }

    $scope.colorCodeActivity = function(activity){
      if ($scope.activityOverlay) {
        var colorclass;
        _.each(activityColorClasses, function(r, i){
          if (activity < r.threshold[0] && activity >= r.threshold[1]) {
            colorclass = r.classname;
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
          var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;
          
            currentDataList = _.filter(tempDataList, function(o){
              return o.grade < $scope.gradeFilterScore;
          });
        }
      }
    }
    
    function filterByMissingSubmissions(nv, ov){
      if ($scope.currentCourse) {
        if ($scope.submissionFilter) {
          var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;
          
          currentDataList = _.filter(tempDataList, function(o){
            return o.missingSubmission >= $scope.submissionFilterScore;
          });
        }
      }
    }
    
    function filterLastLoginCount(nv, ov){
      if ($scope.currentCourse) {
        if ($scope.daysSinceLoginFilter) {
          var tempDataList = currentDataList.length ? currentDataList : $scope.currentCourse.students;
          
          currentDataList = _.filter(tempDataList, function(o){
            // console.log(o.daysSinceLogin >= $scope.daysSinceLoginFilterCount);
            return o.daysSinceLogin >= $scope.daysSinceLoginFilterCount;
          });
        }
      }
    }
    
    function runFilters() {
      if($scope.currentCourse) {
        filterByGrade();
        filterByMissingSubmissions();
        filterLastLoginCount();
        
        if(currentDataList.length) {
          $scope.datalist = currentDataList;
          currentDataList = [];
        } else {
          $scope.datalist = $scope.currentCourse.students;
        }
      }
    }
    
    $scope.$watchGroup(['submissionFilterScore', 'submissionFilter', 'gradeFilterScore', 'gradeFilter', 'daysSinceLoginFilter', 'daysSinceLoginFilterCount'], runFilters);
    // $scope.$watchGroup(['daysSinceLoginFilter'], runFilters);

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
      console.log('buildStudent');
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
      $scope.maxEvents = course.studentEventMax;
      buildActivityColorThreshold();
      runFilters();
    }

    function hideOptionalRiskData() {
      $scope.appHasRiskData = $scope.processedClasses[0].students[0].risk ? true : false;
    }

    function hideOptionalGradeData() {
      $scope.appHasGradeData = $scope.processedClasses[0].students[0].grade ? true : false;
    }

    function hideOptionalMissingSubmissionData() {
      $scope.appHasMissingSubmissionData = $scope.processedClasses[0].students[0].missingSubmission ? true : false;
    }

    function addMissingData() {
      // set app optional data configs
      $scope.config.hasRisk = $scope.config.hasRisk ? $scope.config.hasRisk : false;
      $scope.config.hasGrade = $scope.config.hasGrade ? $scope.config.hasGrade : false;
      $scope.config.hasEmail = $scope.config.hasEmail ? $scope.config.hasEmail : false;
      $scope.config.hasMissingSubmissions = $scope.config.hasMissingSubmissions ? $scope.config.hasMissingSubmissions : true;
      $scope.config.hasLastLogin = $scope.config.hasLastLogin ? $scope.config.hasLastLogin : true;

      // Grab max event count over all classes
      var maxEvents = 0;
      _.each($scope.processedClasses, function(c){
        _.each(c.events, function(event){
          if (maxEvents < event.eventCount) {
            maxEvents = event.eventCount;
          }
        });
      });
      $scope.coursesMaxEvents = maxEvents;

      // Set student activity class total maximum
      _.each($scope.processedClasses, function(c){
        var maxActivity = 0;
        _.each(c.students, function(s){
          if (maxActivity < s.activity) {
            maxActivity = s.activity;
          }
        });
        c.studentActivityMax = maxActivity;
      });

      // fake email data
      if ($scope.config.hasRisk && !$scope.processedClasses[0].students[0].email) {
        console.log('build fake email');
        _.each($scope.processedClasses, function(c){
          _.each(c.students, function(s){
            s.email = s.email ? s.email : s.givenName + '@' + s.givenName+s.familyName + '.com';
          });
        });
      }

      // fake risk data
      if ($scope.config.hasRisk && !$scope.processedClasses[0].students[0].risk) {
        console.log('build fake risk data');
        _.each($scope.processedClasses, function(c){
          _.each(c.students, function(s){
            s.risk = s.risk ? s.risk : Math.round(Math.random() * (100 - 0));
          });
        });
      }

      // fake risk data
      if ($scope.config.hasGrade && !$scope.processedClasses[0].students[0].grade) {
        console.log('build fake grade data');
        _.each($scope.processedClasses, function(c){
          _.each(c.students, function(s){
            s.grade = s.grade ? s.grade : Math.round(Math.random() * (100 - 0));
          });
        });
      }


      // fake missing submission data
      if ($scope.config.hasMissingSubmissions && $scope.processedClasses[0].students[0].missingSubmission !== 0) {
        console.log('build fake submission data');
        _.each($scope.processedClasses, function(c){
          _.each(c.students, function(s){
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

      if (currentUser) {
        PulseApiService
        .getPulseData(currentUser.tenant_id, currentUser.user_id)
        .then(function(data){
          console.log(data);

          $scope.config = _.clone(data);
          delete $scope.config.pulseClassDetails;
          $scope.processedClasses = data.pulseClassDetails;

          addMissingData();
          setCoursesTerm();
          // hideOptionalRiskData();
          // hideOptionalGradeData();
          // hideOptionalMissingSubmissionData();
          // calculateCoursesTerm();

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


        // pulseDataService.initData(currentUser).then(function(data){
        //     $scope.processedClasses = data;
        //     $scope.coursesMaxEvents = pulseDataService.coursesMaxEvents;

        //     hideOptionalRiskData();
        //     hideOptionalGradeData();
        //     hideOptionalMissingSubmissionData();
        //     calculateCoursesTerm();

        //     if ($state.params.studentId && $state.params.groupId) {
        //       $scope.listType = 'student';
        //       buildStudent($state.params.groupId, $state.params.studentId);
        //     } else if ($state.params.groupId) {
        //       $scope.listType = 'students';
        //       $scope.orderByField = 'lastName';
        //       buildStudentList($state.params.groupId);
        //     } else {
        //       $scope.maxEvents = pulseDataService.coursesMaxEvents;
        //       $scope.orderByField = 'label';
        //       $scope.datalist = $scope.processedClasses;
        //       $scope.listType = 'classes';
        //     }
        // });
      }
    }
    
    
// just demoing how to call pulseapi

// PulseApiService
// .getPulseData(currentUser.tenant_id, currentUser.user_id)
// .then(function(pulseData){
//   console.log('pulseData',pulseData);
// });

// end pulseapi


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
            var course = {
              id: c.sourcedId,
              label: c.title,
              startdate: null,
              enddate: null,
              studentEventMax: 0,
              events: [],
              students: [],
              assignments: c.assignments
            };

            _.each(c.metadata, function(value, key){
              if (key.indexOf('classStartDate') !== -1 ) {
                course.startdate = value;
              }
              if (key.indexOf('classEndDate') !== -1 ) {
                course.enddate = value;
              }
            });

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
                // Hides risk data if no risk data is available through the service
                risk: studentSrc.risk ? studentSrc.risk : false,
                //risk: Math.round(Math.random() * (100 - 0)),
                // Hides grade data if no grade data is available through the service
                grade: studentSrc.grade ? studentSrc.grade : false,
                // grade: Math.round(Math.random() * (100 - 0)),
                // activity: Math.round(Math.random() * (1000 - 100) + 100),
                activity: 0,
                daysSinceLogin: 0,
                // Hides missing submission data if no missing submission data is available throug the service
                missingSubmission: studentSrc.missingSubmission ? studentSrc.missingSubmission : false,
                // missingSubmission: Math.round(Math.random() * 6),
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
              student.activity = student.events.length;
              var recentDate = student.events[0].date;

              _.each(student.events,function(evDate, key){
                if (moment(evDate.date) > moment(recentDate)) {
                  recentDate = evDate.date;
                }
              });

              var lastDate = moment(recentDate);
              var now = moment();
              var lastLogin = Math.round(moment.duration(now - lastDate).asDays());

              student.daysSinceLogin = lastLogin;
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

          /**
            * TODO: Move data loading to payload resolver
          */
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

          $('#pulse-data').css({'padding-top':$('.pulse-header').height() - $('#hidden-header').height() + ($('body').hasClass('isLTI') ? 0 : 9)});
          $('.zoom-actions').css({'width':$('#floating-header .timeline-heading').width() + 20, 'top':$('.pulse-header').height() / 2  + ($('body').hasClass('isLTI') ? 0 : 20)});
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
          setDates();
          alignTables(drawChart);
        });

        scope.$on('draw-assignments', drawAssignments);
        $(window).resize(handleResize);
      }
    };
  }]);

})(angular);