package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class Posts extends Model{
	@Id
	private int postId;
	
	@ManyToOne
	private AppUsers appUsers;
	
	
	private String content;
	
	
	private String title;
	
	@Column(name="timestamp",columnDefinition="timestamp default current_timestamp")
	private String date;
	
	private String reminder;
	
	private String isArchive; 
	
	
	public Posts(AppUsers appUsers,String title,String content,String reminder,String isArchive) {
		this.setAppUsers(appUsers);
		this.setTitle(title);
		this.setContent(content);
		this.setReminder(reminder);
		this.setIsArchive(isArchive);
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public String getIsArchive() {
		return isArchive;
	}

	public void setIsArchive(String isArchive) {
		this.isArchive = isArchive;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*public AppUsers getAppUsers() {
		return appUsers;
	}*/

	public void setAppUsers(AppUsers appUsers) {
		this.appUsers = appUsers;
	}
	
	//public static Finder<Long, Posts> find=new Finder<Long, Posts>(Posts.class);
	
}