/**
 * @author varun
 */
var xcon=[];
var ycon=[];
var index=0;
var cx,cy,fx,fy,tx,ty;
var fx1,fy1,fx2,fy2;
var radius;
function display(x,y,id,r){
	d3.select("#circleAdder").append("circle").attr("cx",x).attr("cy",y).attr("r",r).style("fill","none").attr("stroke","black").attr("id",id);
}
function drawCircle(x,y){
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
		var temp1=getDistance(x,y,fx1,fy1);
		var temp2=getDistance(x,y,fx2,fy2);
		if(temp1>=temp2){
			fx=fx1;
			fy=fy1;
		}
		else{
			fx=fx2;
			fy=fy2;
		}
		var C=getNewCenter(cx,cy,x,y,fx,fy);
		cx=C[0];
		cy=C[1];
		radius=C[2];
		fx1=x;
		fx2=fx;
		fy1=y;
		fy2=fy;
		
	}
	d3.select("#big").remove();
	display(cx,cy,"big",radius);
}

function getDistance(x1,y1,x2,y2){
	return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
}


function getNewCenter(cx,cy,tx,ty,fx,fy){
	var dx=tx-cx;
	var dy=ty-cy;
	cx=Number.parseInt(cx);
	tx=Number.parseInt(tx);
	var m=Number.MAX_SAFE_INTEGER;
	var newx=0;
	var newy=0;
	var y=0;
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
var radius=Math.max(getDistance(newx,newy,tx,ty),getDistance(newx,newy,fx,fy));
return [newx,newy,radius];
}

