package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class GroupMessageValid extends Model{
@Id
private
Long id;
@ManyToOne
private
Messages messages;
@ManyToOne
private
Users users;
private Integer isReceived;
public GroupMessageValid(Messages messages,Users users){
	this.setMessages(messages);
	this.setUsers(users);
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public Integer getIsReceived() {
	return isReceived;
}
public void setIsReceived(Integer isReceived) {
	this.isReceived = isReceived;
}
public Users getUsers() {
	return users;
}
public void setUsers(Users users) {
	this.users = users;
}
public Messages getMessages() {
	return messages;
}
public void setMessages(Messages messages) {
	this.messages = messages;
}
}
