package ClientRes;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by zemoso on 11/8/16.
 */
public class MostRecentUserWrapper {
    private String username;
    private int id;
    private String message;
    private Date date;
    private String name;
    private int unreadCount=0;
    public MostRecentUserWrapper(String username, int id, String message,Date date,int unreadCount){
        this.setUsername(username);
        this.setId(id);
        this.setMessage(message);
        this.setDate(date);
        this.setUnreadCount(unreadCount);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {

        this.name = name;
    }
    public String getName(){
        return name;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
