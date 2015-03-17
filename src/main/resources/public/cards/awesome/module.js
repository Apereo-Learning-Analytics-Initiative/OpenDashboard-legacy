(function(angular) {
'use strict';

    var m_w = 123456789;
    var m_z = 987654321;
    var mask = 0xffffffff;

    // Takes any integer
    function seed(i) {
        m_w = i;
    }

    // Returns number between 0 (inclusive) and 1.0 (exclusive),
    // just like Math.random().
    function random()
    {
        m_z = (36969 * (m_z & 65535) + (m_z >> 16)) & mask;
        m_w = (18000 * (m_w & 65535) + (m_w >> 16)) & mask;
        var result = ((m_z << 16) + m_w) & mask;
        result /= 4294967296;
        return result + 0.5;
    }

    var versionTotal = 10;

angular
.module('od.cards.awesome', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('awesome',{
        title: 'Awesome Card',
        description: 'This is our awesome card.',
        imgUrl: 'http://m.img.brothersoft.com/iphone/1818/538995818_icon175x175.jpg',
        cardType: 'awesome',
        styleClasses: 'od-card col-xs-12',
	    config: [
            {field:'lap_url',fieldName:'LAP URL',fieldType:'url',required:false},
            {field:'lap_key',fieldName:'LAP Key',fieldType:'text',required:false},
            {field:'lap_secret',fieldName:'LAP Secret',fieldType:'text',required:false},
            {field:'url',fieldName:'OpenLRS URL',fieldType:'url',required:false},
            {field:'key',fieldName:'OpenLRS Key',fieldType:'text',required:false},
            {field:'secret',fieldName:'OpenLRS Secret',fieldType:'text',required:false}
	    ]
    });
 })
 .controller('AwesomeCardController', function($scope, $log, ContextService, RosterService, LearningAnalyticsProcessorService, EventService, OutcomesService, DemographicsService) {
	
	$scope.course = ContextService.getCourse();
	$scope.lti = ContextService.getInbound_LTI_Launch();

	if ($scope.lti.ext.ext_ims_lis_memberships_url && $scope.lti.ext.ext_ims_lis_memberships_id) {
		
		var basicLISData = {};
		basicLISData.ext_ims_lis_memberships_url = $scope.lti.ext.ext_ims_lis_memberships_url;
		basicLISData.ext_ims_lis_memberships_id = $scope.lti.ext.ext_ims_lis_memberships_id;
		
		var options = {};
		options.contextMappingId = $scope.contextMapping.id;
		options.dashboardId = $scope.activeDashboard.id;
		options.cardId = $scope.card.id;
		options.basicLISData = basicLISData;

        var drawAwesomeness = function(data) {
            // just to have some space around items.
            var margins = {
                "left": 40,
                "right": 30,
                "top": 30,
                "bottom": 30
            };

            var width = 500;
            var height = 500;

            // this will be our colour scale. An Ordinal scale.
            var color = d3.scale.category10();

            // we add the SVG component to the scatter-load div
            var svg = d3.select("#scatter-load").append("svg").attr("width", width).attr("height", height).append("g")
                .attr("transform", "translate(" + margins.left + "," + margins.top + ")");

            // this sets the scale that we're using for the X axis.
            // the domain define the min and max variables to show. In this case, it's the min and max prices of items.
            // this is made a compact piece of code due to d3.extent which gives back the max and min of the price variable within the dataset
            var x = d3.scale.linear()
                .domain([0, 220])
                // the range maps the domain to values from 0 to the width minus the left and right margins (used to space out the visualization)
                .range([0, width - margins.left - margins.right]);

            // this does the same as for the y axis but maps from the rating variable to the height to 0.
            var y = d3.scale.linear()
                .domain([0, 220])
                // Note that height goes first due to the weird SVG coordinate system
                .range([height - margins.top - margins.bottom, 0]);

            // add the tooltip area to the webpage
            var tooltip = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);

            // we add the axes SVG component. At this point, this is just a placeholder. The actual axis will be added in a bit
            svg.append("g").attr("class", "x axis").attr("transform", "translate(0," + y.range()[0] / 2 + ")");
            svg.append("g").attr("class", "y axis").attr("transform", "translate(" + x.range()[1] / 2 + ", 0)");

            // this is our X axis label. Nothing too special to see here.
            svg.append("text")
                .attr("fill", "#414241")
                .attr("text-anchor", "end")
                .attr("x", width - 50)
                .attr("y", height / 2)
                .text("Engagement");

            // this is our Y axis label. Nothing too special to see here.
            svg.append("text")
                .attr("fill", "#414241")
                .attr("text-anchor", "end")
                .attr("x", width / 2 - 15)
                .attr("y", -15)
                .text("Achievement");

            // this is the actual definition of our x and y axes. The orientation refers to where the labels appear - for the x axis, below or above the line, and for the y axis, left or right of the line. Tick padding refers to how much space between the tick and the label. There are other parameters too - see https://github.com/mbostock/d3/wiki/SVG-Axes for more information
            var xAxis = d3.svg.axis().scale(x).orient("bottom").tickPadding(2);
            var yAxis = d3.svg.axis().scale(y).orient("left").tickPadding(2);

            // this is where we select the axis we created a few lines earlier. See how we select the axis item. in our svg we appended a g element with a x/y and axis class. To pull that back up, we do this svg select, then 'call' the appropriate axis object for rendering.
            svg.selectAll("g.y.axis").call(yAxis);
            svg.selectAll("g.x.axis").call(xAxis);


            // now, we can get down to the data part, and drawing stuff. We are telling D3 that all nodes (g elements with class node) will have data attached to them. The 'key' we use (to let D3 know the uniqueness of items) will be the name. Not usually a great key, but fine for this example.
            var leaner = svg.selectAll("g.node").data(data, function (d) {
                return [d.learner.person.name_full, d.standard, d.version];
            });

            // we 'enter' the data, making the SVG group (to contain a circle and text) with a class node. This corresponds with what we told the data it should be above.

            var leanerGroup = leaner.enter().append("g").attr("class", "node")
                // this is how we set the position of the items. Translate is an incredibly useful function for rotating and positioning items
                .attr('transform', function (d) {
                    return "translate(" + x(d.events) + "," + y(d.grade) + ")";
                });

            // we add our first graphics element! A circle!
            leanerGroup.append("circle")
                .attr("r", 5)
                .attr("class", "dot")
                .style("fill", function (d) {
                    // remember the ordinal scales? We use the colors scale to get a colour for our manufacturer. Now each node will be coloured
                    // by who makes the chocolate.
                    return color(d.standard);
                })
                .select(function (dd, i) {
                    return (dd.version === 0) ? this : null;
                })
                .on("mouseover", function(d) {
                    tooltip.transition()
                        .duration(200)
                        .style("opacity", .9);
                    tooltip.html('<div class="tooltipDiv">' + d.learner.person.name_full + "<br>(" + d.standard + ")</div>")
                        .style("left", (d3.event.pageX + 25) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                    var name_full = d.learner.person.name_full;
                    // hide other learners (only version == 0)
                    svg.selectAll('.node')
                        .select(function (dd, i) {
                            return (dd.version === 0) ? this : null;
                        })
                        .style("opacity", 0);
                    // show historical data for this learner
                    for (var version = 0; version < versionTotal; ++version) {
                        svg.selectAll('.node')
                            .select(function (dd, i) {
                                return (name_full === dd.learner.person.name_full && version === dd.version) ? this : null;
                            })
                            .style("opacity", (versionTotal - version) / versionTotal);
                    }
                })
                .on("mouseout", function(d) {
                    tooltip.transition()
                        .duration(500)
                        .style("opacity", 0);
                    // show all learners back
                    svg.selectAll('.node')
                        .style("opacity", 1);
                    // hide historical version
                    svg.selectAll('.node')
                        .select(function(dd, i) { return (dd.version !== 0) ? this : null;})
                        .style("opacity", 0);
                });

            // hide historical version
            svg.selectAll('.node')
                .select(function(dd, i) { return (dd.version !== 0) ? this : null;})
                .style("opacity", 0);

            // order elements by z-index, so version == 0 is on top (latest in list)
            svg.selectAll('.node')
                .sort(function(a, b) { return b.version - a.version; });

            // now we add some text, so we can see what each item is.
            //leanerGroup.append("text")
            //    .style("text-anchor", "middle")
            //    .attr("dy", -10)
            //    .text(function (d) {
            //        // this shouldn't be a surprising statement.
            //        return d.learner.person.name_full;
            //    });

            // draw legend
            var legend = svg.selectAll(".legend")
                .data(color.domain())
                .enter().append("g")
                .attr("class", "legend")
                .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

            // draw legend colored rectangles
            legend.append("rect")
                .attr("x", width - 60)
                .attr("y", height - 100)
                .attr("width", 18)
                .attr("height", 18)
                .style("fill", color);

            // draw legend text
            legend.append("text")
                .attr("x", width - 65)
                .attr("y", height - 91)
                .attr("dy", ".35em")
                .style("text-anchor", "end")
                .text(function(d) { return d;})
        };


        var handleLAPResponse = function (riskResults) {
            _.forEach($scope.course.learners, function (learner) {
                var risk = _.find(riskResults, { 'alternativeId': learner.user_id });
                if (risk) {
                    learner.risk = risk.modelRiskConfidence;
                }
            });
        };

        var buildRosterUsageData = function(){
            seed(12345);
            var outliers = 0;
            _.forEach($scope.course.learners, function(learner){

                var standards = [];
                var effortScale = 5;
                if (outliers < 10){
                    effortScale = .75;
                }
                if (outliers < 5){
                    effortScale = 20;
                }
                for (var i = 0; i < 3; ++i) {
                    var grade = 0;
                    for (var k = i; k < $scope.outcomes.length; k += 3) {
                        var result = _.find($scope.outcomes[k].results, {'user_id': learner.user_id});
                        if (!result) continue; // no outcome for this learners & assignment
                        grade += result.score;
                    }
                    //var grade = Math.round(random() * 100);
                    //if (outliers < 10){
                    //    grade = Math.round(random() * 20);
                    //}
                    //if (outliers < 5){
                    //    grade = Math.round(random() * 20 + 80);
                    //}
                    var engagement = Math.round(grade / effortScale + random() * 5);
                    var historicalGrade = grade;
                    var historical = [];
                    var historicalVelocity = random() * 2 - 1;
                    for (var j = 0; j < versionTotal; ++j){
                        historicalVelocity += (Math.round(Math.random() * .25 -.125));
                        historicalVelocity = Math.min(2, Math.max(-2, historicalVelocity));
                        historicalGrade += historicalVelocity;
                        historicalGrade = Math.max(0, Math.min(220, historicalGrade));
                        var historicalEngagement = Math.round(historicalGrade / effortScale + random() * 2);
 
                        historical[j] = {events: historicalEngagement, grade: Math.round(historicalGrade)};
                    }

                    var standardName;
                    switch (i) {
                        case 0:
                            standardName = "Coding Ability";
                            break;
                        case 1:
                            standardName = "Effective Communication";
                            break;
                        case 2:
                            standardName = "Visual Ninja";
                            break;
                    }
                    standards[i] = {name: standardName, events: engagement, grade: grade, historical: historical};
                }
                learner.standards = standards;
                outliers++;

            });
            console.log($scope.course.learners);
        };

        //RosterService
        //    .getRoster(options, null)
        //    .then(
        //    function (rosterData) {
        //        if (rosterData) {
        //            //$scope.course.buildRoster(rosterData);
        //            EventService.getEvents($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
        //                .then(handleLRSResponse);
        //            LearningAnalyticsProcessorService.getResults($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
        //                .then(handleLAPResponse);
        //        }
        //    }
        //);

        var transformData = function() {
            var data = [];
            _.forEach($scope.course.learners, function(learner) {
                _.forEach(learner.standards, function(standard) {
                    data.push({
                        learner: learner,
                        standard: standard.name,
                        events: standard.events,
                        grade: standard.grade,
                        version: 0
                    });
                    var i = 1;
                    _.forEach(standard.historical, function (history) {
                        data.push({
                            learner: learner,
                            standard: standard.name,
                            events: history.events,
                            grade: history.grade,
                            version: i
                        });
                        i += 1;
                    });
                });
            });
            return data;
        };

        var fetchOutcomesAndProceed = function() {
            OutcomesService
                .getOutcomes(options,null)
                .then(
                function(outcomesData) {
                    $scope.outcomes = outcomesData;
                    console.log(outcomesData);

                    buildRosterUsageData();
                    var data = transformData();
                    console.log(data);
                    drawAwesomeness(data);
                }
            );
        };

        if ($scope.isStudent) {
            $scope.course.learners = [];
            $scope.course.learners.push(ContextService.getCurrentUser());
            fetchOutcomesAndProceed();
        } else {
            RosterService
                .getRoster(options, null) // pass null so the default implementation is used
                .then(
                function (rosterData) {
                    if (rosterData) {
                        $scope.course.buildRoster(rosterData);
                        $scope.course.learners = _.sortBy($scope.course.learners, function (learner) {
                            return learner.user_id;
                        });
                        fetchOutcomesAndProceed();
                    }
                }
            );
        }
		
		//DemographicsService
		//.getDemographics()
		//.then(
		//	function (demographicsData) {
		//		$scope.demographics = demographicsData;
		//	}
		//
		//);
        //
        //EventService
        //.getEvents($scope.contextMapping.id, $scope.activeDashboard.id, $scope.card.id)
        //.then(
        //    function(statements) {
        //        $scope.events = statements;
        //        _.forEach($scope.events, function (event) {
        //            // make them pretty (http://www.adlnet.gov/expapi/*):
        //            event.verb.id = event.verb.id.split('/').pop();
        //            event.object.definition.type = event.object.definition.type.split('/').pop();
        //        });
        //    }
        //);


    }
	else {
		$log.error('Card not configured for Roster');
		$scope.message = 'No supporting roster service available';
	}
});

})(angular);
