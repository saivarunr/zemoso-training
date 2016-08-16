package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Groups;
import models.Messages;
import models.Users;
import play.libs.Json;
import play.mvc.*;

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
    public Result newUser(){
    	ObjectNode node=Json.newObject();
    	JsonNode jsonNode=request().body().asJson();
    	String username=jsonNode.path("username").asText("");
    	String password=jsonNode.path("password").asText("");
    	String name=jsonNode.path("name").asText("");
    	Integer isGroup=jsonNode.path("isGroup").asInt(0);
    	if(username.equals("")||password.equals("")){
    		node.put("message","Invalid credentials");
    		return badRequest(node);
    	}
    	String token=UUID.randomUUID().toString();
    	Users users=new Users(username, password, token,name,isGroup);
    	try{
    		node.put("message", "Success");
    		Ebean.save(users);
    		return ok(node);
    	}
    	catch(Exception e){
    		if(e.toString().contains("PRIMARY")){
    			node.put("message","username name already taken");
    		}
    		else{
    			node.put("message", "something wrong");
    		}
    		return badRequest(node);
    	}
    }
    public Result addMembersToGroup(){
    	String token=request().getHeader("Authorization");
    	JsonNode jsonNode=request().body().asJson();
    	String groupName=jsonNode.path("groupName").asText("");
    	String user=jsonNode.path("users").toString();
    	JsonNode json=Json.parse(user);
    	Users users1=null,sender=null;
    	try{
    		sender=Ebean.find(Users.class).where().eq("token", token).findUnique();
    		if(!sender.getUsername().isEmpty()){
    			String groupUsername=UUID.randomUUID().toString();
    			users1=new Users(groupUsername,groupUsername,groupUsername,groupName,1);
    			Ebean.save(users1);
    			for(int i=0;i<json.size();i++){
    				Users users2=Ebean.find(Users.class).where().eq("username", json.get(""+i).asText()).findUnique();
    				Groups groups=new Groups(users1, users2);
    				Ebean.save(groups);
    			}
    		}
    		Messages messages=new Messages(sender,users1,"This group is created by "+sender.getUsername());
    		Ebean.save(messages);
    	}
    	catch(Exception e){
    		return badRequest(e.toString());
    	}
    	
    	return ok();
    }
    
    public Result login(){
    	ObjectNode node=Json.newObject();
    	JsonNode jsonNode=request().body().asJson();
    	String username=jsonNode.path("username").asText("");
    	String password=jsonNode.path("password").asText("");
    	try{
    		Users users=Ebean.find(Users.class).where()
	    		.conjunction()
	    			.eq("username", username)
	    			.eq("password",password)
	    		.endJunction()
	    	.findUnique();
	    	node.put("message", "success");
	    	node.put("token", users.getToken());
	    	return ok(node);
    	}
    	catch(Exception e){
    		node.put("message","Invalid credentials");
    		node.put("token", "");
    		return badRequest(node);
    	}
    }
    
    public Result getUsers(){
    	String token=request().getHeader("Authorization");
    	ArrayNode node=Json.newArray();
    	try{
    		Users users=Ebean.find(Users.class).where().eq("token",token).findUnique();
    		List<Groups> groups=Ebean.find(Groups.class)
    								.where()
    									.eq("username", users)
    								.findList();
    		
    		List<Users> l=Ebean.find(Users.class).where().eq("isGroup", 0).findList();
    		
    		List<Users> list=new ArrayList<Users>();
    		
    		for(int i=0;i<groups.size();i++){
    			Users users2=Ebean.find(Users.class).where().eq("username", groups.get(i).getGroupName().getUsername()).findUnique();
    			list.add(users2);
    		}
    		l.addAll(list);
    		return ok(Json.toJson(l));
    	}
    	catch(Exception e){
    		ObjectNode node1=Json.newObject();
    		node1.put("message",e.toString());
    		return badRequest(node1);
    	}
    }
    
    public Result postMessage(){
    	String token=request().getHeader("Authorization");
    	ObjectNode node=Json.newObject();
    	JsonNode jsonNode=request().body().asJson();
    	String t=jsonNode.path("target").asText("");
    	String message=jsonNode.path("message").asText("");
    	if(t.equals("")||message.equals("")){
    		node.put("message", "Invalid message");
    		return badRequest(node);
    	}
    	try{
    		Users source=Ebean.find(Users.class).where().eq("token", token).findUnique();
    		Users target=Ebean.find(Users.class).where().eq("username", t).findUnique();
    		Messages messages=new Messages(source,target,message);
    		Ebean.save(messages);
    		node.put("message",messages.getId());
    		return ok(node);
    	}
    	catch(Exception e){
    		node.put("message","Invalid credentials");
    	}
    	return badRequest(node);
    }
    public Result getMessagesOf(){
    	String token=request().getHeader("Authorization");
    	String target=request().getQueryString("target");
    	try{
    		Users users=Ebean.find(Users.class).where().eq("token", token).findUnique();
    		List<Messages> list=Ebean.find(Messages.class).where()
	    			.conjunction()
	    				.eq("reciever",users)
	    				.eq("sender",Ebean.find(Users.class).where().eq("username",target).findUnique())
	    				.eq("requested",0)
	    			.endJunction()
	    		.orderBy("timestamp")
    		.findList();
    		List<Groups> groups=Ebean.find(Groups.class).where().eq("username",users).findList();
    		List<Messages> groupMessages=new ArrayList<Messages>();
    		for(Groups groups2:groups){
    			List<Messages> list2=Ebean.find(Messages.class).where().eq("reciever", groups2).findList();
    			groupMessages.addAll(list2);
    		}
    		list.addAll(groupMessages);
    		for(Messages messages:list){
    			Messages messages2=Ebean.find(Messages.class).where().eq("id",messages.getId()).findUnique();
    			messages2.setRequested(1);
    			Ebean.save(messages2);
    		}
    		return ok(Json.toJson(list));
    	}
    	catch(Exception e){
    		ObjectNode node=Json.newObject();
    		node.put("message", "failed");
    		return badRequest(node);
    	}
    }
    
    public Result readMessages(){
    	String token=request().getHeader("Authorization");
    	String target=request().getQueryString("target");
    	try{
    		List<Messages> list=Ebean.find(Messages.class).where()
	    			.conjunction()
	    				.eq("reciever",Ebean.find(Users.class).where().eq("token", token).findUnique())
	    				.eq("sender",Ebean.find(Users.class).where().eq("username",target).findUnique())
	    			.endJunction()
    		.findList();
    		for(Messages messages:list){
    			Messages messages2=Ebean.find(Messages.class).where().eq("id",messages.getId()).findUnique();
    			messages2.setRead(1);
    			Ebean.save(messages2);
    		}
    		return ok();
    	}
    	catch(Exception e){
    		ObjectNode node=Json.newObject();
    		node.put("message", "failed");
    		return badRequest(node);
    	}
    }
    
    @SuppressWarnings("deprecation")
	public Result getAllMessages(){
    	String token=request().getHeader("Authorization");
    	try{
    		List<Messages> list=Ebean.find(Messages.class).where()
	    			.conjunction()
	    				.eq("reciever",Ebean.find(Users.class).where().eq("token", token).findUnique())
	    				.eq("requested",0)
	    			.endJunction()
	    		.orderBy("timestamp")
    		.findList();
    		for(Messages messages:list){
    			Messages messages2=Ebean.find(Messages.class).where().eq("id",messages.getId()).findUnique();
    			messages2.setRequested(1);
    			Ebean.save(messages2);
    		}
    		ObjectNode node=Json.newObject();
    		node.put("messages",Json.toJson(list));
    		List<Object> list2=Ebean.find(Messages.class).where()
    			.conjunction().eq("sender",Ebean.find(Users.class).where().eq("token", token).findUnique())
    			.eq("read", 1).endJunction().findIds();
    		node.put("read",Json.toJson(list2));
    		return ok(node);
    	}
    	catch(Exception e){
    		ObjectNode node=Json.newObject();
    		node.put("message", "failed");
    		return badRequest(node);
    	}
    }
}
