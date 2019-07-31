package chatapp.ui.create_group;

import chatapp.classes.AppProperties;
import chatapp.classes.Fetchers.GroupFetcher;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Group;
import chatapp.classes.model.User;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateGroup  implements Initializable {

    @FXML
    private JFXTextField group_name;

    @FXML
    private JFXListView<User> member_list;

    @FXML
    private JFXTextField username_to_find;

    @FXML
    private JFXListView<User> friends_list;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setup_member_list();
        setup_friends_list();
        setup_username_to_find();
    }


    private void setup_friends_list() {
        friends_list.getStylesheets().add(getClass().getResource("member_list_style.css").toExternalForm());
        friends_list.setVerticalGap(10.0);
        //friends_list.setCellHorizontalMargin(10.0);
        friends_list.setCellFactory(new Callback<ListView<User>, ListCell<User>>(){
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new MemberListCell();
            }
        });
        friends_list.setExpanded(true);
        friends_list.depthProperty().set(1);
    }

    private void setup_username_to_find() {
        username_to_find.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.isEmpty()){
                    friends_list.getItems().clear();
                }else {
                    friends_list.getItems().clear();

                    for (User item: ServerServices.get_friends_by_usrename(newValue)){
                        if(!member_list.getItems().contains(item)){
                            friends_list.getItems().add(item);
                        }
                    }
                }
            }
        });
    }

    private void setup_member_list() {
        member_list.getStylesheets().add(getClass().getResource("member_list_style.css").toExternalForm());
        member_list.setCellFactory(new Callback<ListView<User>, ListCell<User>>(){
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new MemberListCell();
            }
        });
        member_list.setExpanded(true);
        member_list.depthProperty().set(1);
    }

    @FXML
    void add_to_mem_list(ActionEvent event) {
        if(friends_list.getSelectionModel().isEmpty())return;
        member_list.getItems().add(friends_list.getSelectionModel().getSelectedItem());
        friends_list.getItems().remove(friends_list.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void remove_from_mem_list(ActionEvent event) {
        if(member_list.getSelectionModel().isEmpty())return;
        friends_list.getItems().add(member_list.getSelectionModel().getSelectedItem());
        member_list.getItems().remove(member_list.getSelectionModel().getSelectedIndex());
    }


    @FXML
    void cancel(ActionEvent event) {
        close();
    }

    @FXML
    void create_group(ActionEvent event) {
        member_list.getItems().add(AppProperties.currUser); // Include the Curr User
        if(group_name.getText().isEmpty()||member_list.getItems().size()<3){//
            // display error
        }else{ //create group
            Group group=new Group();
            group.setName(group_name.getText());
            group.setMembers(member_list.getItems());

            Group ret_group=ServerServices.create_group(group);
            if(ret_group==null||ret_group.getGroup_id()==0){ // show error

            }else{ // group successfully added
                int groups_list_size=GroupFetcher.getInstance(null).getGroup_list().getItems().size();
                GroupFetcher.getInstance(null).getGroup_list().getItems().add(groups_list_size-1, ret_group);
                close();
            }
        }
    }


    private void close(){
        group_name.getScene().getWindow().hide();
    }
}
