package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
	
	public Users(String username,String password,String token){
		this.username=username;
		this.password=password;
		this.token=token;
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
}

