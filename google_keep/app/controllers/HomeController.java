package controllers;



import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

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
import play.data.DynamicForm;
import play.data.Form;

import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import scala.App;
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
        return ok(index.render());
    }
    public Result welcome(){
    	return ok(welcome.render("ZeMoSo Notes",""));
    }
    
    public Result addUser(){
    	JsonNode jsonNode=request().body().asJson();
    	String username=jsonNode.path("username").asText();
    	String password=jsonNode.path("password").asText();
    	String email=jsonNode.path("email").asText();
    	String hash=BCrypt.hashpw(password, BCrypt.gensalt());
    	String token=UUID.randomUUID().toString();
    	AppUsers users=new AppUsers(username, hash, email,token);
    	
    	try{
    		Ebean.beginTransaction();
    		Ebean.save(users);	
    	}
    	catch(Exception e){
    		Ebean.rollbackTransaction();
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
    	Ebean.commitTransaction();
    	return ok("Successfully registered");

    }
    
    public Result login(){
    	AppUsers appUsers=Form.form(AppUsers.class).bindFromRequest().get();
    	String username=appUsers.getUsername();
    	String password=appUsers.getPassword();
    	try{
	    	AppUsers users=Ebean.find(AppUsers.class).where().eq("username", username).findUnique();
	    	String db_password=users.getPassword();
	    	if(BCrypt.checkpw(password, db_password)){
	    		session("username",username);
	    		return redirect("main");
	    	}
    	}
    	catch(Exception e){
    		
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
	    	Integer isArchive=jsonNode.path("isArchive").asInt();
	    	Posts posts=new Posts(appUsers, title, content, reminder,isArchive);
	    	Ebean.beginTransaction();
	    	Ebean.save(posts);
	    	Ebean.commitTransaction();
	    	return ok("");
    	}
    	catch(Exception e){
    		Ebean.rollbackTransaction();
    		return internalServerError(e.toString());
    	}
    }
    public Result getAllPosts(){
    	String token=request().getHeader("Authorization");
    	AppUsers appUsers=Ebean.find(AppUsers.class).where().eq("token", token).findUnique();
    	if(appUsers==null)
    		return badRequest("");
    	try{
    		List<Posts> list=Ebean.find(Posts.class).where().eq("appUsers", appUsers).eq("isArchive",0).orderBy("postId desc").findList();
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
    public Result archivePostById(){
    	String token=request().getHeader("Authorization");
    	JsonNode jsonNode=request().body().asJson();
    	String postId=jsonNode.path("postId").asText();
    	try{
    		
	    	Posts posts=Ebean.find(Posts.class)
	    			.where()
	    			.conjunction()
		    			.eq("appUsers", Ebean.find(AppUsers.class).where().eq("token", token).findUnique())
		    			.eq("postId", postId)
	    			.endJunction()
	    			.findUnique();
	    	Ebean.beginTransaction();
	    		posts.setIsArchive(1);
	    		Ebean.update(posts);
	    	Ebean.commitTransaction();
	    	return ok();
    	}
    	catch(Exception e){
    		Ebean.rollbackTransaction();
    	}
    	return badRequest();
    }
    public Result viewArchives(){
    	return ok(index.render(	));
    }
}
