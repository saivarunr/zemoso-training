/**
 * @author varun
 */
var number_of_points=0;
var xcontainer=[];
var ycontainer=[];
var radius=0;
var xCenter=0;
var yCenter=0;
/*
 * returns radius for particular i and j based circle
 */
function radiusSetter(i,j){
	radius=Math.sqrt(Math.pow(xcontainer[i]-xcontainer[j],2),Math.pow(ycontainer[i]-ycontainer[j],2))/2;	
}
/*
 * Sets center of circle for i and j co-ordinates
 */
function setCenterOfCircle(i,j){
	xCenter=(xcontainer[i]+xcontainer[j])/2;
	yCenter=(ycontainer[i]+ycontainer[j])/2;
}
/*
 * Checks whether a point is outside, inside or on the circle
 * returns true if point is inside or on the circle
 * else false
 */
function isCircleLegal(a,b){
	radiusSetter(a,b);
	setCenterOfCircle(a,b);
	for(var i=0;i<number_of_points;i++){
		if(Math.sqrt(Math.pow(xcontainer[i]-xCenter,2),Math.pow(ycontainer[i]-yCenter,2))/2>radius)
			return false;
		return true;
	}
}

function updateScreen(firstIndex,secondIndex,newRadius){
	d3.select("#BigCircle").remove();
	d3.select("#circleAdder").append("circle").attr("id","BigCircle").attr("cx",firstIndex).attr("cy",secondIndex).attr("r",newRadius).attr("fill","white").attr("stroke","black").attr("stroke-width","3");
}

function display(x,y,counter){
	d3.select("#circleAdder").append("circle").attr("id",counter).attr("cx",x).attr("cy",y).attr("r",2).attr("fill","black");
}
