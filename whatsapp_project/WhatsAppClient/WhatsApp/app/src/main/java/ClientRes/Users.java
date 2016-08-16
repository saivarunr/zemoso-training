package ClientRes;

/**
 * Created by zemoso on 4/8/16.
 */
public class Users {
    private String username;
    private String name;
    private Integer is_group;
    public String getUsername() {
        return username;
    }
    public Users(String username,String name,Integer is_group){

        this.username=username;
        this.name=name;
        this.is_group=is_group;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIs_group() {
        return is_group;
    }

    public void setIs_group(Integer is_group) {
        this.is_group = is_group;
    }
}
