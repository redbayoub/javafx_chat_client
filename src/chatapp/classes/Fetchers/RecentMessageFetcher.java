package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import chatapp.ui.mainView.Lists_Selection_Listeners;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentMessageFetcher extends AbstractFetcher {

    private JFXListView<Message> recent_msgs;

    public RecentMessageFetcher(JFXListView<Message> recent_msgs) {
        this.recent_msgs = recent_msgs;
        int period=Integer.parseInt(AppProperties.getProperties().getProperty("refresh.recent.msg.interval", "60").trim());
        scheduleAtFixedRate(period, TimeUnit.SECONDS);
    }

    @Override
    protected void run_fetcher() {
        // fetch recent messages from server
        List<Message> recent_msgs_list= ServerServices.getRecentMessages();
        Platform.runLater(()->{
            Message selected_item=recent_msgs.getSelectionModel().getSelectedItem();

            recent_msgs.getItems().setAll(recent_msgs_list);
            if(selected_item!=null){
                selected_item.setUserData(Lists_Selection_Listeners.IGNORE_SELCTION);
                recent_msgs.getSelectionModel().select(selected_item);
            }
        });
    }



}
