package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import play.data.validation.Constraints.Required;

@Entity
public class Users extends Model{
	@Id
	@Required
	private String username;
	@Required
	private String password;
	@Column(unique=true,nullable=false)
	private String token;
	
	private String name;
	private Integer isGroup;
	

	
	public Users(String username,String password,String token, String name2,Integer isGroup){
		this.username=username;
		this.password=password;
		this.token=token;
		this.name=name2;
		this.isGroup=isGroup;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(Integer isGroup) {
		this.isGroup = isGroup;
	}
}

