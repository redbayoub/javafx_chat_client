package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Group;
import chatapp.classes.model.User;
import javafx.application.Platform;
import org.controlsfx.control.GridView;

import java.util.List;

public class GroupFetcher extends Thread {
    private static GroupFetcher instance;



    private GridView<Group> group_list;
    private boolean running=false;
    private boolean paused=false;

    private GroupFetcher(GridView<Group> group_list) {
        this.group_list = group_list;
    }

    public static GroupFetcher getInstance(GridView<Group> group_list) {
        if(group_list==null&&instance==null)return null;
        else{
            if(instance==null)instance=new GroupFetcher(group_list);
            return instance;
        }
    }

    @Override
    public void run() {
        running=true;
        while(running){
            // fetch f r from server
            List<User> frs= ServerServices.get_friends();
            // add new msgs to list view
            Platform.runLater(()->{
                List<Group> groups=ServerServices.getGroups();
                group_list.getItems().setAll(groups);
            });

            try {
                sleep(Integer.parseInt(AppProperties.getProperties().getProperty("refresh.group.interval", "60").trim())*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GridView<Group> getGroup_list() {
        return group_list;
    }

    public void stopThread(){
        running=false;
    }
}
