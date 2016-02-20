package client;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import pt.ulisboa.tecnico.sdis.id.ws.*;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import client.uddi.*;
import client.handler.*;
import client.Encryption;
import client.exception.*;
import java.io.File;

public class SDMainCli {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 4) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", SDMainCli.class.getName());
            return;
        }

        String uddiURL = args[0];
        String nameId = args[1];
        String nameStore = args[2];
        int nReplics = Integer.parseInt(args[3]);

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", nameId);
        String endpointAddressId = uddiNaming.lookup(nameId);

        if (endpointAddressId == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddressId);
        }

        System.out.println("Creating stub ...");
        SDId_Service service = new SDId_Service();
        SDId port = service.getSDIdImplPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddressId);

        System.out.println("Remote call ...");

        /* Client */

        Scanner input;
        int command;

        while(true){
            
            System.out.println("SD-ID");
            System.out.println("1 - Create User");
            System.out.println("2 - Renew Password");
            System.out.println("3 - Remove User");
            System.out.println("4 - Request Authentication");

            System.out.println("0 - Exit");
            System.out.print("Option: ");

            input = new Scanner(System.in);
            command = input.nextInt();

            if(command == 1){

                System.out.println("---Create User---");

                System.out.print("User: ");
                input = new Scanner(System.in);
                String user = input.nextLine();
                user=user.toLowerCase();

                System.out.print("EmailAddress: ");
                input = new Scanner(System.in);
                String email = input.nextLine();

                try{
                    port.createUser(user, email);
                    System.out.println("Sucess!");

                } catch(EmailAlreadyExists_Exception e){
                    System.out.println(e.getMessage());

                } catch(UserAlreadyExists_Exception e){
                    System.out.println(e.getMessage());

                } catch(InvalidEmail_Exception e){
                    System.out.println(e.getMessage());

                } catch(InvalidUser_Exception e){
                    System.out.println(e.getMessage());
                }        
            }

            else if(command == 2){

                System.out.println("---Renew Password---");

                System.out.print("User: ");
                input = new Scanner(System.in);
                String user = input.nextLine();
                user=user.toLowerCase();

                try{
                    port.renewPassword(user);
                    System.out.println("Sucess!");
                } catch(UserDoesNotExist_Exception e){
                    System.out.println(e.getMessage());
                }
            }
            
            else if(command == 3){

                System.out.println("---Remove User---");

                System.out.print("User: ");
                input = new Scanner(System.in);
                String user = input.nextLine();
                user=user.toLowerCase();

                try{
                    port.removeUser(user);
                    System.out.println("Sucess!");
                } catch(UserDoesNotExist_Exception e){
                    System.out.println(e.getMessage());
                }
            }

            else if(command == 4){

                System.out.println("---Request Authentication---");

                System.out.print("User: ");
                input = new Scanner(System.in);
                String user = input.nextLine();

                System.out.print("Password: ");
                input = new Scanner(System.in);
                String pass = input.nextLine();
                byte[] pass_bytes = pass.getBytes("UTF-8");

                Encryption encryption = new Encryption();

                String service_str = "SDStore";
                byte[] service_bytes = service_str.getBytes("UTF-8");
                byte[] number_bytes = encryption.generateKey();

                byte[] toSend = new byte[service_bytes.length + number_bytes.length];
                System.arraycopy(service_bytes, 0, toSend, 0, service_bytes.length);
                System.arraycopy(number_bytes, 0, toSend, service_bytes.length, number_bytes.length);   

                try{

                    byte[] response = port.requestAuthentication(user, toSend);
                    byte[] kClient = encryption.hash(pass_bytes);
                    byte[] first = Arrays.copyOfRange(response, 0, 48);
                    byte[] first_deciphered = encryption.decipher(first, kClient);
                    byte[] kSession = Arrays.copyOfRange(first_deciphered, 0, 16);
                    byte[] number_received = Arrays.copyOfRange(first_deciphered, 16, 32);
                    byte[] ticket = Arrays.copyOfRange(response, 48, response.length);

                    if(Arrays.equals(number_bytes, number_received)){

                        System.out.println("Authentication Sucessful");

                        FrontEnd fe = new FrontEnd(nReplics, service_str, uddiURL, user, ticket, kSession);

                        while(true){

                            System.out.println("SD-STORE");
                            System.out.println("1 - Create Document");
                            System.out.println("2 - List Documents");
                            System.out.println("0 - Exit");
                            System.out.print("Option: ");

                            
                            input = new Scanner(System.in);
                            command = input.nextInt();

                            if(command == 1){

                                System.out.println("---Create Doc---");
                                System.out.print("DocId: ");

                                input = new Scanner(System.in);
                                String docId = input.nextLine();

                                try{
                                    fe.createDoc(docId);
                                    System.out.println("Sucess!");
                                //} catch(UserDoesNotExist_Exception e){
                                //    System.out.println(e.getMessage());
                                } catch(DocAlreadyExists_Exception e1){
                                    System.out.println(e1.getMessage());

                                } catch(VerificationFailedException e2){
                                    System.out.println(e2.getMessage());
                                }     
                            }

                            else if(command == 2){

                                System.out.println("---List Docs---");

                                try{
                                    List<String> lista = fe.listDocs();
                                    System.out.println("ListDocs("+user+"):");
                                    for(int i=0; i<lista.size(); i++){
                                        System.out.println(i+": "+lista.get(i));
                                    }
                                    System.out.println("Sucess!");
                                //} catch(UserDoesNotExist_Exception e){
                                  //  System.out.println(e.getMessage());
                                } catch(VerificationFailedException e){
                                    System.out.println(e.getMessage());
                                } catch(Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }
                            else if(command == 0)
                                break;

                            else
                                System.out.println("Invalid Command!");
                        }
                    }

                    else
                        System.out.println("Authentication Failed");

                } catch(AuthReqFailed_Exception e){
                    System.out.println(e.getMessage());

                } catch(Exception e){
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                    System.out.println("Authentication Failed");
                }
            }

            else if(command == 0)
                break;

            else
                System.out.println("Invalid Command!");
        }

        System.out.println("Bye!");          
    }
}
