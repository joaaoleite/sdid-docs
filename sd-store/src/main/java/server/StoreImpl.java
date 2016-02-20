package server;

import java.util.*;

import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.store.ws.*;


import javax.annotation.Resource;
import javax.jws.*;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import server.handler.*;

@WebService(
    endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", 
    wsdlLocation = "SD-STORE.1_1.wsdl", 
    name = "SDStore", 
    portName = "SDStoreImplPort", 
    targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", 
    serviceName = "SDStore"
)
@HandlerChain(file="/handler-chain.xml")

public class StoreImpl implements SDStore {

    public static final String CLASS_NAME = StoreImpl.class.getSimpleName();
    public static final String TOKEN = "server";

    @Resource
    private WebServiceContext webServiceContext;


    public static HashMap<String, DocumentRepository> userRepositories = new HashMap<String, DocumentRepository>();

    /*
     * From WSDL: <!-- Creates a new document in the provided user's repository.
     * In case this is the first operation on that user, a new repository is
     * created for the new user. Faults: a document already exists with the same
     * id -->
     */
    public void createDoc(DocUserPair docUserPair)
            throws DocAlreadyExists_Exception {

        MessageContext messageContext = webServiceContext.getMessageContext();

        if((docUserPair.getUserId() != null) && (!docUserPair.getUserId().equals("")) &&
                (docUserPair.getDocumentId() != null) && (!docUserPair.getDocumentId().equals(""))) {

            String userId = docUserPair.getUserId();
            String document = docUserPair.getDocumentId();
            String argument = userId + "/" + document;


            if(verify(messageContext, userId, argument)){

                System.out.println("Verification sucessed");

                DocumentRepository rep = userRepositories.get(docUserPair.getUserId());


                if (rep == null) {
                    rep = new DocumentRepository();
                    userRepositories.put(docUserPair.getUserId(), rep);
                }

                messageContext.put("success", "true");
                
                if (rep.addNewDocument(docUserPair.getDocumentId()) == false) {
                    DocAlreadyExists faultInfo = new DocAlreadyExists();
                    faultInfo.setDocId(docUserPair.getDocumentId());
                    throw new DocAlreadyExists_Exception("Document already exists", faultInfo);
                }
            }
            else{       
                System.out.println("Verification failed, cant createDoc");
                messageContext.put("success", "false");
            }
        }
        else{       
                System.out.println("Invalid inputs");
                messageContext.put("success", "false");
        }
        return;
    }

    /*
     * From WSDL: <!-- Lists the document ids of the user's repository. Faults:
     * user does not exist -->
     */
    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception{
        
        MessageContext messageContext = webServiceContext.getMessageContext();

        if(userId == null || userId.equals("") || userRepositories.get(userId) == null){  
            UserDoesNotExist faultInfo = new UserDoesNotExist();
            faultInfo.setUserId(userId);
            throw new UserDoesNotExist_Exception("User does not exist", faultInfo);
        }

        if(verify(messageContext, userId, userId)){

            System.out.println("Verification sucessed");
            DocumentRepository rep = userRepositories.get(userId);
            messageContext.put("success", "true");
            return rep.listDocs(userId);
              	    	
        }
        else{
            System.out.println("Verification failed, cant listDocs");
            messageContext.put("success", "false");
            return new ArrayList<String>();
        }	  
    }

    /*
     * From WSDL: <!-- Replaces the entire contents of the document by the
     * contents provided as argument. Faults: document does not exist, user does
     * not exist, repository capacity is exceeded. -->
     */
    public void store(DocUserPair docUserPair, byte[] newContents)
            throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {

        throw new UnsupportedOperationException("Not implemented in minimal version!");
    }

    /*
     * From WSDL: <!-- Returns the current contents of the document. Fault: user
     * or document do not exist -->
     */
    public byte[] load(DocUserPair docUserPair)
            throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {

        throw new UnsupportedOperationException("Not implemented in minimal version!");
    }

    private boolean verify(MessageContext context, String userId, String argument){

        // get token from message context
        String ticket_str = (String) context.get("ticket");
        String auth_str = (String) context.get("auth");
        String mac_str = (String) context.get("mac");


        byte[] ticket = parseBase64Binary(ticket_str);
        byte[] auth = parseBase64Binary(auth_str);
        byte[] mac = parseBase64Binary(mac_str);

        Encryption encryption = new Encryption();

        try{
            
            byte[] kServer = encryption.read("Kserver/" + userId + ".txt");
            byte[] ticketDeciphered = encryption.decipher(ticket, kServer);
            int tamanho = ticketDeciphered.length;

            byte[] ticketPart_bytes = Arrays.copyOfRange(ticketDeciphered, 0, tamanho - 16);
            byte[] kSession = Arrays.copyOfRange(ticketDeciphered, tamanho - 16, tamanho);

            String ticketPart = new String(ticketPart_bytes);

            String[] ticketComponents = ticketPart.split("/");

            String userTicket = ticketComponents[0];
            String service = ticketComponents[1];
            long start = Long.parseLong(ticketComponents[2]);
            long end = Long.parseLong(ticketComponents[3]);

            byte[] authDeciphered_Bytes = encryption.decipher(auth, kSession);
            String authDeciphered = new String(authDeciphered_Bytes);

            String[] authComponents = authDeciphered.split("/");
            String userAuth = authComponents[0];
            long tRequest = Long.parseLong(authComponents[1]);

            return (userTicket.equals(userAuth) &&
                    (start <= tRequest) && (tRequest <= end) &&
                     encryption.verifyMac(mac, argument.getBytes("UTF-8"), kSession));

        }catch(Exception e){
            System.out.println("Error on verify!");
            return false;
        }
    }

    // for testing

    static void reset() {
        userRepositories.clear();
        // as specified in:
        // http://disciplinas.tecnico.ulisboa.pt/leic-sod/2014-2015/labs/proj/test.html
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("alice", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("bruno", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("carla", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("dimas", rep);
        }
    }
}
