/**
 * @author varun
 */

var xcon=[]; //X-Axis co-ordinate container
var ycon=[]; //Y-axis co-ordinate container
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
		var C=getNewCenter(cx,cy,x,y,x,y,fx,fy);
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

/*
 * This function takes two points A,B on the circle and tries to minimize the radius if
 * the points are not diametrically opposite (Angle of 180 degrees between two points),
 * it tries to move the Center(cx,cy) to the farthest point available in the circle except A,B
 */
function minimize(){
	//If difference is 180 degrees (equivalent numerical value is 3.14) return true
	if(Math.abs(Math.atan2(fy1-cy,fx1-cx)-Math.atan2(fy2-cy,fx2-cx))==Math.PI)
		return true;
	//If not
	//Get the farthest co-ordinates from the center of the circle
	coordinates=getFarthest(cx,cy);		
	//Calculate the new center X, Center Y and radius
	Center=getNewCenter(cx,cy,(fx1+fx2)/2,(fy1+fy2)/2,coordinates[0],coordinates[1],fx1,fy1);
	cx=Center[0];
	cy=Center[1];
	radius=Center[2];
	//remove the previous circle containing points
	d3.select("#big").remove();
	//Update the new circle
	display(cx,cy,"big",radius);
}


//This function returns farthest co-ordinates in a circle from (x,y) which aren't points on the circle i.e A,B
function getFarthest(x,y){
	var farthest=-1;
	var coordinates=[];
	var l=xcon.length;
	for(var i=0;i<l;i++){
		if((xcon[i]!=fx1&&ycon[i]!=fy1)&&(xcon[i]!=fx2&&ycon[i]!=fy2)){
			if(getDistance(xcon[i],ycon[i],x,y)>farthest){
				farthest=getDistance(xcon[i],ycon[i],x,y);
				coordinates=[xcon[i],ycon[i]];
			}
		}
	}
return coordinates;
}

function getDistance(x1,y1,x2,y2){
//returns distance between two points
	return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
}

/*
 * This function calculates new center (newx,newy) by taking old center (cx,cy) and the target point
 * to where the center (cx,cy) to be moved (tx,ty) and 
 * two reference fixed points (fx1,fy1) (fx2,fy2)
 * these two reference points serve as reference, 
 * 
 *------------------------------------------
 * A new center is found when distance between (newx,newy)-(fx1,fy1) is equal to (newx,newy)-(fx2,fy2)
 */
function getNewCenter(cx,cy,tx,ty,fx1,fy1,fx2,fy2){
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
			var temp=Math.abs(Number.parseInt(getDistance(x,y,fx1,fy1)-getDistance(x,y,fx2,fy2)));
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
			var temp=Math.abs(Number.parseInt(getDistance(x,y,fx1,fy1)-getDistance(x,y,fx2,fy2)));
			if(m>temp){
				m=temp;
				newx=x;
				newy=y;
			}
	}
}
//Return maximum radius of target point and fixed point (the point which was farthest point on circle from target point)
var radius=Math.max(getDistance(newx,newy,fx1,fy1),getDistance(newx,newy,fx2,fy2));
return [newx,newy,radius];
}

function checkExists(x,y){
	var l=xcon.length;
	for(var i=0;i<l;i++){
  			if(xcon[i]==x && ycon[i]==y){
  				xcon.splice(i,1);
  				ycon.splice(i,1);
  				document.getElementById("i"+x+"-"+y).remove();
  				return true;
  				
  			}
  		}
  		return false;
}

//Function which updates the circle
function update(x,y){
	//remove the original circle
	d3.select("#big").remove();
  			if(xcon.length==1) 
  			/*
  			 * If no. of points in the current plane are 1 then 
			* just return back A circle cannot be formed with one point
			*/
  				return;
  			else if(xcon.length==2){
  				/*
  				 * If no. of points in the plane are two, just draw circle using the two points
  				 */
  				drawCircle(xcon[0],ycon[0]);
  				
  			}
  			else{
  				var coordinates=getFarthest(cx,cy);  				  						
  				/*
  				 * Find the farthest point from the center of previous circle
  				 */
		  			if(x==fx1&&y==fy1){
		  				/*
		  				 * If current removed point (x,y) is fixed point fx1,fy1
		  				 * update the new fixed point of circle
		  				 */
						fx1=coordinates[0];
						fy1=coordinates[1];			
						/*
						 * and get new center for this circle
						 */
						Center=getNewCenter(cx,cy,fx2,fy2,fx2,fy2,fx1,fy1);
						
		  			}
		  			else if(x==fx2&&y==fy2){
		  				/*
		  				 * Else if the point removed is the fixed point fx2,fy2
		  				 * update it with new farthest point of the circle
		  				 */
		  				fx2=coordinates[0];
						fy2=coordinates[1];		
						/*
						 * Get new coordinates for the center and as well as the radius
						 */
						Center=getNewCenter(cx,cy,fx1,fy1,fx1,fy1,fx2,fy2);
						
		  			}
		  			/*
		  			 * cx contains the new center X coordinate
		  			 * cy contains the new center Y Coordinate
		  			 * radius contains the new radius of the circle
		  			 */
		  				cx=Center[0];
						cy=Center[1];
						radius=Center[2];	
  			/*
  			 * Display the newly formed circle
  			 */
  			display(cx,cy,"big",radius);
  			}
}
