package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class Messages extends Model{
	@Id
	private Long id;
	
	@ManyToOne
	@Column(name="sender")
	private Users sender;
	
	@ManyToOne
	@Column(name="reciever")
	private Users reciever;
	
	@Column(columnDefinition="TEXT")
	private String message;
	
	@Column(name="timestamp",columnDefinition="timestamp DEFAULT CURRENT_TIMESTAMP")
	String timestamp;
	
	@Column(name="requested",columnDefinition="integer default 0")
	private
	Integer requested;
	public Messages(Users sender,Users reciever,String message){
		setSender(sender);
		setReciever(reciever);
		setMessage(message);
		setRequested(0);
	}
	 Users getSender() {
		return sender;
	}
	 public String getTimestamp(){
		 return this.timestamp;
	 }
	 void setSender(Users sender) {
		this.sender = sender;
	}
	 Users getReciever() {
		return reciever;
	}
	 void setReciever(Users reciever) {
		this.reciever = reciever;
	}
	public String getSenderName(){
		return this.sender.getUsername();
	}
	public String getRecieverName(){
		return this.reciever.getUsername();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getRequested() {
		return requested;
	}
	public void setRequested(Integer requested) {
		this.requested = requested;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
