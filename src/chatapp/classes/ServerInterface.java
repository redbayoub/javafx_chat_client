package chatapp.classes;

import chatapp.classes.model.Message;
import chatapp.classes.model.User;

import java.util.List;

public interface ServerInterface {
    boolean send_message(Message msg);
    List<Message> receive_latest_messages();
    List<User> get_users();


}
