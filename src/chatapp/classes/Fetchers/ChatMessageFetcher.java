package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.Date;
import java.util.List;

public class ChatMessageFetcher extends Thread {
    private JFXListView<Message> chat_msg_list;
    private int sender_id;
    private int group_id;

    private boolean running=false;

    private Message tamplate;
    public ChatMessageFetcher(JFXListView<Message> chat_msg_list, int sender_id, int group_id) {
        this.chat_msg_list = chat_msg_list;
        this.sender_id = sender_id;
        this.group_id = group_id;

        this.tamplate=new Message();
        tamplate.setSenderId(sender_id);
        tamplate.setReceiver_id(AppProperties.currUser.getId());
        tamplate.setGroupId(group_id);
    }




    @Override
    public void run() {
        running=true;
        Platform.runLater(() -> chat_msg_list.getItems().clear());
        while(running){
            // fetch msg from server
            Platform.runLater(()->{
                if(chat_msg_list.getItems().size()==0){
                    tamplate.setSending_date(new Date());
                    List<Message> msgs= ServerServices.getChatMessages(tamplate,false);
                    chat_msg_list.getItems().setAll(msgs);
                    chat_msg_list.scrollTo(msgs.size());

                }else{
                    tamplate.setSending_date(chat_msg_list.getItems().get(chat_msg_list.getItems().size()-1).getSending_date());
                    List<Message> msgs= ServerServices.getChatMessages(tamplate,true);
                    chat_msg_list.getItems().addAll(msgs);
                }
            });

            try {
                sleep(Integer.parseInt(AppProperties.getProperties().getProperty("refresh.chat.interval", "30").trim())*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public int getSender_id() {
        return sender_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void stopThread(){
        running=false;
    }
}
