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
	
	public Messages(Users sender,Users reciever,String message){
		setSender(sender);
		setReciever(reciever);
		setMessage(message);
	}
	public Users getSender() {
		return sender;
	}
	public void setSender(Users sender) {
		this.sender = sender;
	}
	public Users getReciever() {
		return reciever;
	}
	public void setReciever(Users reciever) {
		this.reciever = reciever;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
