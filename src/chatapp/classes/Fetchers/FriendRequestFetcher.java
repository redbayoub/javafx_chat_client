package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.FriendRequest;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FriendRequestFetcher extends AbstractFetcher {
    private JFXListView<FriendRequest> recived_fr_list;



    public FriendRequestFetcher(JFXListView<FriendRequest> recived_fr_list) {
        this.recived_fr_list = recived_fr_list;
        int period=Integer.parseInt(AppProperties.getProperties().getProperty("refresh.friend.request.interval", "60").trim());

        scheduleAtFixedRate(period, TimeUnit.SECONDS);
    }

    @Override
    protected void run_fetcher() {
        // fetch f r from server
        List<FriendRequest> frs= ServerServices.get_friend_requests();

        // add new msgs to list view
        Platform.runLater(()->{
            recived_fr_list.getItems().setAll(frs);
        });
    }




}
