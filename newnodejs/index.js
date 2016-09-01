var express=require("express");
var app=new express();
var one=require("./scripts/one.js");
var path=require("path");
var React=require('react');
var ReactDOM=require('react-dom/server');
require('node-jsx').install();
app.set("view engine","ejs");
app.set("views",path.join(__dirname,'views'));

app.use(express.static(path.join(__dirname, 'public')));

app.listen(3000,function(){
	console.log("The node started");
});

app.get("/",function(req,res){
	var RootApp=React.createFactory(require('./scripts/thisReact.js'));
	var text=ReactDOM.renderToString(RootApp({}));
	res.render("index.ejs",{});
});