package models;

import java.util.Map;

import javax.persistence.Entity;

import com.avaje.ebean.annotation.DbJsonB;


@Entity
public class Group_User {
private String data;
@DbJsonB
private
Map<String, Object> map;
public Group_User(Map<String,Object> map,String data){
	setData(data);
	setMap(map);
}
public String getData() {
	return data;
}
public void setData(String data) {
	this.data = data;
}
public Map<String, Object> getMap() {
	return map;
}
public void setMap(Map<String, Object> map) {
	this.map = map;
}

}
