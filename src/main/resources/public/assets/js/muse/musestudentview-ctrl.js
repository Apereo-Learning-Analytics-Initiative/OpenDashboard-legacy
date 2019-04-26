(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('museStudentViewController',[
    '$scope',
    '$rootScope',
    '$http',
    '$q',
    '$timeout',
    '$state',
    'SessionService',
    'museApiService',
    function($scope, $rootScope, $http, $q, $timeout, $state,SessionService, museApiService){
      "use strict";

      var currentUser = SessionService.getCurrentUser();

      if (currentUser) {  	
    	  var data = museApiService
    	  .getCourseStudent(currentUser.tenant_id, currentUser.user_id, $state.params.courseId, $state.params.studentId)
    	  .then(function (data){
    		  data.statistics = JSON.parse(data.enrollment.metadata["http://unicon.net/vocabulary/v1/enrollmentStatistics"]);
    		  $scope.CourseStudent = data;
    		  
    	    		
    		// Start of activity by week
    		  
    		var activity_data = [];
    		var tmpObj;
			var wk;
			
			for(var i = 0; i < 15; i++){
				wk = i + 1;
				tmpObj = { "week": "week " + wk.toString(), "Student Login Count": data.statistics.weeklyActivityLogin[i]
						 , "Course Login Average": data.statistics.weeklyActivityLoginClassAvg[i]
						 , "Student Content Read Count": data.statistics.weeklyActivityContentRead[i]
			    		 , "Course Content Read Average": data.statistics.weeklyActivityContentReadClassAvg[i]
						 , "Student Lesson View Count": data.statistics.weeklyActivityLessonView[i]		
			    		 , "Course Lesson View Average": data.statistics.weeklyActivityLessonViewClassAvg[i]
						 , "Student Assessment Attempt Count": data.statistics.weeklyActivityAssessmentAttempt[i]
			    		 , "Course Assessment Attempt Average": data.statistics.weeklyActivityAssessmentAttemptClassAvg[i]
						 , "Student Assessment Submit Count": data.statistics.weeklyActivityAssessmentSubmit[i]
			    		 , "Course Assessment Submit Average": data.statistics.weeklyActivityAssessmentSubmitClassAvg[i]
						 , "Student Forums Post Count": data.statistics.weeklyActivityForumsPost[i]
			    		 , "Course Forums Post Average": data.statistics.weeklyActivityForumsPostClassAvg[i]
						 , "Student Forums Read Count": data.statistics.weeklyActivityForumsRead[i] 
			    		 , "Course Forums Read Average": data.statistics.weeklyActivityForumsReadClassAvg[i]
						 , "Student Other Activity Count": data.statistics.weeklyActivityOtherActivity[i]
			    		 , "Course Other Activity Average": data.statistics.weeklyActivityOtherActivityClassAvg[i]}
				
				activity_data.push(tmpObj);
			}
		    	

	    	$scope.activityData = activity_data;
	    	
    	    //format data
    	    var activityNames = d3.keys(activity_data[0]).filter(function(key) { return key !== "week"; });

    	    activity_data.forEach(function(d) {
    	    	d.activities = activityNames.map(function(name) { return {name: name, value: +d[name]}; });
    	    });
	    	
	    	
	    	//remove any tooltips
	    	d3.selectAll(".d3tooltip").remove();
	    	
	    	//start of bar chart
	    	var parentWidthActivity = d3.select('.d3_student_activity').node().getBoundingClientRect().width;
	    	var parentWidthComparison = d3.select('.d3_studentclass_activity').node().getBoundingClientRect().width;
	    	
	    	//resizing
	    	window.addEventListener("resize", function(){
	    		try{
	    			//check if in view/not null before resizing -- listener is on window, so all pages
	    			if(d3.select('.d3_student_activity')._groups[0][0]){
		    	    	d3.select('.d3_student_activity svg').remove();
		    	    	d3.select('.d3_studentclass_activity svg').remove();
		    	    	d3.selectAll('.d3tooltip').remove();
		    	    	
		    	    	parentWidthActivity = d3.select('.d3_student_activity').node().getBoundingClientRect().width;
		    	    	parentWidthComparison = d3.select('.d3_studentclass_activity').node().getBoundingClientRect().width;
		    	    	
		    	    	makeStudentActivityChart();
		    	    	makeComparisonChart();
	    			}
	    		}catch(err){
    	    		console.log(err);
    	    	}
    	    });
	    	
	    	var makeStudentActivityChart = function() {
	    		
			    	var margin_chart = {top: 20, right: 60, bottom: 30, left: 40},
					width_chart = parentWidthActivity - margin_chart.left - margin_chart.right,
					height_chart = 440 - margin_chart.top - margin_chart.bottom;
			    	
		
			    	//create svg space
			    	var svg_chart=d3.select(".d3_student_activity")
						.append("svg")
						.attr("width", width_chart + margin_chart.left + margin_chart.right)
						.attr("height", height_chart + margin_chart.top + margin_chart.bottom)
						.attr("class", "svg_space")
						
					var g = svg_chart.append("g")
							.attr("transform", "translate(" + margin_chart.left + "," + margin_chart.top + ")");
		
			    	//make x and y axis
			    	var x0 = d3.scaleBand()
		    	        .rangeRound([0, width_chart])
		    	        .paddingInner(0.3);
		    	    var x1 = d3.scaleBand()
		    	        .padding(0.05);
		    	    var y0 = d3.scaleLinear()
		    	        .rangeRound([height_chart, 0]);
		    	    
		
		    	    //colors
		    	    var z0 = d3.scaleOrdinal()
		    	    	.range(["rgba(45,77,255,0.8)", "rgba(45,77,255,0.4)", "rgba(255,196,45,0.8)" 
		    		           ,"rgba(255,196,45,0.4)", "rgba(255,83,45,0.8)", "rgba(255,83,45,0.4)"
							   ,"rgba(194, 27, 255, 0.8)", "rgba(194, 27, 255, 0.4)", "rgba(84, 255, 27, 0.8)"
							   ,"rgba(84, 255, 27, 0.4)", "rgba(255, 27, 76, 0.8)", "rgba(255, 27, 76, 0.4)"
							   ,"rgba(21, 255, 239, 0.8)", "rgba(21, 255, 239, 0.4)", "rgba(136, 8, 255, 0.8)"						   
		    		           ,"rgba(136, 8, 255, 0.4)"]);    
		
		    	    //attach data to x and y axis
		    	    x0.domain(activity_data.map(function(d){ return d["week"] }));
		    	    x1.domain(activityNames).range([0, x0.bandwidth()]);
		    	    y0.domain([0, d3.max(activity_data, function(d){ return d3.max(d.activities, function(d){ return d.value + 1; }); })]);
		    	   
		    	    
		    	    //attach x and y axis to svg
		    	    g.append("g")
			         .attr("class", "xaxis")
			         .attr("transform", "translate(0," + height_chart + ")")
			         .call(d3.axisBottom(x0));
			    
		    	    g.append("g")
			         .attr("class", "yaxis")
			         .call(d3.axisLeft(y0).ticks(null, "s"));
		    	    
		    	    
		    	    //gridlines in x axis function
		    	    function make_x_gridlines() {		
		    	        return d3.axisBottom(x0).ticks(5).tickSizeOuter(0);
		    	    }
		
		    	    //gridlines in y axis function
		    	    function make_y_gridlines() {		
		    	        return d3.axisLeft(y0).ticks(5);
		    	    }
		    	    
		    	    
		    	  //define tooltips div
				  var tooltipStudent = d3.select("body")
					  .append("div")	
					  .attr("class", "d3tooltip")				
					  .style("opacity", 0)
					  .style("position", "absolute")	
					  .style("text-align", "left")			
					  .style("min-width", "100px")			
					  .style("min-height", "20px")					
					  .style("padding", "2px")				
					  .style("font", "11px sans-serif")	
					  .style("background", "lightsteelblue")
					  .style("border", "0px")
					  .style("border-radius", "8px")		
					  .style("pointer-events", "none");
		    	    
		    	
		    	    //attach data to svg
		    	    var areas = g.append("g")
		    	      .selectAll("g")
		    	      .data(activity_data)
		    	      .enter().append("g")
		    	        .attr("transform", function(d) {return "translate(" + x0(d["week"]) + ",0)"; })
		    	        .on("mousemove", function(d){
							tooltipStudent.transition()
					        	.duration(50)
					        	.style("opacity", .9);
							
							//keep tooltip from going out of view on right side
							var tooltipStudentWidth = tooltipStudent.node().getBoundingClientRect().width;
							var tooltipStudentHeight = tooltipStudent.node().getBoundingClientRect().height;
							var xpos = d3.event.pageX > parentWidthActivity - tooltipStudentWidth ? d3.event.pageX - tooltipStudentWidth : d3.event.pageX;
							
							tooltipStudent.html(
									"<span style=\"text-transform:capitalize;\">" + d.week + "</span></br>"
									
									+ "<div style='display: inline-block; padding: 5px;'>"
										+ "<span>" + "&emsp;&emsp;<u>Activity</u>" + "</span>" + "</br>"
										
										+ "<span style=\"background-color:rgba(45,77,255,0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(45,77,255,0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Course Login:" + "</br>"
										
										+ "<span style=\"background-color:rgba(255,196,45,0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(255,196,45,0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Content Read:" + "</br>"
										
										+ "<span style=\"background-color:rgba(255,83,45,0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(255,83,45,0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Lesson View:" + "</br>"
										
										+ "<span style=\"background-color:rgba(194, 27, 255, 0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(194, 27, 255, 0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Assessment Attempt:" + "</br>"
										
										+ "<span style=\"background-color:rgba(84, 255, 27, 0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(84, 255, 27, 0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Assessment Submit:" + "</br>"
										
										+ "<span style=\"background-color:rgba(255, 27, 76, 0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(255, 27, 76, 0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Forums Post:" + "</br>"
										
										+ "<span style=\"background-color:rgba(21, 255, 239, 0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(21, 255, 239, 0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Forums Read:" + "</br>"
										
										+ "<span style=\"background-color:rgba(166, 8, 255, 0.8);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "<span style=\"background-color:rgba(166, 8, 255, 0.4);" +
										  "display: block; left: 0; float: left;" +
										  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
										  "\"></span>"
										+ "Other Activity:" + "</br>"
										
									+ "</div>"
									
									+ "<div style='display: inline-block; text-align: center; padding: 5px;'>"
										+ "<span>" + "<u>Student</u>" + "</span>" + "</br>"
										+ d.activities[0].value + "</br>"
										+ d.activities[2].value + "</br>"
										+ d.activities[4].value + "</br>"
										+ d.activities[6].value + "</br>"
										+ d.activities[8].value + "</br>"
										+ d.activities[10].value + "</br>"
										+ d.activities[12].value + "</br>"
										+ d.activities[14].value + "</br>"
									+ "</div>"
									
									
									+ "<div style='display: inline-block; text-align: center; padding: 5px;'>"
										+ "<span>" + "<u>Class</u>" + "</span>" + "</br>"
										+ d.activities[1].value + "</br>"
										+ d.activities[3].value + "</br>"
										+ d.activities[5].value + "</br>"
										+ d.activities[7].value + "</br>"
										+ d.activities[9].value + "</br>"
										+ d.activities[11].value + "</br>"
										+ d.activities[13].value + "</br>"
										+  d.activities[15].value+ "</br>"
									+ "</div>"
							)
					        	.style("left", (xpos) + "px")		
			    			    .style("top", (d3.event.pageY - tooltipStudentHeight) + "px");
							
							d3.select(this).style('stroke', 'black');
					    })
					    .on("mouseout", function(d) {
					        	  tooltipStudent.transition()
					        	  		.duration(500)
					        	  		.style("opacity", 0);
					        	  
					        	  d3.select(this).style('stroke', 'none');
					    });
						
						
		    	      var rects = areas.selectAll("rect")
		    	      .data(function(d) {return d.activities})
		    	      .enter().append("rect")
		    	        .attr("x", function(d) { return x1(d.name); })
		    	        .attr("y", function(d) { return y0(d.value); })
		    	        .attr("width", x1.bandwidth())
		    	        .attr("height", function(d) { return height_chart - y0(d.value); })
		    	        .style("fill", function(d){ return z0(d.name); })
						
		
		    	    
			    	  //add the X gridlines
				    	var xgrid = g.append("g")			
				    	    .attr("class", "gridx")
				    	    .attr("transform", "translate(" + (x0.bandwidth() * (44/77)) + "," + height_chart + ")")
				    	    .attr("stroke", "#777")
						    .attr("stroke-opacity", 0.2)
				    	    .call(make_x_gridlines()
				    	          .tickSize(-height_chart)
				    	          .tickFormat("")
				    	  );
			
				    	 //add the Y gridlines
				    	 var ygrid = g.append("g")			
				    	     .attr("class", "gridy")
				    	     .attr("stroke", "#777")
						     .attr("stroke-opacity", 0.2)
				    	     .call(make_y_gridlines()
				    	          .tickSize(-width_chart)
				    	          .tickFormat("")
				    	 );
				    	 
				    	 //remove stroke on path
				    	 xgrid.select("path")
				    	 	.attr("stroke-width", "0");
    	  }
    	  
    	  makeStudentActivityChart();
    	  //End of activity by week
	    	
  		  
  		  // Start of Average Student Activity Vs Class
	      
		    	 
		    	 //reformat data
		    	 var comparison_data = [
		    				
		    				{"label": "Course Login"
		    				, "Student Login Count": data.statistics.weeklyActivityTotalLogin
		    				, "Course Login Average": data.statistics.weeklyActivityLoginClassCount
		    				, "activities": [
		    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalLogin},
		    						{name: "Activity of Class", value: data.statistics.weeklyActivityLoginClassCount}
		    				   ]
		    				},
		    				
		    				{"label": "Content Read"
		    				, "Student Content Read Count": data.statistics.weeklyActivityTotalContentRead
		    				, "Course Content Read Average": data.statistics.weeklyActivityContentReadClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalContentRead},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityContentReadClassCount}
	    					  ]
		    				},
		    				
		    				{"label": "Lesson View"
		    				, "Student Lesson View Count": data.statistics.weeklyActivityTotalLessonView
		    				, "Course Lesson View Average": data.statistics.weeklyActivityLessonViewClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalLessonView},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityLessonViewClassCount}
	    				      ]
		    				},
		    				
		    				{"label": "Assessment Attempt"
		    				, "Student Assessment Attempt Count": data.statistics.weeklyActivityTotalAssessmentAttempt
		    				, "Course Assessment Attempt Average": data.statistics.weeklyActivityAssessmentAttemptClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalAssessmentAttempt},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityAssessmentAttemptClassCount}
	    				      ]
		    				},
		    				
		    				{"label": "Assessment Submit"
		    				, "Student Assessment Submit Count": data.statistics.weeklyActivityTotalAssessmentSubmit
		    				, "Course Assessment Submit Average": data.statistics.weeklyActivityAssessmentSubmitClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalAssessmentSubmit},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityAssessmentSubmitClassCount}
	    				      ]
		    				},
		    				
		    				{"label": "Forums Post"
		    				, "Student Forums Post Count": data.statistics.weeklyActivityTotalForumsPost
		    				, "Course Forums Post Average": data.statistics.weeklyActivityForumsPostClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalForumsPost},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityForumsPostClassCount}
	    				      ]
		    				},
		    				
		    				{"label": "Forums Read"
		    				, "Student Forums Read Count": data.statistics.weeklyActivityTotalForumsRead
		    				, "Course Forums Read Average": data.statistics.weeklyActivityForumsReadClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalForumsRead},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityForumsReadClassCount}
	    				      ]
		    				},
		    				
		    				{"label": "Other Activity"
		    				, "Student Other Activity Count": data.statistics.weeklyActivityTotalOtherActivity
		    				, "Course Other Activity Average": data.statistics.weeklyActivityOtherActivityClassCount
		    				, "activities": [
	    						{name: "Activity of Student", value: data.statistics.weeklyActivityTotalOtherActivity},
	    						{name: "Activity of Class", value: data.statistics.weeklyActivityOtherActivityClassCount}
	    				      ]
		    				}	
		    			];
		    	 
		    	 		$scope.comparisonData = comparison_data;
		    	 
		    	 		var comparisonLabels = ["Activity of Student", "Activity of Class"];
		    	 
		    	 		//start of bar chart
		    	 		
		    	 		var makeComparisonChart = function(){
			    			   	var margin_comparison = {top: 20, right: 60, bottom: 30, left: 40},
			    							width_comparison = parentWidthComparison - margin_comparison.left - margin_comparison.right,
			    							height_comparison = 438 - margin_comparison.top - margin_comparison.bottom;
			    			    	    	
		
				    	    	//create svg space
				    	    	var svg_comparison=d3.select(".d3_studentclass_activity")
									.append("svg")
									.attr("width", width_comparison + margin_comparison.left + margin_comparison.right)
									.attr("height", height_comparison + margin_comparison.top + margin_comparison.bottom)
									.attr("class", "svg_space2");
									
								var gcomp = svg_comparison.append("g")
										.attr("transform", "translate(" + margin_comparison.left + "," + margin_comparison.top + ")");
		
				    	    	//make x and y axis
				    	    	var x0comp = d3.scaleBand()
					    	        .rangeRound([0, width_comparison])
					    	        .paddingInner(0.3);
					    	    var x1comp = d3.scaleBand()
					    	        .padding(0.05);
					    	    var y0comp = d3.scaleLinear()
					    	        .rangeRound([height_comparison, 0]);
									
				 
					    	    //colors
					    	    var z0comp = d3.scaleOrdinal()
					    	    	.range(["rgba(255, 231, 108, 0.8)", "rgba(255, 108, 157, 0.8)"]);
					    	    
					  
					    	    //attach data to x and y axis
					    	    x0comp.domain(comparison_data.map(function(d){ return d["label"] }));
					    	    x1comp.domain(comparisonLabels).range([0, x0comp.bandwidth()]);
					    	    y0comp.domain([0, d3.max(comparison_data, function(d){ return d3.max(d.activities, function(d){ return d.value + 1; }); })]);
					    	   
					    	    
					    	    //gridlines in x axis function
					    	    function make_x_gridlinescomp() {		
					    	        return d3.axisBottom(x0comp).ticks(5).tickSizeOuter(0);
					    	    }
		
					    	    //gridlines in y axis function
					    	    function make_y_gridlinescomp() {		
					    	        return d3.axisLeft(y0comp).ticks(5);
					    	    }
					    	
					    	    
					    	    //attach x and y axis to svg
					    	    gcomp.append("g")
				    	         .attr("class", "xaxis")
				    	         .attr("transform", "translate(0," + height_comparison + ")")
				    	         .call(d3.axisBottom(x0comp));
				    	    
					    	    gcomp.append("g")
				    	         .attr("class", "yaxis")
				    	         .call(d3.axisLeft(y0comp).ticks(null, "s"));
					    	    
					    	    
					    	  //define tooltips div
				    			var tooltipComparison = d3.select("body")
				    					.append("div")	
				    					.attr("class", "d3tooltip")				
				    					.style("opacity", 0)
				    					.style("position", "absolute")	
				    					.style("text-align", "left")			
				    					.style("min-width", "100px")			
				    					.style("min-height", "20px")				
				    					.style("padding", "2px")				
				    					.style("font", "12px sans-serif")	
				    					.style("background", "lightsteelblue")
				    					.style("border", "0px")
				    					.style("border-radius", "8px")		
										.style("pointer-events", "none");
					    	
					    	    //attach data to svg
					    	    var rectAreasComp = gcomp.append("g")
					    	      .selectAll("g")
					    	      .data(comparison_data)
					    	      .enter().append("g")
					    	        .attr("transform", function(d) {return "translate(" + x0comp(d["label"]) + ",0)"; })
					    	      .on("mousemove", function(d){
									    tooltipComparison.transition()
			    			        	    .duration(200)
			    			        	    .style("opacity", .9);
									    
									  //keep tooltip from going out of view on right side
									    var tooltipComparisonWidth = tooltipComparison.node().getBoundingClientRect().width;
										var tooltipComparisonHeight = tooltipComparison.node().getBoundingClientRect().height;
										var xposcomp = d3.event.pageX > parentWidthActivity - tooltipComparisonWidth ? d3.event.pageX - tooltipComparisonWidth : d3.event.pageX;
									    
									    tooltipComparison.html(
									    		
											"<span style=\"text-transform:capitalize;\">" + d.label + "</span></br>"
											
										   +"<span style=\"background-color:rgba(255, 231, 108, 0.8);" +
											  "display: block; left: 0; float: left;" +
											  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
											  "\"></span>" + d.activities[0].name + ": " + d.activities[0].value + "</br>"
											  
										   + "<span style=\"background-color:rgba(255, 108, 157, 0.8);" +
											  "display: block; left: 0; float: left;" +
											  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
											  "\"></span>" + d.activities[1].name + ": " + d.activities[1].value
											  
									    )
			    			        	    .style("left", (xposcomp) + "px")		
			    			        	    .style("top", (d3.event.pageY - tooltipComparisonHeight) + "px");	
									
									    d3.select(this).style('stroke', 'black');
			    			        })
			    			        .on("mouseout", function(d) {
			    			        	tooltipComparison.transition()
			    			        	    .duration(500)
			    			        	    .style("opacity", 0);
			    			        	  
			    			        	d3.select(this).style('stroke', 'none');
			    			    });
									
									
				    	        var rectsComp = rectAreasComp.selectAll("rect")
				    	          .data(function(d) {return d.activities})
				    	          .enter().append("rect")
				    	            .attr("x", function(d) { return x1comp(d.name); })
				    	            .attr("y", function(d) {return y0comp(d.value); })
				    	            .attr("width", x1comp.bandwidth())
				    	            .attr("height", function(d) { return height_comparison - y0comp(d.value); })
				    	            .style("fill", function(d){ return z0comp(d.name); })
								    
				    	    
						    	  //add the X gridlines
							    	var xgridcomp = gcomp.append("g")			
							    	    .attr("class", "gridxcomp")
							    	    .attr("transform", "translate(" + (x0comp.bandwidth() * (75/148)) + "," + height_comparison + ")")
							    	    .attr("stroke", "#777")
					    			    .attr("stroke-opacity", 0.2)
							    	    .call(make_x_gridlinescomp()
							    	          .tickSize(-height_comparison)
							    	          .tickFormat("")
							    	  );
						
							    	 //add the Y gridlines
							    	 var ygridcomp = gcomp.append("g")			
							    	     .attr("class", "gridycomp")
							    	     .attr("stroke", "#777")
					    			     .attr("stroke-opacity", 0.2)
							    	     .call(make_y_gridlinescomp()
							    	          .tickSize(-width_comparison)
							    	          .tickFormat("")
							    	 );
							    	 
							    	 //remove stroke on path
							    	 xgridcomp.select("path")
							    	 	.attr("stroke-width", "0");
    	  }
    	  
    	  makeComparisonChart();

  		  // End of Average Student Activity Vs Class
	
    	  });
      } 
    }])
})(angular);  