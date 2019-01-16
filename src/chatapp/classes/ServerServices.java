package chatapp.classes;

import chatapp.classes.model.FriendRequest;
import chatapp.classes.model.Group;
import chatapp.classes.model.Message;
import chatapp.classes.model.User;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ServerServices {
    static {
        // to use multipart
        //config.register(MultiPartFeature.class);
        //Client client= ClientBuilder.newClient(config);
        Client client=ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .build();

        service=client.target(ServerInfo.getBaseURI());

    }
    private static WebTarget service;
    public static User add_user(User user){
        User returnedUser=null;
        try{
            // post the user to server
            Response response=service.path("users").path("signUp")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(user, MediaType.APPLICATION_JSON),Response.class);
            returnedUser=response.readEntity(User.class);
            return returnedUser;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public static boolean confirm_user(int userId,int confirmNB){
        try{
            Response response=service.path("users").path("confirm").path(Integer.toString(userId))
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(confirmNB, MediaType.APPLICATION_JSON),Response.class);
            return (response.getStatusInfo().getFamily()== Response.Status.Family.SUCCESSFUL);
        }catch (Exception e){
            System.err.println(e);
            return false;
        }
    }

    public static User login_user(String email,String password_sha1) {
        try{
            User user=new User();
            user.setEmail(email);
            user.setPassword_sha1(password_sha1);
            Response response=service.path("users").path("login")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(user, MediaType.APPLICATION_JSON),Response.class);

            return response.readEntity(User.class);
        }catch (Exception e){
            System.err.println(e);
            return null;
        }
    }

    public static void logout_user(User user){
        try{
            Response response=service.path("users").path("logout")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(user, MediaType.APPLICATION_JSON),Response.class);

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static List<User> get_people_by_usrename(String username){
        try{
            Response response=service.path("users")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .path("search")
                    .path(username)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<User> returnedList=response.readEntity(new GenericType<List<User>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static boolean send_friend_request(int freind_id){
        try{
            FriendRequest friendRequest=new FriendRequest();
            friendRequest.setSenderId(AppProperties.currUser.getId());
            friendRequest.setReceiverId(freind_id);
            // post the friend request to server
            Response response=service.path("friend_requests")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(friendRequest, MediaType.APPLICATION_JSON),Response.class);
            return response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL);
        }catch (Exception e){
            System.err.println(e);
            return false;
        }
    }

    public static User get_user_by_id(int user_id){
        try{
            Response response=service.path("users")
                    .path(Integer.toString(user_id))
                    .request(MediaType.APPLICATION_JSON)
                    .get();
           return response.readEntity(User.class);

        }catch (Exception e){
            return null;
        }
    }

    public static List<FriendRequest> get_friend_requests(){
        try{
            Response response=service.path("friend_requests")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<FriendRequest> returnedList=response.readEntity(new GenericType<List<FriendRequest>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static void accept_friend_request(FriendRequest fr){
        try{
            Response response=service.path("friend_requests")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(fr, MediaType.APPLICATION_JSON),Response.class);

        }catch (Exception e){
        }
    }

    public static void decline_friend_request(FriendRequest fr){
        try{
            Response response=service.path("friend_requests")
                    .path(Integer.toString(fr.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

        }catch (Exception e){
        }
    }

    public static List<User> get_friends() {
        try{
            Response response=service.path("friends")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<User> returnedList=response.readEntity(new GenericType<List<User>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static List<Message> getChatMessages(Message tamplate,boolean after) {
        try{
            String afterOrBefore;
            if(after) afterOrBefore="after";
            else afterOrBefore="before";
            Response response=service.path("messages")
                    .path(afterOrBefore)
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(tamplate, MediaType.APPLICATION_JSON),Response.class);
            List<Message> returnedList=response.readEntity(new GenericType<List<Message>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static List<Message> getRecentMessages() {
        try{
            Response response=service.path("messages")
                    .path("recent")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<Message> returnedList=response.readEntity(new GenericType<List<Message>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static Message send_message(Message msg){
        try{

            // post the msg request to server
            Response response=service.path("messages")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(msg, MediaType.APPLICATION_JSON),Response.class);
            return response.readEntity(Message.class);
        }catch (Exception e){
            System.out.println(e);
            return new Message();
        }
    }

    public static List<Group> getGroups() {
        try{
            Response response=service.path("groups")
                    .path("find")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<Group> returnedSet=response.readEntity(new GenericType<List<Group>>(){});
            return returnedSet;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static List<User> get_friends_by_usrename(String username){
        try{
            Response response=service.path("friends")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .path("search")
                    .path(username)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            List<User> returnedList=response.readEntity(new GenericType<List<User>>(){});
            return returnedList;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }


    public static Group create_group(Group group){
        try{

            // post the msg request to server
            Response response=service.path("groups")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(group, MediaType.APPLICATION_JSON),Response.class);
            return response.readEntity(Group.class);
        }catch (Exception e){
            System.out.println(e);
            return new Group();
        }
    }

    public static User update_user(User user){
        try{
            Response response=service.path("users")
                    .path("update")
                    .path(Integer.toString(AppProperties.currUser.getId()))
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(user, MediaType.APPLICATION_JSON),Response.class);
            return response.readEntity(User.class);
        }catch (Exception e){
            System.out.println(e);
            return new User();
        }
    }

    public static String upload_file(InputStream stream,String filename){
        try{
            //FileDataBodyPart filePart = new FileDataBodyPart("file",file);
            StreamDataBodyPart bodyPart=new StreamDataBodyPart("file", stream, filename);
            //MultiPart multipartEntity = new FormDataMultiPart().bodyPart(filePart);
            MultiPart multipartEntity = new FormDataMultiPart().bodyPart(bodyPart);
            Response response=service.path("upload/")
                    .register( MultiPartFeature.class)
                    .request()
                    .post(Entity.entity(multipartEntity,MediaType.MULTIPART_FORM_DATA),Response.class);

            stream.close();
            multipartEntity.close();
            return response.readEntity(String.class);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public static File get_file(String file_id){
        try{
            Response response=service.path("upload")
                    .path("files")
                    .path(file_id)
                    .request(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .get();
            String filename=response.getHeaderString("Content-Disposition");
            InputStream file_is=response.readEntity(InputStream.class);

            File tmp_file=new File(AppProperties.getTmp_dir(),filename);
            FilesOperations.copy_file(file_is, tmp_file);

            return tmp_file;

        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }


}
