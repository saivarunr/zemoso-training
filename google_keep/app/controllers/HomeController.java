package controllers;



import java.util.List;
import java.util.Random;
import java.util.UUID;

import jdk.nashorn.internal.scripts.JS;

import akka.actor.ActorRef;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.crypto.MACSigner;


import models.AppUsers;
import models.Posts;
import play.data.Form;

import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import sun.security.provider.SecureRandom;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */


public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
    public Result welcome(){
    	return ok(welcome.render("ZeMoSo Notes",""));
    }
    
    public Result addUser(){
    	AppUsers users=Form.form(AppUsers.class).bindFromRequest().get();
    	users.setToken(UUID.randomUUID().toString());
    	try{
    		users.save();	
    	}
    	catch(Exception e){
    		if(e.toString().contains("PRIMARY")){
    			return badRequest("Username taken");
    			}
    		else if(e.toString().contains("email")){
    			return badRequest("Email id already mapped");
    		}
    		else{
    			return internalServerError("Something went wrong");
    		}
    	}
    	return ok("Successfully registered");
    }
    
    public Result login(){
    	AppUsers appUsers=Form.form(AppUsers.class).bindFromRequest().get();
    	String username=appUsers.getUsername();
    	String password=appUsers.getPassword();
    	int count=Ebean.find(AppUsers.class).where().conjunction().eq("username", username).eq("password", password).endJunction().findRowCount();
    	if(count==1){
    		session("username",username);
    		return redirect("main");
    	}
    	return ok(welcome.render("ZeMoSo Notes, login attempt failed","x"));
    
    		

    	
    }
    public Result newRegister(){
    	return ok(newRegister.render("new users"));
    }
    
    public  Result main(){
    	if(session("username")==null)
    		return redirect("login");
    	AppUsers user=Ebean.find(AppUsers.class).where().eq("username", session("username")).findUnique();
    	return ok(main.render(session("username"),user.getToken()));
    }
    
    
    public Result trial(){
    	ActorRef ref=ServerClock.serverClock;
    	ref.tell("varun",ref);
    	return ok();
    }
    public Result jwt(){
    	JsonNode jsonNode=request().body().asJson();
    	String text=jsonNode.path("arg").asText();
    	
    	return ok("".toString());
    	
    }
    public Result addPost(){
    	String token=request().getHeader("Authorization");
    	AppUsers appUsers=Ebean.find(AppUsers.class).where().eq("token", token).findUnique();
    	if(appUsers==null)
    		return badRequest("");
    	try{
    		JsonNode jsonNode=request().body().asJson();
	    	String title=jsonNode.path("title").asText();
	    	String content=jsonNode.path("content").asText();
	    	String reminder="";
	    	String isArchive="";
	    	Posts posts=new Posts(appUsers, title, content, reminder, isArchive);
	    	posts.save();
	    	return ok("");
    	}
    	catch(Exception e){
    		return internalServerError("");
    	}
    }
    public Result getAllPosts(){
    	String token=request().getHeader("Authorization");
    	AppUsers appUsers=Ebean.find(AppUsers.class).where().eq("token", token).findUnique();
    	if(appUsers==null)
    		return badRequest("");
    	try{
    		List<Posts> list=Ebean.find(Posts.class).where().eq("appUsers", appUsers).findList();
    		return ok(Json.toJson(list));
    	}
    	catch(Exception e){
    		return internalServerError("");
    	}
    	
    }
    public Result getPost(){
    	String token=request().getHeader("Authorization");
    	String id=request().getQueryString("id");
    	AppUsers appUsers=Ebean.find(AppUsers.class).where().eq("token", token).findUnique();
    	if(appUsers==null)
    		return badRequest("");
    	try{
    		List<Posts> list=Ebean.find(Posts.class).where().conjunction().eq("appUsers", appUsers).eq("postId",id).findList();
    		return ok(Json.toJson(list));
    	}
    	catch(Exception e){
    		return internalServerError("");
    	}
    }
}
