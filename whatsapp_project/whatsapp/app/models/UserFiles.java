package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.avaje.ebean.Model;

@Entity
public class UserFiles extends Model {
	@Id
	Long id;
	@ManyToOne
	private
	Messages messages;
	@Column(columnDefinition="MEDIUMTEXT")
	private String fileEncoded;
	
	public UserFiles(Messages messages,String fileEncoded){
		this.messages=messages;
		this.fileEncoded=fileEncoded;
	}
	public Messages getMessages() {
		return messages;
	}
	public void setMessages(Messages messages) {
		this.messages = messages;
	}
	public String getFileEncoded() {
		return fileEncoded;
	}
	public void setFileEncoded(String fileEncoded) {
		this.fileEncoded = fileEncoded;
	}
}
