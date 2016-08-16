package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;



@Entity
public class Groups extends Model{
	@Id
    public Long id;
	@ManyToOne
	private Users groupName;
	@ManyToOne
	private Users username;
	public Groups(Users users,Users username){
		this.groupName=users;
		this.username=username;
	}
	
	public Users getGroupName() {
		return groupName;
	}
	public void setGroupName(Users groupName) {
		this.groupName = groupName;
	}
	
}
