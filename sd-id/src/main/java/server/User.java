package server;

import java.util.Random;

public class User{
	private String _userId;
	private String _emailAddress;
	private String _password;

	public User(String userId, String emailAddress){
		_userId=userId;
		_emailAddress=emailAddress;
		_password=generatePassword();
	}
	public User(String userId, String emailAddress, String password){
		_userId=userId;
		_emailAddress=emailAddress;
		_password=password;
	}

	public String getPassword(){
		return _password;
	}

	public String getEmailAddress(){
		return _emailAddress;
	}

	public String generatePassword(){
		Random rand = new Random();
		String pass="";
		int num;
		int i;
		for(i=0; i<8; i++){
			do{
				num = rand.nextInt(128);
			}while(!((48<=num && num<=57) || (65<=num && num<=90) || (97<=num && num<=122)));
			
			pass+=(char)num;
		}
		_password = pass;
		return pass;
	}
}