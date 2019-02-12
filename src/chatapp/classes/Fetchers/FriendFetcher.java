package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.User;
import chatapp.ui.mainView.Lists_Selection_Listeners;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FriendFetcher extends AbstractFetcher {
    private JFXListView<User> friends_list_view;


    public FriendFetcher(JFXListView<User> friends_list_view) {
        this.friends_list_view = friends_list_view;
        int period=Integer.parseInt(AppProperties.getProperties().getProperty("refresh.friend.interval", "60").trim());
        scheduleAtFixedRate(period, TimeUnit.SECONDS);
    }


    @Override
    protected void run_fetcher() {
        // fetch f r from server
        List<User> frs= ServerServices.get_friends();
        // add new msgs to list view
        Platform.runLater(()->{
            User selected_item=friends_list_view.getSelectionModel().getSelectedItem();
            friends_list_view.getItems().setAll(frs);
            if(selected_item!=null){
                selected_item.setUserData(Lists_Selection_Listeners.IGNORE_SELCTION);
                friends_list_view.getSelectionModel().select(selected_item);
            }

        });
    }
}
