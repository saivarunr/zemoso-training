const http = require('http');
var bodyParser=require('body-parser');
var express=require('express');
var mongoose=require('mongoose');
const port = 3000;

//jsonParser
var jsonParser=bodyParser.json();

//Connect to mongodb
mongoose.connect("mongodb://localhost/zemoso");

//Express JS usage
var thisApp=new express();

//JsonParser for request parsing
thisApp.use(bodyParser.json());

//Users schematics
var UsersSchema=new mongoose.Schema({
	username:String,phone:String,email:String
});


var User=mongoose.model('Users',UsersSchema);


thisApp.get("/varun",function(req,res){
	res.end("hello vaurn")
});


thisApp.post("/users",jsonParser,function(req,res){
	if(req.body==null){
		return res.sendStatus(400);
	}
	var userDetails=req.body;
	User.find({"username":req['username']},function(err,data){
		if(err){
			
		}
		console.log(""+data.length);
	});
	var user=new User(userDetails);
	user.save(function(err){
		if(err)
			return res.end("error");
		return res.end("done");
	});
	
});

thisApp.get("/allUsers",function(req,res){
	User.find({},function(err,data){
		var userslist=[];
		data.forEach(function(d){
			userslist.push(d.username);
		});
		return res.json(userslist);
	});
});
thisApp.listen(port,function(){
	console.log("app started");
});
