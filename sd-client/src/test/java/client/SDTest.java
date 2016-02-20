package client;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import pt.ulisboa.tecnico.sdis.id.ws.*;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import client.uddi.*;
import client.handler.*;
import client.Encryption;
import client.exception.*;
import java.io.File;

import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;


import client.Encryption;

/**
 *  Test suite
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SDTest {

	// static members
    private static SDId port;



    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

	    SDId_Service service = new SDId_Service();
        port = service.getSDIdImplPort();

    }

    @AfterClass
    public static void oneTimeTearDown() {
        port=null;
    }


    // initialization and clean-up for each test

    @Before
    public void setUp() {
	}

	public FrontEnd frontEnd(String user, String pass){

		Encryption encryption = new Encryption();
		FrontEnd fe = null;

		try{
			System.out.println("CRIAR SERVICO\n========================");
			byte[] pass_bytes = pass.getBytes("UTF-8");
			byte[] service_bytes = "SDStore".getBytes("UTF-8");
	        byte[] number_bytes = encryption.generateKey();

	        byte[] toSend = new byte[service_bytes.length + number_bytes.length];
	        System.arraycopy(service_bytes, 0, toSend, 0, service_bytes.length);
	        System.arraycopy(number_bytes, 0, toSend, service_bytes.length, number_bytes.length);   

			byte[] response = port.requestAuthentication(user, toSend);
			byte[] kClient = encryption.hash(pass_bytes);
			byte[] first = Arrays.copyOfRange(response, 0, 48);
			byte[] first_deciphered = encryption.decipher(first, kClient);
			byte[] kSession = Arrays.copyOfRange(first_deciphered, 0, 16);
			byte[] number_received = Arrays.copyOfRange(first_deciphered, 16, 32);
			byte[] ticket = Arrays.copyOfRange(response, 48, response.length);

			if(Arrays.equals(number_bytes, number_received)){

		    	System.out.println("Authentication Sucessful");
			    fe = new FrontEnd(3, "SDStore", "http://localhost:8081", user, ticket, kSession);
	    	}
		}catch(Exception e){
	    	System.out.println(e.getMessage());
	    }

		
	    return fe;

	}

    @After
    public void tearDown() {
    }


    @Test
    public void test01(){

        String result="";
        String expected="";

        try{ port.createUser("Madalena","madalena@mail.com");} 
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test02(){

        String result="";
        String expected="User Already Exists";

        try{ port.createUser("Madalena","madalena2@mail.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test03(){

        String result="";
        String expected="Email Already Exists";

        try{ port.createUser("Outra Madalena","madalena@mail.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test04(){

        String result="";
        String expected="Invalid Email Address";

        try{ port.createUser("Terceira Madalena","madalena.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test05(){

        String result="";
        String expected="Invalid Email Address";

        try{ port.createUser("Quarta Madalena","@madalena.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test06(){

        String result="";
        String expected="Invalid Email Address";

        try{ port.createUser("Quinta Madalena","madalena@");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test07(){

        String result="";
        String expected="Invalid User";

        try{ port.createUser("","madalenavazia@mail.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test08(){

        String result="";
        String expected="Invalid User";

        try{ port.createUser(null,"madalenanull@mail.com");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test09(){

        String result="";
        String expected="User Does Not Exists";

        try{ port.renewPassword(null);}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test10(){

        String result="";
        String expected="";

        try{ port.renewPassword("Madalena");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test11(){

        String result="";
        String expected="User Does Not Exists";

        try{ port.removeUser("Manel");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test12(){

        String result="";
        String expected="";

        try{ port.removeUser("bruno");}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test13(){

        String result="";
        String expected="User Does Not Exists";

        try{ port.requestAuthentication("manel","jsdygj".getBytes());}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }


    @Test
    public void test21(){

    	FrontEnd fe = frontEnd("alice","Aaa1");

        String result="";
        String expected="";

        try{ fe.createDoc("doc1");} 
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test22(){

    	FrontEnd fe = frontEnd("alice","Aaa1");

        String result="";
        String expected="Document already exists";

        try{ fe.createDoc("doc1"); }
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test23(){

    	FrontEnd fe = frontEnd("bruno","Bbb2");

        List<String> result = null;
        List<String> expected = new ArrayList<String>();

        try{ result=fe.listDocs(); }
        catch(Exception e){ }

        assertEquals(expected,result);
    }

    @Test
    public void test24(){

    	FrontEnd fe = frontEnd("bruno","Bbb2");

        String result = "";
        String expected = "";

        try{ fe.createDoc("doc3"); }
        catch(Exception e){ result = e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test25(){

    	FrontEnd fe = frontEnd("alice","Aaa1");

        String result="";
        String expected="";

        try{ fe.listDocs();}
        catch(Exception e){ result=e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test26(){

    	FrontEnd fe = frontEnd("bruno","Bbb2");

        String result = "";
        String expected = "Document already exists";

        try{ fe.createDoc("doc3"); }
        catch(Exception e){ result = e.getMessage(); }

        assertEquals(expected,result);
    }

    @Test
    public void test27(){

    	FrontEnd fe = frontEnd("bruno","Bbb2");

        List<String> result = null;
        List<String> expected = new ArrayList<String>();
        expected.add("doc3");

        try{ result = fe.listDocs();}
        catch(Exception e){ e.getMessage(); }

        assertEquals(expected,result);
    }

}
