package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.User;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;

public class FriendFetcher extends Thread {
    private JFXListView<User> friends_list_view;
    private boolean running=false;
    private boolean paused=false;

    public FriendFetcher(JFXListView<User> friends_list_view) {
        this.friends_list_view = friends_list_view;
    }

    @Override
    public void run() {
        running=true;
        while(running){
            // fetch f r from server
            List<User> frs= ServerServices.get_friends();
            // add new msgs to list view
            Platform.runLater(()->{
                User selected_item=friends_list_view.getSelectionModel().getSelectedItem();
                friends_list_view.getItems().remove(0, friends_list_view.getItems().size());
                friends_list_view.getItems().setAll(frs);
                if(selected_item!=null)
                    friends_list_view.getSelectionModel().select(selected_item);
            });

            try {
                sleep(Integer.parseInt(AppProperties.getProperties().getProperty("refresh.friend.interval", "60").trim())*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void stopThread(){
        running=false;
    }
}
