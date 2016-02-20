package server;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import javax.jws.*;
import pt.ulisboa.tecnico.sdis.id.ws.*;
import java.util.*;
import java.security.Key;
import javax.crypto.SecretKey;
import java.io.File;

@WebService(
    endpointInterface="pt.ulisboa.tecnico.sdis.id.ws.SDId", 
    wsdlLocation="SD-ID.1_1.wsdl",
    name="SdId",
    portName="SDIdImplPort",
    targetNamespace="urn:pt:ulisboa:tecnico:sdis:id:ws",
    serviceName="SDId"
)
public class SDIdImpl implements SDId{

	Map<String,User> users = new TreeMap<String,User>();

	public SDIdImpl(){
		users.put("alice",new User("alice","alice@tecnico.pt","Aaa1"));
		users.put("bruno",new User("bruno","bruno@tecnico.pt","Bbb2"));
		users.put("carla",new User("carla","carla@tecnico.pt","Ccc3"));
		users.put("duarte",new User("duarte","bruno@tecnico.pt","Ddd4"));
		users.put("eduardo",new User("eduardo","eduardo@tecnico.pt","Eee5"));
	}
	
	public void createUser(String userId, String emailAddress) throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception{
		
		boolean at = false;

		if(emailAddress==null || emailAddress.equals("")){

			InvalidEmail invalid = new InvalidEmail();
			invalid.setEmailAddress(emailAddress);
			throw new InvalidEmail_Exception("Invalid Email Address", invalid);
		}

		for(int i = 0; i < emailAddress.length(); i++){

			char c = emailAddress.charAt(i);

			if(c == '@'){

				at = true;

				if(i == 0 || (i == emailAddress.length() - 1)){

					InvalidEmail invalid = new InvalidEmail();
					invalid.setEmailAddress(emailAddress);
					throw new InvalidEmail_Exception("Invalid Email Address", invalid);
				}
			}
		}

		if(!at){
			InvalidEmail invalid = new InvalidEmail();
			invalid.setEmailAddress(emailAddress);
			throw new InvalidEmail_Exception("Invalid Email Address", invalid);
		}
		if(userId==null){

			InvalidUser invalid = new InvalidUser();
			invalid.setUserId("");
			throw new InvalidUser_Exception("Invalid User", invalid);
		}
		if(userId.equals("")){

			InvalidUser invalid = new InvalidUser();
			invalid.setUserId("");
			throw new InvalidUser_Exception("Invalid User", invalid);
		}

		for(Map.Entry<String,User> entry : users.entrySet()) {

			User user = entry.getValue();

			if(user.getEmailAddress().equals(emailAddress)){

				EmailAlreadyExists invalid = new EmailAlreadyExists();
				invalid.setEmailAddress(emailAddress);
				throw new EmailAlreadyExists_Exception("Email Already Exists", invalid);
			}
		}
		if(users.containsKey(userId)){

			UserAlreadyExists invalid = new UserAlreadyExists();
			invalid.setUserId(userId);
			throw new UserAlreadyExists_Exception("User Already Exists", invalid);
		}

		User user = new User(userId,emailAddress);
		users.put(userId,user);
		String password = user.getPassword();
		System.out.println(password);
	}

	public void renewPassword(String userId) throws UserDoesNotExist_Exception{

		if(userId==null || userId.equals("")){
			UserDoesNotExist invalid = new UserDoesNotExist();
			invalid.setUserId(userId);
			throw new UserDoesNotExist_Exception("User Does Not Exists", invalid);
		}

		if(users.containsKey(userId)){
			User user = users.get(userId);
			String password = user.generatePassword();
			System.out.println(password);
		}
		else{
			UserDoesNotExist invalid = new UserDoesNotExist();
			invalid.setUserId(userId);
			throw new UserDoesNotExist_Exception("User Does Not Exists", invalid);
		}
	}

	public void removeUser(String userId) throws UserDoesNotExist_Exception{

		if(userId==null || userId.equals("")){
			UserDoesNotExist invalid = new UserDoesNotExist();
			invalid.setUserId(userId);
			throw new UserDoesNotExist_Exception("User Does Not Exists", invalid);
		}
		
		if(users.containsKey(userId)){
			users.remove(userId);
		}
		else{
			UserDoesNotExist invalid = new UserDoesNotExist();
			invalid.setUserId(userId);
			throw new UserDoesNotExist_Exception("User Does Not Exists", invalid);
		}
		System.out.println("User "+userId+" removed.");
	}

	public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception{

		if(userId==null || userId.equals("") || reserved==null){
			byte[] b={(byte)0};
			AuthReqFailed invalid = new AuthReqFailed();
			invalid.setReserved(b);
			throw new AuthReqFailed_Exception("Authentication Failed", invalid);
		}

		if(users.containsKey(userId)){

			int tamanho = reserved.length;

			byte[] service_bytes = Arrays.copyOfRange(reserved, 0, tamanho - 16);
			byte[] number = Arrays.copyOfRange(reserved, tamanho - 16, tamanho);
			String service = new String(service_bytes);

			User user = users.get(userId);

			try{

				byte[] password = user.getPassword().getBytes("UTF-8");

				Encryption encryption = new Encryption();

				byte[] kSession = encryption.generateKey();
				byte[] kClient = encryption.hash(password);

				byte[] toCipher = new byte[kSession.length + number.length];
				System.arraycopy(kSession, 0, toCipher, 0, kSession.length);
				System.arraycopy(number, 0, toCipher, kSession.length, number.length);
			
				byte[] ciphered = encryption.cipher(toCipher, kClient);

				long epoch = System.currentTimeMillis();
				long end = epoch + 300000;

				String ticketPart = userId + "/" + service + "/" + Long.toString(epoch) + "/" + Long.toString(end);
				byte[] ticketPart_bytes = ticketPart.getBytes("UTF-8");

				byte[] ticketFinal = new byte[ticketPart_bytes.length + kSession.length];
				System.arraycopy(ticketPart_bytes, 0, ticketFinal, 0, ticketPart_bytes.length);
				System.arraycopy(kSession, 0, ticketFinal, ticketPart_bytes.length, kSession.length);

				byte[] kServer = encryption.generateKey();

				String fp = new File("").getAbsolutePath();
				System.out.println(fp);

                int toParse = fp.indexOf("/sd-id");
                String newPath = fp.substring(0, toParse) + "/sd-store/";
                String kServer_name = newPath + "Kserver/" + userId + ".txt";
				encryption.write(kServer, kServer_name);
				
				byte[] ticketCiphered = encryption.cipher(ticketFinal, kServer);

				byte[] total = new byte[ciphered.length + ticketCiphered.length];
				System.arraycopy(ciphered, 0, total, 0, ciphered.length);
				System.arraycopy(ticketCiphered, 0, total, ciphered.length, ticketCiphered.length);

				return total;

			}catch (Exception e){

				System.out.println(e.getMessage());
				byte[] b={(byte)0};
				AuthReqFailed invalid = new AuthReqFailed();
				invalid.setReserved(b);
				throw new AuthReqFailed_Exception("Authentication Failed", invalid);
			}
	
		}

		byte[] b={(byte)0};
		AuthReqFailed invalid = new AuthReqFailed();
		invalid.setReserved(b);
		throw new AuthReqFailed_Exception("User Does Not Exists", invalid);
	}
}