package server;

import javax.xml.ws.Endpoint;

import server.uddinaming.UDDINaming;
import java.util.*;


public class StoreMain {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println("Argument(s) error!");
            return;
        }

        String uddiURL = args[0];
        String wsName = args[1];
        String wsURL = args[2];
        int serviceN = Integer.parseInt(args[3]);

        

        int port = Integer.parseInt(wsURL.split(":")[2].split("/")[0])+serviceN;
        String serviceURL = "http:"+wsURL.split(":")[1]+":"+port+"/"+wsURL.split(":")[2].split("/")[1];
        String serviceName = wsName+serviceN;
        String wsTestUrl = wsURL + "/test";


        Endpoint testEndpoint = null;
        UDDINaming uddiNaming = null;
        Endpoint endPoint = null;

        try {
            StoreImpl impl;
            
            if (System.getProperty("store-ws.test") != null) {
                System.out.println("Populating test data...");
            }

            impl = new StoreImpl();
        	endPoint = Endpoint.create(impl);

        	System.out.println("Starting "+serviceName);
        	endPoint.publish(serviceURL);

        	// publish to UDDI
            if (serviceURL != null) {
                System.out.println("Publishing "+serviceName+" to UDDI at "+serviceURL);
                uddiNaming = new UDDINaming(uddiURL);
                uddiNaming.rebind(serviceName, serviceURL);
            }
            impl.reset();

            /*if ("true".equalsIgnoreCase(System.getProperty("ws.test"))) {
                
                System.out.printf("Starting %s%n", wsTestUrl);
                testEndpoint = Endpoint.create(new TestControl());
                testEndpoint.publish(wsTestUrl);
            }*/

            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();

        } finally {
            try {
                if (endPoint != null) {     
                    endPoint.stop();
                    System.out.println("Stopped endpoint "+serviceName);
                }
                if (testEndpoint != null) {
                    testEndpoint.stop();
                    System.out.printf("Stopped %s%n", wsTestUrl);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when stopping: %s%n", e);
            }
            try {
                if (uddiNaming != null) {
                    uddiNaming.unbind(wsName);
                    System.out.printf("Deleted SD-Store Services");
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when deleting: %s%n", e);
            }
        }

    }

}
