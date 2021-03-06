
package pt.ulisboa.tecnico.sdis.store.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "docDoesNotExist", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws")
public class DocDoesNotExist_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private DocDoesNotExist faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public DocDoesNotExist_Exception(String message, DocDoesNotExist faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public DocDoesNotExist_Exception(String message, DocDoesNotExist faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist
     */
    public DocDoesNotExist getFaultInfo() {
        return faultInfo;
    }

}
