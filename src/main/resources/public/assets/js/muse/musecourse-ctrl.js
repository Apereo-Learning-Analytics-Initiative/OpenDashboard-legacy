(function(angular) {
  'use strict';

  angular.module('OpenDashboard')
  .controller('museCourseController',[
    '$scope',
    '$rootScope',
    '$http',
    '$q',
    '$timeout',
    '$state',
    'SessionService',
    'museApiService',
    function($scope, $rootScope, $http, $q, $timeout, $state, SessionService, museApiService){
    	"use strict";
      
		var currentUser = SessionService.getCurrentUser();
		
    	if (currentUser) {  
    		
    		// Global Variables
    		var currentWeek; // Week of semester
    		
    		
    	    var data = museApiService
    	    .getCourse(currentUser.tenant_id, currentUser.user_id, $state.params.courseId)
    	    .then(function (data){
    	    	
    	    	// Initialized variables.
    	    	var radius;
    	    	var email;
	       		var riskProb = 0.0;
	       		var grade = "-";
	       		var red = "rgba(230,0,0,0.5)";
    	    	var yellow = "rgba(255,255,0,0.5)";
    	    	var green = "rgba(34,139,34,0.5)";
    	    	var black = "rgba(0, 0, 0, 1.0)";
    	    	var isGrad = 0;
    	    	var bubbleColor = yellow;
       	    	var myDataSets = [];
       	    	var bubbleStudentIds = [];
       	    	var border = bubbleColor;
       	    	// Regular expression to check data model type.
       	    	var regCheck = RegExp('_BASE','g');
       	    	var dataFinalGrades = [
       	    		{grade: 'A', number: 0, color: '#006900', students: []},
       	    		{grade: 'A-', number: 0, color: '#007900', students: []},
       	    		{grade: 'B+', number: 0, color: '#009300', students: []},
       	    		{grade: 'B', number: 0, color: '#00b400', students: []},
       	    		{grade: 'B-', number: 0, color: '#00ec00', students: []},
       	    		{grade: 'C+', number: 0, color: '#bffe00', students: []},
       	    		{grade: 'C' ,number: 0, color: '#e5fe54', students: []},
       	    		{grade: 'C-', number: 0, color: '#fffe02', students: []},
       	    		{grade: 'D+', number: 0, color: '#fc534d', students: []},
       	    		{grade: 'D', number: 0, color: '#fc3438', students: []},
       	    		{grade: 'D-', number: 0, color: '#fc0000', students: []},
       	    		{grade: 'F', number: 0, color: '#a80000', students: []},
       	    		{grade: 'P', number: 0, color: '#71676e', students: []},
       	    		{grade: 'W', number: 0, color: '#a7b2a9', students: []},
       	    		{grade: '-', number: 0, color: '#435259', students: []}
				];
       	    	
       	    	var possibleGrades = ['A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'D-', 'F', 'P', 'W', '-'];
       	  
       	    	var dataPredictions = [
       	    		{ prediction: 'Not at Risk', number: 0, color: green, students: []},   
       	    		{ prediction: 'Indeterminate', number: 0, color: yellow, students: []},
       	    		{ prediction: 'At Risk', number: 0, color: red, students: []},
       	 		];
       	    	
     	       	for (var i = 0; i < data.enrollments.length; i++){
     	       		try{
     	       			
	     	       		if (data.enrollments[i].role != "teacher"){
	     	       			
	     	       			data.enrollments[i].studentStatistics = JSON.parse(data.enrollments[i].metadata["http://unicon.net/vocabulary/v1/enrollmentStatistics"]);
	     	       			riskProb = data.enrollments[i].studentStatistics.successProbability;
	     	       			radius = data.enrollments[i].studentStatistics.totalActivity;
	     	       			grade = data.enrollments[i].studentStatistics.finalGrade;
	     	       			isGrad =  data.enrollments[i].studentStatistics.graduateFlag;
	     	       			bubbleColor = yellow;    
	     	       			
	     	       			// Add intervention and accuracy variables
	     	       			data.enrollments[i].intervention = null;
	     	       			data.enrollments[i].accuracy = false;
	     	       			
	     	       			if(!possibleGrades.includes(grade)){grade = '-'}
	     	       			
	     	       			if (riskProb < 0.45){
	     	       				bubbleColor = red;
	     	       				dataPredictions[2].number++;
	     	       			    dataPredictions[2].students.push(data.enrollments[i].user.givenName + " " + data.enrollments[i].user.familyName);
	     	       			} else if (riskProb > 0.55) {
	     	       				bubbleColor = green;
	     	       				dataPredictions[0].number++;
	     	       			dataPredictions[0].students.push(data.enrollments[i].user.givenName + " " + data.enrollments[i].user.familyName);
	     	       			}else{
	     	       				dataPredictions[1].number++;
	     	       				dataPredictions[1].students.push(data.enrollments[i].user.givenName + " " + data.enrollments[i].user.familyName);
	     	       			}
	     	       			
	     	       			//tally up the final grade numbers and add student to list
	     	       			for(var j = 0; j < dataFinalGrades.length; j++){
	     	       				if(grade === dataFinalGrades[j].grade){
	     	       					dataFinalGrades[j].number++;
	     	       					dataFinalGrades[j].students.push(data.enrollments[i].user.givenName + " " + data.enrollments[i].user.familyName);
	     	       				}
	     	       			}
	     	       			
	     	       		    border = bubbleColor;
	     	       		    regCheck.lastIndex = 0; // reset index to start search from line start
	     	       			if (!regCheck.test(data.enrollments[i].studentStatistics.dataModelType)) {
	     	       				border = black;
	     	       			}
	     	       			
	     	       			myDataSets.push({
	     	       					label: data.enrollments[i].user.givenName + " " + data.enrollments[i].user.familyName
	     	       					, data: [{x: i + 1, y: riskProb, r: radius, finalGrade: grade, grad: isGrad}]
		    	                  	, backgroundColor: bubbleColor
		    	                  	, borderColor: border
		    	                  	, borderWidth: 1.5
	     	       			});
	     	       			bubbleStudentIds.push({
     	       					 courseId: data.enrollments[i].class.sourcedId
	     	       				,studentId: data.enrollments[i].user.sourcedId
	     	       			});
	     	       		}else{
	     	       			if(i < data.enrollments.length - 1){
	     	       				data.enrollments.push(data.enrollments.splice(i, 1)[0]);
		 	       				i--;
	     	       			}
		 	       		}
     	       		} catch(e){console.log(e);}
     	       	}
     	       	data.courseStatistics = JSON.parse(data.course.metadata["http://unicon.net/vocabulary/v1/classStatistics"]);
    	    	$scope.course = data;
    	    	$scope.enrollments = data.enrollments;
    	    	$scope.sortColumn = "studentStatistics.isAtRisk";
    	    	$scope.reverseSort = true;
       	    	$scope.bubbleStudentIds = bubbleStudentIds;
    	    	console.log($scope);
    	    	$scope.sortData = function (column) {
    	    		$scope.reverseSort = ($scope.sortColumn == column) ? !$scope.reverseSort : false;
    	    		$scope.sortColumn = column;
    	    	}
    	    	
    	    	$scope.getSortClass = function (column){
    	    		if ($scope.sortColumn == column){
    	    			return $scope.reverseSort ? 'arrow-down' : 'arrow-up'
    	    		}
    	    		
    	    		return '';
    	    	}
    	    	
    	    	// set global week variable
    	    	currentWeek = data.enrollments[0].studentStatistics.currentWeek;
    	    	
    	    	//remove any tooltips
    	    	d3.selectAll(".d3tooltip").remove();
    	    	
    			//start of d3 bubble chart
    	    		
    	    	    //resizing
    	    	    var parentWidthBubble = d3.select('.d3_bubbles').node().getBoundingClientRect().width;
    	    	    var parentWidthChart = d3.select('.d3_class_activity').node().getBoundingClientRect().width;
    	    	    var parentWidthDonut = d3.select('#muserisk').node().getBoundingClientRect().width;
    	    	    
    	    	    window.addEventListener("resize", function(){
    	    	    	try{
    	    	    		//check if in view/not null before resizing -- listener is on window, so all pages
    	    	    		if(d3.select('.d3_bubbles')._groups[0][0]){
		    	    	    	d3.select('.d3_bubbles svg').remove();
		    	    	    	d3.select('.d3_class_activity svg').remove();
		    	    	    	d3.select('.d3_donut svg').remove();
		    	    	    	d3.selectAll('.d3tooltip').remove();
		    	    	    	
		    	    	    	parentWidthBubble = d3.select('.d3_bubbles').node().getBoundingClientRect().width;
		    	    	    	parentWidthChart = d3.select('.d3_class_activity').node().getBoundingClientRect().width;
		    	    	    	parentWidthDonut = d3.select('#muserisk').node().getBoundingClientRect().width;
		    	    	    	
		    	    	    	makeBubbleChart();
		    	    	    	makeBarChart();
		    	    	    	makeDonut();
    	    	    		}
    	    	    	}catch(err){
    	    	    		console.log(err);
    	    	    	}
    	    	    });

    				var makeBubbleChart = function(){
    					try{
	    					var margin = {top: 30, right: 60, bottom: 30, left: 60},
	    					width = parentWidthBubble - margin.left - margin.right,
	    					height= 340 - margin.top - margin.bottom;

		    				/* 
		    				 * value accessor - returns the value to encode for a given data object.
		    				 * scale - maps value to a visual display encoding, such as a pixel position.
		    				 * map function - maps from data value to display value
		    				 * axis - sets up axis
		    				 */ 
		    				
		    				//setup x
		    				var xValue = function(d) {return d.data[0].x;}, // data -> value
		    					xScale = d3.scaleLinear().domain([0, data.enrollments.length]).range([0, width]), // value -> display
		    					xMap = function(d) {return xScale(xValue(d));}, // data -> display
		    					xAxis = d3.axisBottom().scale(xScale).ticks(data.enrollments.length).tickFormat(function(d){return d;});
		    					
		    				//setup y
		    				var yValue = function(d) {return d.data[0].y;}, // data -> value
		    					yScale = d3.scaleLinear().range([height, 0]), // value -> display
		    					yMap = function(d) {return yScale(yValue(d));}, // data -> display
		    					yAxis = d3.axisLeft().scale(yScale).tickFormat(function(d){return (d*100) + "%"});
		    				
		    				// setup fill color
		    				var cValue = function(d) { return d.backgroundColor;}
		    				
		    				// add the graph canvas
		    				var svg=d3.select(".d3_bubbles").append("svg")
		    							.attr("width", width + margin.left + margin.right)
		    							.attr("height", height + margin.top + margin.bottom)
		    							.style("overflow", "visible")
		    					    .append("g")
		    							.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		    				
		    				// don't want dots overlapping axis, so add in buffer to data domain
		    				xScale.domain([d3.min(myDataSets, xValue)-1, d3.max(myDataSets, xValue)+1]);
		    				  
		    				  
		    				//x-axis
		    				svg.append("g")
		    				 	  .attr("class", "x-axis")
		    				 	  .attr("transform", "translate(0," + height + ")")
		    				 	  .call(xAxis);
		    				 	  
		    				//x-axis label
		    			    svg.append("text")
		    				      .attr("class", "label")
		    				      .attr("transform", "translate(" + (width/2) + " ," + 
		    				    	   (height + margin.top + 10) + ")")
		    				      .style("font", "12px sans-serif")
		    				      .style("text-anchor", "middle")
		    				      .text("Student Sequence Number 1 - " + data.courseStatistics.enrollment);
		    				
		    				// y-axis
		    				svg.append("g")
		    				      .attr("class", "y-axis")
		    				      .call(yAxis);
		    	
		    				      
		    				//y-axis label
		    				svg.append("text")
		    				      .attr("class", "label")
		    				      .attr("transform", "rotate(-90)")
		    				      .attr("x", 0 - (height / 2))
		    				      .attr("y", 0 - (margin.left-15))
		    				      .style("font", "12px sans-serif")
		    				      .style("text-anchor", "middle")
		    				      .text("Probability of Success");
		    				
		    				//define tooltips div
		    				var tooltipBubbles = d3.select("body")
		    						.append("div")	
		    							.attr("class", "d3tooltip")				
		    							.style("opacity", 0)
			    						.style("position", "absolute")	
			    						.style("text-align", "left")			
			    						.style("min-width", "10px")			
			    						.style("min-height", "20px")				
			    						.style("padding", "2px")				
			    						.style("font", "12px sans-serif")	
			    						.style("background", "lightsteelblue")
			    						.style("border", "0px")
			    						.style("border-radius", "8px")		
									    .style("pointer-events", "none");
		    				
		    				// draw bubbles
		    			    var bubbles = svg.selectAll(".bubble")	
		    			    	  .data(myDataSets).enter()
		    			    	  
		    			    var gradOverlay = bubbles.append("circle")
						          .attr("class", "bubble")
						          .attr("r", function(d){return d.data[0].r - .5;})
						          .attr("cx", xMap)
						          .attr("cy", yMap)
						          .attr("stroke", function(d){ return d.data[0].grad ? "#800080" : "none"})
						          .attr("stroke-width", 5)
						          .attr("stroke-miterlimit", 5)
						          .attr("stroke-dasharray", "1")
						          .style("fill", "none");
		    			          
		    			    	  
		    			    var circles = bubbles.append("circle")
		    			          .attr("class", "bubble")
		    			          .attr("r", function(d){return d.data[0].r;})
		    			          .attr("cx", xMap)
		    			          .attr("cy", yMap)
		    			          .attr("stroke", function(d){ return d.borderColor})
		    			          .attr("stroke-width", 1)
		    			          .style("fill", function(d){return d.backgroundColor;});
		    			    
			    			   
					      //add events
				          circles.on("click", function(d, i){
				        	  try{
				        		  d3.selectAll(".d3tooltip").style("opacity", 0);
				        		  
				        		  $state.go('index.museStudent',$scope.bubbleStudentIds[i]);
				        	  }catch(err){}})
				          .on("mousemove", function(d) {
				        	  tooltipBubbles.transition()
				        	  		.duration(50)
				        	  		.style("opacity", .9);
				        	  
				        	  var tooltipBubblesWidth = tooltipBubbles.node().getBoundingClientRect().width;
							  var tooltipBubblesHeight = tooltipBubbles.node().getBoundingClientRect().height;
							  var xposbubbles = d3.event.pageX > parentWidthBubble - tooltipBubblesWidth ? d3.event.pageX - tooltipBubblesWidth  : d3.event.pageX;
				        	  
							  tooltipBubbles.html(
				        			  "<span style=\"background-color:" + d.backgroundColor + "; " +
									  "display: block; left: 0; float: left; " +
									  "top: 0; width: 10px; height:10px; border-radius: 5px; " +
									  "\"></span>" + d.label  
				        	  )
				        	  		.style("left", (xposbubbles) + "px")		
				        	  		.style("top", (d3.event.pageY - tooltipBubblesHeight) + "px");	
				          })
				          .on("mouseout", function(d) {
				        	  tooltipBubbles.transition()
				        	  		.duration(500)
				        	  		.style("opacity", 0);
				          });
		    				      
		    			          
		    			    //add final grades
		    			    bubbles.append("text")
		    			          .attr("class", "finalGrades")
		    			          .attr('text-anchor', 'middle')
		    			          .attr("x", xMap)
		    			          .attr("y", yMap)
		    			          .attr('alignment-baseline', 'bottom')
		    			          .attr('cursor', 'default')
		    			          .style('font-size', function(d){ return d.data[0].r * 0.9 + 'px'})
		    			          .text(function(d){
		    			        	  return (d.data[0].finalGrade == "-" ? "" : d.data[0].finalGrade);
		    			          })
		    			          .on("click", function(d, i){
		    			        	  try{
		    			        		  tooltipBubbles.style("opacity", 0);
		    			        		  
		    			        		  $state.go('index.museStudent',$scope.bubbleStudentIds[i]);
		    			        	  }catch(err){}})
		    			          .on("mousemove", function(d, i) {
		    			        	  tooltipBubbles.transition()
					        	  	  	.duration(50)
					        	  		.style("opacity", .9);
		    			        	  
		    			        	  var tooltipBubblesTextWidth = tooltipBubbles.node().getBoundingClientRect().width;
		  							  var tooltipBubblesTextHeight = tooltipBubbles.node().getBoundingClientRect().height;
		  							  var xposBubblesText = d3.event.pageX > parentWidthBubble - tooltipBubblesTextWidth ? d3.event.pageX - tooltipBubblesTextWidth : d3.event.pageX;
		  							
		    			        	  tooltipBubbles.html(
		    			        			  "<span style=\"background-color:" + d.backgroundColor + "; " +
											  "display: block; left: 0; float: left; " +
											  "top: 0; width: 10px; height:10px; border-radius: 5px; " +
											  "\"></span>" + d.label
		    			        			  )
		    			        	  .style("left", (xposBubblesText) + "px")		
		    			        	  .style("top", (d3.event.pageY - tooltipBubblesTextHeight) + "px");
		    			          })
		    			          .on("mouseout", function(d) {
		    			        	  tooltipBubbles.transition()
					        	  		.duration(500)
					        	  		.style("opacity", 0);
		    			          });
		
		    			    //add x grid
		    			    svg.selectAll(".x-axis g line")
		    			    	  .attr("stroke", "#777")
		    			    	  .attr("stroke-opacity", 0.3)
		    			    	  .attr("y1", -height)
		    			    	  .attr("y2", 5);
		    			    
		    			    //add y grid
		    			    svg.selectAll(".y-axis g line")
		    			    	  .attr("stroke", "#777")
		    			    	  .attr("stroke-opacity", 0.3)
		    			    	  .attr("x1", width)
		    			    	  .attr("x2", -5);
		
    					}catch(err){console.log(err)}
    	            }        		
    				makeBubbleChart();
		    			//end of d3 bubble chart
    			    
    				
    				
    			//start of d3 pie chart
        		var g_donut = null;
        		var g_pie = null;
    			var g_donut_small = null;
    			var g_pie_small = null;
    				
    			var makeDonut = function(){
		    			    var studentList = "";
		    			    var parseStudents = function(students){
		    			    	studentList = "";
		    			    	for(var student in students){
		    			    		studentList += students[student] + "</br>"
		    			    	}
		    			    	return studentList;
		    			    }
		    			    
		    			    
		    			    var w=500,h=500, r = w - (w/3);
		    				 
		    				//pie generators
		    				var pie=d3.pie()
		    						.value(function(d){return d.number})
		    						.sort(null);
		
		    				//arc generators
		    				var arc_pie=d3.arc()
		    						.innerRadius(0)
		    						.outerRadius(r - 140);
		    						
		    				var arc_donut=d3.arc()
		    						.innerRadius(r - 140)
		    						.outerRadius(r - 110);
		    						
		    				var labelGradeArc=d3.arc()
		    						.outerRadius(r - 160)
		    						.innerRadius(r - 170);
		
		    				//define svg
		    				var dynamicMargin;
		    				
		    				var svg=d3.select(".d3_donut")
		    						  .append("svg")
		    						  	.attr("width", w)
		    						  	.attr("height", h)
		    						  	.style("margin-left", function(){
		    						  		dynamicMargin = (0.0000011978044099805527 * Math.pow(parentWidthDonut, 2)) + (0.4967881382171117 * parentWidthDonut) - (258.22514968877323)
		    						  		return dynamicMargin + "px";
		    						  	})
		    						  	.attr("class", "svg_space");
		  
		    					
		    				//define tooltips div
		    				var tooltipDonut = d3.select(".donut_key")
		    						.append("div")	
		    						.attr("class", "d3tooltip")				
		    						.style("opacity", 0)
		    						.style("position", "absolute")	
		    						.style("text-align", "center")			
		    						.style("min-width", "225px")			
		    						.style("min-height", "20px")				
		    						.style("padding", "2px")				
		    						.style("font", "12px sans-serif")	
		    						.style("background", "lightsteelblue")
		    						.style("border", "1px solid black")
		    						.style("border-radius", "8px")		
								    .style("pointer-events", "none")
								    .style(" margin-top", "200px")
								    .style(" margin-left", "200px");
		    				
		    				//append shapes to svg		
		    				var pie_shape=d3.select(".svg_space")
		    						.append("g")
		    						.attr('transform','translate('+(w/2)+','+(h/2)+')')
		    						.attr("class", "pie");
		    						
		    				var donut_shape=d3.select(".svg_space")
		    						.append("g")
		    						.attr('transform','translate('+(w/2)+','+(h/2)+')')
		    						.attr("class", "donut");
		
		    				//create arcs w/ enter function---append g elements(arcs)
		    				
		    				//arcs
		    				g_pie = pie_shape.selectAll(".arc1")
		    					.data(pie(dataFinalGrades))
		    					.enter()
		    					.append('g')
		    					.attr("class", "arc1")
		    					.on("click", function(d){
		    						g_donut.attr("display", "none");
		    						g_pie.attr("display", "none");
		    						d3.select(".d3_donut").style("display", "none");
		    						
		    						g_donut_small.attr("display", "block");
		    						g_pie_small.attr("display", "block");
								  
		    						
		    						d3.select(".background-scale-img")
									   .style("background-image", "url(\'assets/img/locales/chart_background.png\')");
		    					})
		    					.on("mouseover", function(d) {
		    						var gradeString = "Not Available";
		    						
		    						if(d.data.grade != '-'){
		    							gradeString = "of " + d.data.grade;
		    						}
		    						
		    						tooltipDonut.transition()
		    							   .duration(50)
		    							   .style("opacity", .9);
		    						tooltipDonut.html(	
		    								"Final Grade " + gradeString + ": " + d.data.number + " Student(s)</br></br>" 
		    								+ parseStudents(d.data.students)	
		    						)
		    						
		    						d3.select(this).style('stroke', 'black');
		    						d3.selectAll(".donut_text").style('stroke', 'none');
		    					})
		    					.on("mouseout", function(d) {
		    						tooltipDonut.transition()
		    						.duration(500)
		    						.style("opacity", 0);
		    						
		    						d3.select(this).style('stroke', 'none');
		    				});
		    					
		    				g_donut = donut_shape.selectAll(".arc2")
		    					.data(pie(dataPredictions))
		    					.enter()
		    					.append('g')
		    					.attr("class", "arc2")
		    					.on("click", function(d){
		    						g_donut.attr("display", "none");
		    						g_pie.attr("display", "none");
		    						d3.select(".d3_donut").style("display", "none");
		    						
		    						g_donut_small.attr("display", "block");
		    						g_pie_small.attr("display", "block");
		
		    						
		    						d3.select(".background-scale-img")
									   .style("background-image", "url(\'assets/img/locales/chart_background.png\')");
		    					})
		    					.on("mouseover", function(d) {
		    						tooltipDonut.transition()
		    							   .duration(50)
		    							   .style("opacity", .9);
		    						tooltipDonut.html(
		    								"Prediction of " + d.data.prediction + ": " + d.data.number + " Student(s)</br></br>" 
		    								+ parseStudents(d.data.students)
		    								)
		    						
		    						d3.select(this).style('stroke', 'black');
		    					})
		    					.on("mouseout", function(d) {
		    						tooltipDonut.transition()
		    						.duration(500)
		    						.style("opacity", 0);
		    						
		    						d3.select(this).style('stroke', 'none');
		    				});
		    				
						
		    				//append the path of the arc
		    				g_pie.append("path")
		    					  .attr("d", arc_pie)
		    					  .style("fill", function(d){return d.data.color; });
		    					  
		    				g_donut.append("path")
		    					  .attr("d", arc_donut)
		    					  .style("fill", function(d){return d.data.color; });
		    				
		    				
		    				//define final grade text
		    				g_pie.append("text")
		    					  .attr("transform", function(d){return "translate(" + labelGradeArc.centroid(d) + ")"; })
		    					  .attr("text-anchor", "middle")
		    					  .attr('cursor', 'default')
		    					  .attr("class", "donut_text")
		    					  .text(function(d, i){
		    						 if(dataFinalGrades[i].number == 0){
		    							 return "";
		    						 }else{
		    						  return d.data.grade == '-' ? "": d.data.grade;
		    						 }
		    					  });
    	        }
    			makeDonut();
    			
    			
    			
    			
    			
    			
    			//small donut
    			var makeSmallDonut = function(){
    				
    				var w=500,h=500, r = w - (w/3);
   				 
    				//pie generators
    				var pie=d3.pie()
    						.value(function(d){return d.number})
    						.sort(null);

    				//arc generators
    				var arc_pie_small=d3.arc()
							.innerRadius(0)
							.outerRadius(r - 290);
					
					var arc_donut_small=d3.arc()
							.innerRadius(r - 290)
							.outerRadius(r - 270);
    				
					//define svg
    				var svg_small=d3.select(".d3_donut_small")
					  		  .append("svg")
					  			.attr("width", w/3)
					  			.attr("height", h/3)
					  			.attr("class", "svg_space_small");
    				
    				//append shapes to svg
    				var pie_shape_small=d3.select(".svg_space_small")
							.append("g")
							.attr('transform','translate('+(w/6)+','+(h/7)+')')
							.attr("class", "pie");
					
					var donut_shape_small=d3.select(".svg_space_small")
							.append("g")
							.attr('transform','translate('+(w/6)+','+(h/7)+')')
							.attr("class", "donut");
					
					//arcs
					g_pie_small = pie_shape_small.selectAll(".arc1")
							.data(pie(dataFinalGrades))
							.enter()
							.append('g')
							.attr("class", "arc1")
							.on("click", function(d){
									g_donut_small.attr("display", "none");
									g_pie_small.attr("display", "none");
									
									g_donut.attr("display", "block");
									g_pie.attr("display", "block");
									d3.select(".d3_donut").style("display", "block");
		    						
		    						d3.select(".background-scale-img")
									   .style("background-image", "url(\'assets/img/locales/chart_background_large.png\')");
		    				})
							.on("mouseover", function(d) {
								d3.select(this).style('stroke', 'black');
							})
							.on("mouseout", function(d) {
								d3.select(this).style('stroke', 'none');
				   });
					
				g_donut_small = donut_shape_small.selectAll(".arc2")
							.data(pie(dataPredictions))
							.enter()
							.append('g')
							.attr("class", "arc2")
							.on("click", function(d){
									g_donut_small.attr("display", "none");
									g_pie_small.attr("display", "none");
									
									g_donut.attr("display", "block");
									g_pie.attr("display", "block");
									d3.select(".d3_donut").style("display", "block");
										
									d3.select(".background-scale-img")
									   .style("background-image", "url(\'assets/img/locales/chart_background_large.png\')");
			    			})
							.on("mouseover", function(d) {
								d3.select(this).style('stroke', 'black');
							})
							.on("mouseout", function(d) {
								d3.select(this).style('stroke', 'none');
				});
				
				//append the path of the arc
				g_pie_small.append("path")
					  .attr("d", arc_pie_small)
					  .style("fill", function(d){return d.data.color; });
				  
				g_donut_small.append("path")
					  .attr("d", arc_donut_small)
					  .style("fill", function(d){return d.data.color; });
					  
    				
    			}
    			
    			makeSmallDonut();
    			//end of d3 pie chart

    	    	
    	    	//set up chart data for the d3 bar chart
    			var chart_data = [];
    			var tmpObj;
    			var wk;
    			
    			for(var i = 0; i < 15; i++){
    				wk = i + 1;
    				tmpObj = {"week": "week " + wk.toString(), "Course Login Average": data.courseStatistics.loginCountAvg[i]
			   	    		 , "Content Read Average": data.courseStatistics.contentReadCountAvg[i]
			   	    		 , "Lesson View Average": data.courseStatistics.lessonViewCountAvg[i]
			   	    		 , "Assessment Attempt Average": data.courseStatistics.assessmentAttemptCountAvg[i]
			   	    		 , "Assessment Submit Average": data.courseStatistics.assessmentSubmitCountAvg[i]
			   	    		 , "Forums Post Average": data.courseStatistics.forumsPostCountAvg[i]
			   	    		 , "Forums Read Average": data.courseStatistics.forumsReadCountAvg[i]
			   	    		 , "Activity Average": data.courseStatistics.activityCountAvg[i]}
    				
    				chart_data.push(tmpObj);
    			}
   
    	    	
	    	    //format data
	    	    var activityNames = d3.keys(chart_data[0]).filter(function(key) { return key !== "week"; });

	    	    chart_data.forEach(function(d) {
	    	    	d.activities = activityNames.map(function(name) { return {name: name, value: +d[name]}; });
	    	    });
	
    	    	//start of bar chart
		    	    	var makeBarChart = function(){	

				    	    	var margin_chart = {top: 20, right: 60, bottom: 30, left: 40},
								width_chart = parentWidthChart - margin_chart.left - margin_chart.right,
								height_chart = 440 - margin_chart.top - margin_chart.bottom;
				    	    	
				
				    	    	//create svg space
				    	    	var svg_chart=d3.select(".d3_class_activity")
									.append("svg")
									.attr("width", width_chart + margin_chart.left + margin_chart.right)
									.attr("height", height_chart + margin_chart.top + margin_chart.bottom)
									.attr("class", "svg_space2")
													
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
					    	    var z = d3.scaleOrdinal()
					    	    	.range(["rgba(45,77,255,0.8)", "rgba(255,196,45,0.8)", "rgba(255,83,45,0.8)" 
					    		           ,"rgba(194,27,255,0.8)", "rgba(84,255,27,0.8)", "rgba(255,27,76,0.8)" 
					    		           ,"rgba(21,255,239,0.8)", "rgba(136,8,255,0.8)"]);
					    	    
					    	    
					    	    //attach data to x and y axis
					    	    x0.domain(chart_data.map(function(d){ return d["week"] }));
					    	    x1.domain(activityNames).range([0, x0.bandwidth()]);
					    	    y0.domain([0, d3.max(chart_data, function(d){ return d3.max(d.activities, function(d){ return d.value + 1; }); })]);
					    	   
					    	    
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
					    	
					    	    //attach data to svg
					    	    var areas = g.append("g")
					    	      .selectAll("g")
					    	      .data(chart_data)
					    	      .enter().append("g")
					    	        .attr("transform", function(d, i) {return "translate(" + x0(d["week"]) + ",0)"; });
					    	    var rects = areas.selectAll("rect")
					    	      .data(function(d) {return d.activities;})
					    	      .enter().append("rect")
					    	        .attr("x", function(d) { return x1(d.name); })
					    	        .attr("y", function(d) { return y0(d.value); })
					    	        .attr("width", x1.bandwidth())
					    	        .attr("height", function(d) { return height_chart - y0(d.value); })
					    	        .style("fill", function(d){ return z(d.name); });
					    	    
					    	    //tooltips
					    	    
					    	    var tooltipChart = d3.select("body")
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
				
					    	    areas.on("mousemove", function(d) {
									tooltipChart.transition()
									   .duration(50)
									   .style("opacity", .9);
									
									//keep tooltip from going out of view on right side
									var tooltipChartWidth = tooltipChart.node().getBoundingClientRect().width;
									var tooltipChartHeight = tooltipChart.node().getBoundingClientRect().height;
									var xposChart = d3.event.pageX > parentWidthChart - tooltipChartWidth ? d3.event.pageX - tooltipChartWidth : d3.event.pageX;
									
									tooltipChart.html("<span style=\"text-transform:capitalize;\">" + d.week + "</span></br>"
											
													+ "<span style=\"background-color:rgba(45,77,255,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[0].name + ": " + d.activities[0].value + "</br>"
																    
													+ "<span style=\"background-color:rgba(255,196,45,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[1].name + ": " + d.activities[1].value + "</br>"
													  
													+ "<span style=\"background-color:rgba(255,83,45,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[2].name + ": " + d.activities[2].value + "</br>"
													
													+ "<span style=\"background-color:rgba(194,27,255,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[3].name + ": " + d.activities[3].value + "</br>"
													
													+ "<span style=\"background-color:rgba(84,255,27,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[4].name + ": " + d.activities[4].value + "</br>"
													
													+ "<span style=\"background-color:rgba(255,27,76,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[5].name + ": " + d.activities[5].value + "</br>"
													
													+ "<span style=\"background-color:rgba(21,255,239,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[6].name + ": " + d.activities[6].value + "</br>"
													
													+ "<span style=\"background-color:rgba(136,8,255,0.8);" +
													  "display: block; left: 0; float: left;" +
													  "top: 0; width: 10px; height:10px; border-radius: 5px;" +
													  "\"></span>" + d.activities[7].name + ": " + d.activities[7].value + "</br>"
									)
										.style("left", (xposChart) + "px")		
					    			    .style("top", (d3.event.pageY - tooltipChartHeight) + "px");
									
									d3.select(this).style('stroke', 'black');
								})
								.on("mouseout", function(d) {
									tooltipChart.transition()
									.duration(500)
									.style("opacity", 0);
									
									d3.select(this).style('stroke', 'none');
								});
					    	    
						    	//add the X gridlines
						    	var xgrid = g.append("g")			
						    	    .attr("class", "gridx")
						    	    .attr("transform", "translate(" + (x0.bandwidth() * (1/2)) + "," + height_chart + ")")
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
		    	    	makeBarChart();
    	    	//end of bar chart
		    	    	
		    	    	
    	    	var retrieveApi = function() {
        	    	var courseTable = document.getElementById("courseTable").children[1].children;
        	    	
    	    		// Get intervention data
        	    	var innerData = {"aggregateType": "aggregate"};
        	    	var url = 'api-activity-aggregate';  	      	     	    	
        	    	
        	    	innerData.filter = [
        	    		{"$match": {"course": data.course.sourcedId
        	    				  , "intervention": {"$exists": true}
        	    			}
        	    		},
        	    		{ "$sort": { "datetime":1 } },
        	    		{"$group": { "_id": {"student_first_name": "$student_first_name", "student_last_name": "$student_last_name"}
        	    				   , "intervention": {"$last": "$intervention" } 
        	    				   } 
        	    		}
        	    	]
        	    		
        	    	var apiInData = {"data": JSON.stringify(innerData)}

            	    var interData = museApiService.sendToApi(currentUser.tenant_id, url, apiInData).then(function (d){
            	    	d = JSON.parse(d.data);
            	    	
            	    	// See which intervention boxes should be checked
            	    	for(var i in $scope.enrollments){
            	    		
            	    		for(var j in d){
            	    			if(!d[j].intervention) continue; // False already default value
            	    			
            	    			// Check by name
            	    			if($scope.enrollments[i].user.familyName == d[j]._id.student_last_name && $scope.enrollments[i].user.givenName == d[j]._id.student_first_name){
            	    				$scope.enrollments[i].intervention = true;
            	    			}
            	    			
            	    		}
            	    	}
            	    });
        	    	
        	    	
        	    	// Get accuracy data
        	    	innerData.filter =  [
        	    		{"$match": {"course": data.course.sourcedId
  	    				  		  , "probability": {"$exists": true}
        	    				  , "accuracy": {"$exists": true}
        	    			}
		  	    		},
		  	    		{ "$sort": { "datetime":1 } },
		  	    		{"$group": { "_id": {"student_first_name": "$student_first_name", "student_last_name": "$student_last_name"}
		  	    			       , "probability": {"$last": "$probability" }
		  	    			       , "accuracy": {"$last": "$accuracy"} 
		  	    			       } 
		  	    		}
		  	    	]
        	    	
        	    	apiInData = {"data": JSON.stringify(innerData)}

        	    	var accData = museApiService.sendToApi(currentUser.tenant_id, url, apiInData).then(function (d){
        	    		d = JSON.parse(d.data);
        	    		
        	    		// See which accuracy boxes should be checked
            	    	for(var i in $scope.enrollments){
            	    		
            	    		for(var j in d){
                	    		if(d[j].accuracy == null) continue; // Null is neither yes nor no
                	    		
            	    			// Check by name
            	    			if($scope.enrollments[i].user.familyName == d[j]._id.student_last_name && $scope.enrollments[i].user.givenName == d[j]._id.student_first_name){
            	    				d[j].accuracy ? $scope.enrollments[i].accuracy = 'yes' : $scope.enrollments[i].accuracy = 'no';
            	    			}
            	    			
            	    		}
            	    	}
            	    });
            	  
            	    
        	    }

	    		var showConfElems = function(){
		    		if(currentWeek > 5 && currentWeek < 11){
		    			var confSpans = document.getElementsByClassName("instructorConfSpan");
	    	    		
	    	    		for(var i = 0; i < confSpans.length; i++){
	    	    			confSpans[i].style.color = "black";
	    	    		}
	    	    		
		    		}else{
		    			var confIntervention = document.getElementsByName("intervention");
		    			var confCorrection = document.getElementsByName("correction");
		    			 
		    			for(var i = 0; i < confIntervention.length; i++){
		    				confIntervention[i].disabled = true;
		    			}
		    			 
		    			for(var i = 0; i < confCorrection.length; i++){
		    				confCorrection[i].disabled = true;
		    			}
		    		}
		    		
		    		
		    		
		    		retrieveApi();
	    		}
	    		
	    		// Run on table load -- use mutation observer due to load events on window/document not working
	    		 var observer = new MutationObserver(function(){
	    			 showConfElems();
	    		 })
	    		 
	    		 observer.observe(document.getElementById('courseTable'), { attributes: false, childList: true, subtree: true });

 
    	    	
    	    });
    	    
    	    $scope.submitApi = function(el, student) {
    	    	var innerData = {"bulk": false};
    	    	var url = 'api-activity-submit';
	    		var date = new Date();

    	    	// Get data
    	    	innerData.data = {"student_first_name": student.user.givenName
    	    						, "student_last_name": student.user.familyName
    	    						, "userId": student.user.userId
    	    						, "course": student.class.sourcedId
    	    						, "datetime": date.toISOString()
    	    						}; 
    	    	
    	    	if(el.name == "intervention"){
    	    		innerData.data.intervention = el.checked;  
    	    		
    	    	}else if(el.name == "correction"){
	    			innerData.data.probability = student.studentStatistics.successProbability;
    	    		
    	    		if(el.value == "yes"){

    	    			if(el.parentElement.children[1].checked) el.parentElement.children[1].checked = false; 
    	    			
    	    			// Check if box is being unselected
    	    			if(!el.checked){
    	    				innerData.data.accuracy = null;
    	    			}else{
        	    			innerData.data.accuracy = true;
    	    			}
    	    			
    	    		}else if(el.value == "no"){
    	    			if(el.parentElement.children[0].checked) el.parentElement.children[0].checked = false;
    	    			
    	    			// Check if box is being unselected
    	    			if(!el.checked){
    	    				innerData.data.accuracy = null;
    	    			}else{
        	    			innerData.data.accuracy = false;
    	    			}
    	    		}
    	    	}
    	    	
        	    var apiInData = {"data": JSON.stringify(innerData)}
        	    var testData = museApiService.sendToApi(currentUser.tenant_id, url, apiInData).then(function (d){
        	    })
    	    }
    	    
    	    
    	    
	    	var interventionRow = {"student": {"user": null, "class": null}, "elem": null}; // Student selected to email
	    	
    	    // Fires when the modal is opened.
    	    $scope.openEmailModal = function(user, course, el) {
    	    	
    	    	// Fill row object for intervention piece
    	    	interventionRow.student.user = user;
    	    	interventionRow.student.class = course.course;
    	    	interventionRow.elem = el.parentElement.querySelector('[name="intervention"]');
    	
    	    	
    	    	// Clears the data in the Cc field.	
    	    	$("#museEmailCc").val("");
     	    	for (var i = 0; i < course.enrollments.length; i++){
    	    		if (course.enrollments[i].user.role == 'teacher'){
    	    	    	$('#museEmailFrom').html(course.enrollments[i].user.email);
    	    	    	break;	
    	    		}
    	    	}
     	    	var subjectTrim = course.course.title.substring(0, 9);
     	    	var body = "Dear " + user.givenName + " " + user.familyName + ",[EOL][EOL] " + course.courseStatistics.emailTemplate.message;
     	    	
     	    	$('#museEmailBody').val(body.replace(/\[EOL\]/g, "\n"));
    	    	$('#emailModal').modal("show");
    	    	$('#museEmailTo').html(user.email);
     	    	$('#museEmailSubject').html(function(){ 
     	    		return course.course.sourcedId.substring(0, course.course.sourcedId.length - 7) + " - " + course.courseStatistics.emailTemplate.subject; 
     	    	});
     	    	
     	    	var attachmentArea = d3.select('#emailAttachments');
     	    	attachmentArea.html("");
     	    	
     	    	var idcounter = 0;
     	    	var currid = "attach";
     	    	
     	    	for(var i in course.courseStatistics.emailTemplate.Attachments){
     	    		attachmentArea.append('input')
     	    			.attr("type", "checkbox")
     	    			.attr("value", course.courseStatistics.emailTemplate.Attachments[i].url)
     	    			.property("checked", true)
     	    			.attr("id", function(d){return currid + idcounter; })
     	    			.attr("class", "museAttachments");
     	    		
     	    		attachmentArea.append('label')
     	    			.attr("for", function(d){return currid + idcounter; })
     	    			.html(course.courseStatistics.emailTemplate.Attachments[i].description)
     	    			.style("font-size", "11pt")
     	    			.style("font-weight", "400");
     	    		
     	    		attachmentArea.append('span')
     	    			.html("(" + "<a href='" + course.courseStatistics.emailTemplate.Attachments[i].url + "' target='_blank' style='color:blue'>?</a>" + ")");
     	    		
     	    	 	attachmentArea.append('span')
     	    	 		.html("</br>");
     	    	 	
     	    		idcounter++;
     	    	}

     	    	
     	    	attachmentArea.append('span')
     	    		.html("Custom Link Attachment:")
     	    		.style("font-weight", "bold");
     	    	
     	    	attachmentArea.append('input')
	    			.attr("type", "url")
	    			.style("display", "block")
	    			.style("width", "100%")
     	    		.attr("class", "museAttachments")
     	    		.attr("id", "customUrl")
     	    		.attr("placeholder", "http://google.com");
     	    	
     	    	attachmentArea.append('span')
	 	    		.html("Custom Link Description:")
	 	    		.style("font-weight", "bold");
     	    	
     	    	attachmentArea.append('input')
	    			.attr("type", "text")
	    			.style("display", "block")
	    			.style("width", "100%")
	 	    		.attr("class", "museAttachments")
	 	    		.attr("id", "customDesc")
	 	    		.attr("placeholder", "A popular search engine.");
    	    }
    	    
    	    // Presentation test modal
    	    $scope.openTestModal = function() {
    	    	$('#emailModal').modal("show");
    	    }
    	    
       	    // email services
    	    $scope.submit = function() {
    	    	// Check intervention check box
    	    	if(currentWeek > 5 && currentWeek < 11){
    	    		// Simulate it being clicked before submission
    	    		interventionRow.elem.checked = true;
    	    		
    	    		$scope.submitApi(interventionRow.elem, interventionRow.student);
    	    	}
    	    		
    	    	// Variables created to get the data from the input fields.
    	    	var museEmailTo = $("#museEmailTo").html();
    	    	var museEmailFrom = $("#museEmailFrom").html();
    	    	var museEmailCc = $("#museEmailFrom").html();
    	    	var museEmailSubject = $("#museEmailSubject").html();
    	    	var museEmailBody = $("#museEmailBody").val();
    	    	
    	    	//obtain values of each attachment
    	    	var museEmailAttachments = "";
    	    	$('.museAttachments').each(function(i) {
    	    		if($(this).is(':checked')){
    	    			museEmailAttachments += $(this).val() + "[EOF]";
    	    			museEmailAttachments += $scope.course.courseStatistics.emailTemplate.Attachments[i].description + "[EOR]"
    	    		}
    	    	});
    	    		
    	    	if($(customUrl).val().length > 0){
    	    		if($(customDesc).val().length > 0){
    	    			museEmailAttachments += $(customUrl).val() + "[EOF]";
    	    			museEmailAttachments += $(customDesc).val() + "[EOR]";
    	    		}else{
    	    			museEmailAttachments += $(customUrl).val() + "[EOF]";
    	    			museEmailAttachments += $(customUrl).val() + "[EOR]";
    	    		}
    	    	}
 
    	    	//sanitize email body for special characters
    	    	museEmailBody = museEmailBody.replace(/\|/g, "-");
    	    	museEmailAttachments = museEmailAttachments.replace(/\|/g, "-");
    	    	museEmailCc = museEmailCc.replace(/\|/g, "-");
    	    	
    	    	//add line breaks
    	    	museEmailBody = museEmailBody.replace(/\n/g, "<br>")
    	    	
    	    	
    	    	// Object created for sending email data to the database.
    	    	var inData = {"to": museEmailTo, "from": museEmailFrom, "cc": museEmailCc, "subject": museEmailSubject, "body": museEmailBody, "attachments": museEmailAttachments}
	    	    var data = museApiService
	    	    .notifyUser(currentUser.tenant_id, inData)
	    	    .then(function (data){
	    	    });
    	    	$('#emailModal').modal("hide");
    	    }
    	    

    	}
    }])
})(angular);

