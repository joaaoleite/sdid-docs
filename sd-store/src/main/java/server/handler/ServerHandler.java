package server.handler;

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.*;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;
import static javax.xml.bind.DatatypeConverter.printHexBinary;


/**
 *  This is the handler server class of the Relay example.
 *
 *  #4 The server handler receives data from the client handler (via inbound SOAP message header).
 *  #5 The server handler passes data to the server (via message context).
 *
 *  *** GO TO server class to see what happens next! ***
 *
 *  #8 The server class receives data from the server (via message context).
 *  #9 The server handler passes data to the client handler (via outbound SOAP message header).
 *
 *  *** GO BACK TO client handler to see what happens next! ***
 */

public class ServerHandler implements SOAPHandler<SOAPMessageContext> {

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outbound) {
            // outbound message
            // put token in response SOAP header

            String successValue = (String) smc.get("success");
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
                Name success = se.createName("success", "e", "urn:success");
                SOAPHeaderElement successElement = sh.addHeaderElement(success);
                successElement.addTextNode(successValue);

            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            }


        } else {
            // inbound message

            // get token from request SOAP header
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

                

                // get SOAP header elements
                Name ticketName = se.createName("ticket", "e", "urn:ticket");
                Iterator it = sh.getChildElements(ticketName);
                if (!it.hasNext()) { System.out.println("Header element ticket not found"); return true; }
                SOAPElement ticketElement = (SOAPElement) it.next();
                String ticket = ticketElement.getValue();
                smc.put("ticket", ticket);
                smc.setScope("ticket", Scope.APPLICATION);

                // get SOAP header elements
                Name authName = se.createName("auth", "e", "urn:auth");
                it = sh.getChildElements(authName);
                if (!it.hasNext()) { System.out.println("Header element auth not found"); return true; }
                SOAPElement authElement = (SOAPElement) it.next();
                String auth = authElement.getValue();
                smc.put("auth", auth);
                smc.setScope("auth", Scope.APPLICATION);

                // get SOAP header elements
                Name macName = se.createName("mac", "e", "urn:mac");
                it = sh.getChildElements(macName);
                if (!it.hasNext()) { System.out.println("Header element mac not found"); return true; }
                SOAPElement macElement = (SOAPElement) it.next();
                String mac = macElement.getValue();
                smc.put("mac", mac);
                smc.setScope("mac", Scope.APPLICATION);

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
