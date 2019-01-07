package chatapp.classes;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class ServerInfo {


    public static URI getBaseURI() {
        Properties appProperties=AppProperties.getProperties();
        try {
            appProperties.load(new FileInputStream("app.properties"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return UriBuilder.fromUri(appProperties.getProperty("server.uri")).build();
    }
    public static boolean isOn(){

        try{
            ClientConfig config=new ClientConfig();
            Client client= ClientBuilder.newClient(config);
            
            WebTarget service=client.target(getBaseURI());
            service.register(JacksonFeature.class);
            return (service.path("test").request(MediaType.APPLICATION_JSON).get().getStatusInfo().getFamily()== Response.Status.Family.SUCCESSFUL);
        }catch (Exception e){
            System.err.println(e);
            return false;
        }

    }
}
