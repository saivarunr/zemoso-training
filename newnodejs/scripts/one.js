var one=function(a,b){
	console.log("tada one "+(a+b));
}
var two=function(){
	console.log("tada two");
}
module.exports={
	one:one,two:two
}