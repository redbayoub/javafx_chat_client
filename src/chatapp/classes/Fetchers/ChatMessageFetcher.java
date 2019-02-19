package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.Messages;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatMessageFetcher extends AbstractFetcher{
    private static JFXListView<Message> chat_msg_list;

    private int sender_id;
    private int group_id;


    private Message tamplate;
    public ChatMessageFetcher(JFXListView<Message> chat_msg_list, int sender_id, int group_id) {
        this.chat_msg_list = chat_msg_list;
        this.sender_id = sender_id;
        this.group_id = group_id;




        this.tamplate=new Message();
        tamplate.setSenderId(sender_id);
        tamplate.setReceiver_id(AppProperties.currUser.getId());
        tamplate.setGroupId(group_id);
        int period=Integer.parseInt(AppProperties.getProperties().getProperty("refresh.chat.interval", "30").trim());
        chat_msg_list.getItems().clear();
        scheduleAtFixedRate(period, TimeUnit.SECONDS);
    }

    @Override
    protected void run_fetcher() {
        // fetch msg from serve
        if(chat_msg_list.getItems().size()==0){
            tamplate.setSending_date(new Date());
            List<Message> msgs= ServerServices.getChatMessages(tamplate,false);
            System.out.println(msgs);
            if(msgs.size()==10){
                msgs.add(0,Messages.get_load_more_data_msg());
            }
            Platform.runLater(()-> {
                chat_msg_list.getItems().setAll(msgs);
                chat_msg_list.scrollTo(msgs.size()-1);
            });

        }else{
            tamplate.setSending_date(chat_msg_list.getItems().get(chat_msg_list.getItems().size()-1).getSending_date());
            List<Message> msgs= ServerServices.getChatMessages(tamplate,true);
            System.out.println(msgs);
            Platform.runLater(()->{
                chat_msg_list.getItems().addAll(msgs);
            });

        }
    }


    public int getSender_id() {
        return sender_id;
    }

    public int getGroup_id() {
        return group_id;
    }



}
