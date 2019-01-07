package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.FriendRequest;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;

public class FriendRequestFetcher extends Thread {
    private JFXListView<FriendRequest> recived_fr_list;
    private boolean running=false;
    private boolean paused=false;

    public FriendRequestFetcher(JFXListView<FriendRequest> recived_fr_list) {
        this.recived_fr_list = recived_fr_list;
    }

    @Override
    public void run() {
        running=true;
        while(running){
            // fetch f r from server
            List<FriendRequest> frs= ServerServices.get_friend_requests();

            // add new msgs to list view
            Platform.runLater(()->{
                recived_fr_list.getItems().setAll(frs);
            });

            try {
                sleep(Integer.parseInt(AppProperties.getProperties().getProperty("refresh.friend.request.interval", "60").trim())*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void stopThread(){
        running=false;
    }
}
