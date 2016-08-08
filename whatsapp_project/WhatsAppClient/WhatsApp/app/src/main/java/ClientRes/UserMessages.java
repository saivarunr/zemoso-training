package ClientRes;

import org.w3c.dom.Text;

/**
 * Created by zemoso on 6/8/16.
 */
public class UserMessages {
    private int Id;
    private String sender;
    private String reciever;
    private String message;
    private String timestamp;
    public UserMessages(Integer id,String sender,String reciever,String message,String timestamp){
        this.Id=id;
        this.sender=sender;
        this.reciever=reciever;
        this.message=message;
        this.timestamp=timestamp;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getMessageId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
