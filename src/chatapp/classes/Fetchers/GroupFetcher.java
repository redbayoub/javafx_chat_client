package chatapp.classes.Fetchers;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Group;
import javafx.application.Platform;
import org.controlsfx.control.GridView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupFetcher extends AbstractFetcher {
    private static GroupFetcher instance;
    private GridView<Group> group_list;

    private GroupFetcher(GridView<Group> group_list) {
        this.group_list = group_list;

        int period=Integer.parseInt(AppProperties.getProperties().getProperty("refresh.group.interval", "60").trim());
        scheduleAtFixedRate(period, TimeUnit.SECONDS);
    }

    public static GroupFetcher getInstance(GridView<Group> group_list) {
        if(group_list==null&&instance==null)return null;
        else{
            if(instance==null)instance=new GroupFetcher(group_list);
            return instance;
        }
    }

    @Override
    protected void run_fetcher() {
        List<Group> groups=ServerServices.getGroups();
        Platform.runLater(()->{
            group_list.getItems().setAll(groups);
        });
    }


    public GridView<Group> getGroup_list() {
        return group_list;
    }


}
