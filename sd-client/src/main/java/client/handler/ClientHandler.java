package client.handler;

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;


/**
 *  This is the handler client class of the Relay example.
 *
 *  #2 The client handler receives data from the client (via message context).
 *  #3 The client handler passes data to the server handler (via outbound SOAP message header).
 *
 *  *** GO TO server handler to see what happens next! ***
 *
 *  #10 The client handler receives data from the server handler (via inbound SOAP message header).
 *  #11 The client handler passes data to the client (via message context).
 *
 *  *** GO BACK TO client to see what happens next! ***
 */

public class ClientHandler implements SOAPHandler<SOAPMessageContext> {

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if(smc.get("ticket")==null){ return true; }

        if (outbound) {

            // put token in request SOAP header
            try {
                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                //adicionar elementos a header do SOAP
                Name ticket = se.createName("ticket", "e", "urn:ticket");
                SOAPHeaderElement ticketElement = sh.addHeaderElement(ticket);
                ticketElement.addTextNode((String) smc.get("ticket"));

                Name auth = se.createName("auth", "e", "urn:auth");
                SOAPHeaderElement authElement = sh.addHeaderElement(auth);
                authElement.addTextNode((String) smc.get("auth"));

                Name mac = se.createName("mac", "e", "urn:mac");
                SOAPHeaderElement macElement = sh.addHeaderElement(mac);
                macElement.addTextNode((String) smc.get("mac"));

            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            }

        } else {
            // inbound message

            // get token from response SOAP header
            try {
                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }



                // get first header element
                Name name = se.createName("success", "e", "urn:success");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element %s not found");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                String headerValue = element.getValue();

                smc.put("success", headerValue);
                // set property scope to application so that client class can access property
                smc.setScope("success", Scope.APPLICATION);

            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }

        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext messageContext) {
    }

}
