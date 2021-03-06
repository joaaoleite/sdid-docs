
package pt.ulisboa.tecnico.sdis.id.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "EmailAlreadyExists", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws")
public class EmailAlreadyExists_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private EmailAlreadyExists faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public EmailAlreadyExists_Exception(String message, EmailAlreadyExists faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public EmailAlreadyExists_Exception(String message, EmailAlreadyExists faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists
     */
    public EmailAlreadyExists getFaultInfo() {
        return faultInfo;
    }

}
