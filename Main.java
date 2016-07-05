abstract class LoginViaSocialMedia{
	private String username;
	private String password;
	abstract String getSocialMediaName();
	 String getUsername() {
		return username;
	}
	 void setUsername(String username) {
		this.username = username;
	}
	 String getPassword() {
		return password;
	}
	 void setPassword(String password) {
		this.password = password;
	}
	abstract String getUserURL();
	
}

class LoginViaFacebook extends LoginViaSocialMedia{

	public LoginViaFacebook(String username,String password) {
		setPassword(password);
		setUsername(username);
	}
	@Override
	String getSocialMediaName() {
		return "facebook";
	}

	@Override
	String getUserURL() {
		if(getUsername()!=""&&getPassword()!="")
			return "user profile url:: http://"+getSocialMediaName()+".com/"+getUsername();
		return "Cannot find USER PROFILE";
	}
	
}
class LoginViaTwitter extends LoginViaSocialMedia{
	public LoginViaTwitter(String username,String password) {
		setPassword(password);
		setUsername(username);
	}

	@Override
	String getSocialMediaName() {
		// TODO Auto-generated method stub
		return "twitter";
	}

	@Override
	String getUserURL() {
		// TODO Auto-generated method stu
		if(getUsername()!=""&&getPassword()!="")
			return "user profile url:: http://"+getSocialMediaName()+".com/"+getUsername(); 
		return "Cannot find URL related to USER";
	}
	
}

class IntermediatePlatform{
	LoginViaSocialMedia getLoginDetails(String social_media_name,String username,String password){
		if(social_media_name.equalsIgnoreCase("facebook"))
			return new LoginViaFacebook(username, password);
		else if(social_media_name.equalsIgnoreCase("twitter"))
			return new LoginViaTwitter(username, password);
		return null;
	}
}

class Main{
	public static void main(String args[]){
		LoginViaSocialMedia media1=login("facebook","saivarunvishal","saivarunvishal");
		System.out.println("media 1 "+media1.getUserURL());
		LoginViaSocialMedia media2=login("twitter", "saivarunvishal", "xyz");
		System.out.println("media 2 "+media2.getUserURL());
	}
	static LoginViaSocialMedia  login(String social_media_name,String username,String password){
		
		IntermediatePlatform platform= new IntermediatePlatform();
		return platform.getLoginDetails(social_media_name, username, password);
		
	}
}