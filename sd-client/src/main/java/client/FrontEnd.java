package client;
import java.util.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import javax.xml.ws.*;
import javax.jws.WebService;
import pt.ulisboa.tecnico.sdis.store.ws.*;
import client.uddi.*;

import javax.annotation.Resource;
import javax.jws.*;
import client.handler.*;
import client.exception.*;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.ExecutionException;
import java.io.FileNotFoundException;

import java.net.ConnectException;



public class FrontEnd {

    private List<SDStore> sdstores = new ArrayList<SDStore>();
    private int _nReplics;
    private byte[] _ticket;
    private byte[] _kSession;
    private String _user;

    public FrontEnd(int nReplics, String serviceName, String uddiURL, String user, byte[] ticket, byte[] kSession) throws Exception{

        _nReplics = nReplics;
        _user = user;
        _ticket = ticket;
        _kSession = kSession;

        String name="";

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        for(int i=0; i<_nReplics; i++){
            
            name = serviceName + i;
             
            System.out.printf("Looking for '%s'%n", name);
            String endpointAddressStore = uddiNaming.lookup(name);

            if (endpointAddressStore == null) {
                System.out.println("Not found!");
                return;
            } else {
                System.out.printf("Found %s%n", endpointAddressStore);
            }

            SDStore_Service service = new SDStore_Service();
            SDStore port = service.getSDStoreImplPort();

            
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY,endpointAddressStore);

            sdstores.add(port);
        }
        if(sdstores.size()<((nReplics/2)+1)){
            System.out.println("Error creating Front End!");
            throw new Exception("Error creating Front End!");
        }
    }

    
    private void setSoapHeader(Map<String, Object> requestContext, String argument)
        throws VerificationFailedException{

        try{

            Encryption encryption = new Encryption();

            long epoch = System.currentTimeMillis();
            String auth = _user + "/" + Long.toString(epoch);

            byte[] auth_bytes = auth.getBytes("UTF-8");
            byte[] authCiphered = encryption.cipher(auth_bytes, _kSession);

            byte[] argument_bytes = argument.getBytes("UTF-8");
            byte[] macCiphered = encryption.makeMac(argument_bytes, _kSession);

            String ticket_str = printBase64Binary(_ticket);
            String auth_str = printBase64Binary(authCiphered);
            String mac_str = printBase64Binary(macCiphered);

            requestContext.put("ticket",ticket_str);
            requestContext.put("auth",auth_str);
            requestContext.put("mac",mac_str);

        }catch (Exception e){
            System.out.println("Error on Set Soap Header");
        }
    }

    public List<String> listDocs() throws UserDoesNotExist_Exception, VerificationFailedException{

        ArrayList<Response<ListDocsResponse>> responses = new ArrayList<Response<ListDocsResponse>>();
        int numResponses = 0;
        List<List<String>> lista = new ArrayList<List<String>>();
        String success = "false";

        for(int i=0; i<_nReplics; i++){
            SDStore sdstore = sdstores.get(i);
            
            BindingProvider bindingProvider = (BindingProvider) sdstore;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();  
            setSoapHeader(requestContext, _user);

            responses.add(sdstore.listDocsAsync(_user));
        }

        while(numResponses<((_nReplics/2)+1)){

            for(int i=0;i<_nReplics; i++){

                if(responses.get(i)==null){ continue; }

                if(responses.get(i).isDone()){

                    numResponses++;
                    try{ 
                        List<String> tmpList = responses.get(i).get().getDocumentId();
                        
                        Map<String, Object> responseContext = responses.get(i).getContext();
                        success = (String) responseContext.get("success");
                 
                        responses.set(i,null);

                        if(!success.equals("true")){
                            throw new VerificationFailedException("Verification Failed");
                        }else{
                            lista.add(tmpList);
                        }
                    }
                    catch(InterruptedException e){ 
                        responses.set(i,null);
                    }
                    catch(ExecutionException ee){ 
                        responses.set(i,null);
                        Throwable cause = ee.getCause();

                        if(cause.getClass().getName().contains("UserDoesNotExist_Exception")){
                            UserDoesNotExist faultInfo = new UserDoesNotExist();
                            faultInfo.setUserId(_user);
                            throw new UserDoesNotExist_Exception("User does not exist", faultInfo);   
                        }
                    }
                }
            }
        }    

        List<String> merge = new ArrayList<String>();
        for(int i = 0; i<lista.size(); i++){
            merge.addAll(lista.get(i));
        }
        Set<String> hash = new HashSet<String>();
        hash.addAll(merge);
        merge.clear();
        merge.addAll(hash);

        return merge;

    }

    public void createDoc(String docId) throws VerificationFailedException,DocAlreadyExists_Exception{

        ArrayList<Response<CreateDocResponse>> responses = new ArrayList<Response<CreateDocResponse>>();
        int numResponses = 0;
        String success = "false";

        String argument = _user + "/" + docId;

        for(int i=0; i<_nReplics; i++){

            SDStore sdstore = sdstores.get(i);
            
            BindingProvider bindingProvider = (BindingProvider) sdstore;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();  
            setSoapHeader(requestContext, argument);

            DocUserPair pair = new DocUserPair();
            pair.setUserId(_user);
            pair.setDocumentId(docId);

            responses.add(sdstore.createDocAsync(pair));
        }

        while(numResponses<((_nReplics/2)+1)){

            for(int i=0; (i<_nReplics); i++){

                
                    if(responses.get(i)==null){ continue; }

                    if(responses.get(i).isDone()){
                        numResponses++;

                        try{
                            responses.get(i).get();

                            Map<String, Object> responseContext = responses.get(i).getContext();
                            success = (String) responseContext.get("success");
                     
                            responses.set(i,null);
                        
                            if(!success.equals("true")){
                                throw new VerificationFailedException("Verification Failed");
                            }
                        }catch(InterruptedException e){ 
                            responses.set(i,null);
                        }
                        catch(ExecutionException ee){ 
                            responses.set(i,null);
                            Throwable cause = ee.getCause();

                            if(cause.getClass().getName().equals("pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception")){
                                DocAlreadyExists faultInfo = new DocAlreadyExists();
                                faultInfo.setDocId(docId);
                                throw new DocAlreadyExists_Exception("Document already exists", faultInfo);  
                            }
                        }
                    }


            }
        }    
    }



}