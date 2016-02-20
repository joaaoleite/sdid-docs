
package pt.ulisboa.tecnico.sdis.id.ws;

import java.util.concurrent.Future;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SDId", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws")
@HandlerChain(file = "SDId_handler.xml")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SDId {


    /**
     * 
     * @param emailAddress
     * @param userId
     * @return
     *     returns javax.xml.ws.Response<pt.ulisboa.tecnico.sdis.id.ws.CreateUserResponse>
     */
    @WebMethod(operationName = "createUser")
    @RequestWrapper(localName = "createUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUser")
    @ResponseWrapper(localName = "createUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUserResponse")
    public Response<CreateUserResponse> createUserAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "emailAddress", targetNamespace = "")
        String emailAddress);

    /**
     * 
     * @param emailAddress
     * @param asyncHandler
     * @param userId
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "createUser")
    @RequestWrapper(localName = "createUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUser")
    @ResponseWrapper(localName = "createUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUserResponse")
    public Future<?> createUserAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "emailAddress", targetNamespace = "")
        String emailAddress,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<CreateUserResponse> asyncHandler);

    /**
     * 
     * @param emailAddress
     * @param userId
     * @throws InvalidUser_Exception
     * @throws UserAlreadyExists_Exception
     * @throws InvalidEmail_Exception
     * @throws EmailAlreadyExists_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "createUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUser")
    @ResponseWrapper(localName = "createUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.CreateUserResponse")
    public void createUser(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "emailAddress", targetNamespace = "")
        String emailAddress)
        throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception
    ;

    /**
     * 
     * @param userId
     * @return
     *     returns javax.xml.ws.Response<pt.ulisboa.tecnico.sdis.id.ws.RenewPasswordResponse>
     */
    @WebMethod(operationName = "renewPassword")
    @RequestWrapper(localName = "renewPassword", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPassword")
    @ResponseWrapper(localName = "renewPasswordResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPasswordResponse")
    public Response<RenewPasswordResponse> renewPasswordAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId);

    /**
     * 
     * @param asyncHandler
     * @param userId
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "renewPassword")
    @RequestWrapper(localName = "renewPassword", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPassword")
    @ResponseWrapper(localName = "renewPasswordResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPasswordResponse")
    public Future<?> renewPasswordAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<RenewPasswordResponse> asyncHandler);

    /**
     * 
     * @param userId
     * @throws UserDoesNotExist_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "renewPassword", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPassword")
    @ResponseWrapper(localName = "renewPasswordResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RenewPasswordResponse")
    public void renewPassword(
        @WebParam(name = "userId", targetNamespace = "")
        String userId)
        throws UserDoesNotExist_Exception
    ;

    /**
     * 
     * @param userId
     * @return
     *     returns javax.xml.ws.Response<pt.ulisboa.tecnico.sdis.id.ws.RemoveUserResponse>
     */
    @WebMethod(operationName = "removeUser")
    @RequestWrapper(localName = "removeUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUser")
    @ResponseWrapper(localName = "removeUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUserResponse")
    public Response<RemoveUserResponse> removeUserAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId);

    /**
     * 
     * @param asyncHandler
     * @param userId
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "removeUser")
    @RequestWrapper(localName = "removeUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUser")
    @ResponseWrapper(localName = "removeUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUserResponse")
    public Future<?> removeUserAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<RemoveUserResponse> asyncHandler);

    /**
     * 
     * @param userId
     * @throws UserDoesNotExist_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "removeUser", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUser")
    @ResponseWrapper(localName = "removeUserResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RemoveUserResponse")
    public void removeUser(
        @WebParam(name = "userId", targetNamespace = "")
        String userId)
        throws UserDoesNotExist_Exception
    ;

    /**
     * 
     * @param reserved
     * @param userId
     * @return
     *     returns javax.xml.ws.Response<pt.ulisboa.tecnico.sdis.id.ws.RequestAuthenticationResponse>
     */
    @WebMethod(operationName = "requestAuthentication")
    @RequestWrapper(localName = "requestAuthentication", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthentication")
    @ResponseWrapper(localName = "requestAuthenticationResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthenticationResponse")
    public Response<RequestAuthenticationResponse> requestAuthenticationAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "reserved", targetNamespace = "")
        byte[] reserved);

    /**
     * 
     * @param reserved
     * @param asyncHandler
     * @param userId
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "requestAuthentication")
    @RequestWrapper(localName = "requestAuthentication", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthentication")
    @ResponseWrapper(localName = "requestAuthenticationResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthenticationResponse")
    public Future<?> requestAuthenticationAsync(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "reserved", targetNamespace = "")
        byte[] reserved,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<RequestAuthenticationResponse> asyncHandler);

    /**
     * 
     * @param reserved
     * @param userId
     * @return
     *     returns byte[]
     * @throws AuthReqFailed_Exception
     */
    @WebMethod
    @WebResult(name = "credentials", targetNamespace = "")
    @RequestWrapper(localName = "requestAuthentication", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthentication")
    @ResponseWrapper(localName = "requestAuthenticationResponse", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", className = "pt.ulisboa.tecnico.sdis.id.ws.RequestAuthenticationResponse")
    public byte[] requestAuthentication(
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "reserved", targetNamespace = "")
        byte[] reserved)
        throws AuthReqFailed_Exception
    ;

}