package chatapp.classes;

import chatapp.classes.model.Message;
import chatapp.ui.mainView.MediaPlayer.MediaPlayerUI;

import java.util.HashMap;

public class Messages {
    public static HashMap<Integer, MediaPlayerUI> mediaPlayers=new HashMap<>();
    public static Message get_no_more_data_msg(){
        Message sp_msg=new Message();
        sp_msg.setId(-2);
        return sp_msg;
    }
    public static Message get_load_more_data_msg(){
        Message sp_msg=new Message();
        sp_msg.setId(-1);
        return sp_msg;
    }
    public static boolean is_no_more_data_msg(Message msg){
        return ((msg!=null)&&msg.getId()==-2);
    }
    public static boolean is_load_more_data_msg(Message msg){
        return ((msg!=null)&&msg.getId()==-1);
    }


}
