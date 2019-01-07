package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;

public class RecentMessageFetcher extends Thread {

    private JFXListView<Message> recent_msgs;
    private boolean running=false;

    public RecentMessageFetcher(JFXListView<Message> recent_msgs) {
        this.recent_msgs = recent_msgs;
    }




    @Override
    public void run() {
        running=true;
        while(running){
            // fetch recent messages from server
            Platform.runLater(()->{
                List<Message> recent_msgs_list= ServerServices.getRecentMessages();
                recent_msgs.getItems().setAll(recent_msgs_list);
            });

            try {
                sleep(Integer.parseInt(AppProperties.getProperties().getProperty("refresh.recent.msg.interval", "60").trim())*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread(){
        running=false;
    }
}
