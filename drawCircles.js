/**
 * @author varun
 */
var xcon=[]; //X-Axis co-ordinate container
var ycon=[]; //Y-axis co-ordinate container
var index=0;	//index used for adding "ids" to points in the place
var cx,cy,fx,fy,tx,ty; /*
                        * Center (cx,cy) 
                        * FarthestPoint for new point Farthest(fx,fy)
                        *  Target point (new point added) (tx,ty)
                        */
var fx1,fy1,fx2,fy2; /*
                      * Fixed points on circle (fx1,fy1)
                      * (fx2,fy2)
                      */
var radius; 		//radius
function display(x,y,id,r){
	//Function which displays a point or a circle with their id and radius
	d3.select("#circleAdder").append("circle").attr("cx",x).attr("cy",y).attr("r",r).style("fill","none").attr("stroke","black").attr("id",id);
}
function drawCircle(x,y){
	//If x axis container contains only two elements then draw a circle with points on diameter
	if(xcon.length==2){
		cx=(xcon[0]+xcon[1])/2;
		cy=(ycon[0]+ycon[1])/2;
		fx1=xcon[0];
		fx2=xcon[1];
		fy1=ycon[0];
		fy2=ycon[1];
		radius=getDistance(cx,cy,xcon[0],ycon[0]);
	}
	else if(xcon.length>2 && getDistance(x,y,cx,cy)>radius){
		//If number of co-ordinates are more than 2 and the new co-ordinate is outside the circle
		var temp1=getDistance(x,y,fx1,fy1);
		var temp2=getDistance(x,y,fx2,fy2);
		//finding the farthest point from the new point
		if(temp1>=temp2){
			fx=fx1;
			fy=fy1;
		}
		else{
			fx=fx2;
			fy=fy2;
		}
		
		//Call to this method to get new co-ordinates of center and radius
		var C=getNewCenter(cx,cy,x,y,fx,fy);
		cx=C[0];
		cy=C[1];
		radius=C[2];
		//Updating points which lie on the cirle
		fx1=x;
		fx2=fx;
		fy1=y;
		fy2=fy;
		
	}
	
	//remove the previous circle containing points
	d3.select("#big").remove();
	//Add new circle
	display(cx,cy,"big",radius);
	var l=xcon.length;
	for(var i=0;i<l;i++){
		//Check if any of the old points lie outside the new circle if so, add them into the circle using same method
		if(getDistance(xcon[i],ycon[i],cx,cy)>radius){
			drawCircle(xcon[i],ycon[i]);
		}
	}
}


function getDistance(x1,y1,x2,y2){
//returns distance between two points
	return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
}


function getNewCenter(cx,cy,tx,ty,fx,fy){
	//Returns new center co-ordinates and radius
	var dx=tx-cx;
	var dy=ty-cy;
	cx=Number.parseInt(cx);
	tx=Number.parseInt(tx);
	var m=Number.MAX_SAFE_INTEGER;
	var newx=0;
	var newy=0;
	var y=0;
	// Center (cx,cy) and target (tx,ty) if cx>tx i.e., ex: Center(300,300) and target point is (200,200) slope changes
	if(cx>tx){
		for(var x=cx;x>tx;x--){
			y=cy+dy*(x-cx)/dx;
			var temp=Math.abs(Number.parseInt(getDistance(x,y,tx,ty)-getDistance(x,y,fx,fy)));
			if(m>temp){
				m=temp;
				newx=x;
				newy=y;
			}
	}

}
else{
		for(var x=cx;x<tx;x++){
			y=cy+dy*(x-cx)/dx;
			var temp=Math.abs(Number.parseInt(getDistance(x,y,tx,ty)-getDistance(x,y,fx,fy)));
			if(m>temp){
				m=temp;
				newx=x;
				newy=y;
			}
	}
}
//Return maximum radius of target point and fixed point (the point which was farthest point on circle from target point)
var radius=Math.max(getDistance(newx,newy,tx,ty),getDistance(newx,newy,fx,fy));
return [newx,newy,radius];
}
