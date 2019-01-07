package chatapp.ui.mainView;

import chatapp.classes.AppProperties;
import chatapp.classes.EmailConfirmation;
import chatapp.classes.Encryption;
import chatapp.classes.Fetchers.*;
import chatapp.classes.ServerServices;
import chatapp.classes.model.FriendRequest;
import chatapp.classes.model.Group;
import chatapp.classes.model.Message;
import chatapp.classes.model.User;
import chatapp.ui.dialogs.Dialogs;
import chatapp.ui.mainView.ListCells.*;
import com.jfoenix.controls.*;
import eu.hansolo.enzo.notification.Notification;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainView implements Initializable {


    @FXML
    private StackPane root_stack_pane;

    @FXML
    private ImageView acount_avatar;

    @FXML
    private Text account_username;

    @FXML
    private AnchorPane main_root_pane;

    @FXML
    private Text tab_name;

    @FXML
    private JFXListView<Message> recent_msgs_list;

    @FXML
    private JFXListView<User> freinds_list;

    @FXML
    private HBox msg_typing_pane;

    @FXML
    private JFXTextArea send_text_content;

    @FXML
    private HBox root_chat_tools_pane;


    @FXML
    private JFXButton add_text_btn;

    @FXML
    private JFXButton add_image_btn;

    @FXML
    private JFXButton add_audio_btn;

    @FXML
    private JFXButton add_video_btn;

    @FXML
    private JFXButton add_file_btn;

    @FXML
    private JFXButton send_btn;

    @FXML
    private AnchorPane people_root_pane;

    @FXML
    private AnchorPane nothing_to_showPane;

    @FXML
    private JFXTextField username_to_find;
    @FXML
    private JFXListView<User> people_list;
    @FXML
    private JFXListView<Message> chat_msg_list;

    @FXML
    private JFXListView<FriendRequest> recived_friends_requests;

    @FXML
    private GridView<Group> groups_list;

    private ChatMessageFetcher chatMessageFetcher;
    private RecentMessageFetcher recentMessageFetcher;
    private FriendRequestFetcher friendRequestFetcher;
    private FriendFetcher friendFetcher;
    private GroupFetcher groupFetcher;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(AppProperties.currUser.getId()==0||AppProperties.currUser.isIs_connected()==false){
            connect_user();
        }
        account_username.setText(AppProperties.currUser.getUsername());
        if(AppProperties.currUser.getProfile_image_path()!=null && !AppProperties.currUser.getProfile_image_path().isEmpty()){
            try {
                acount_avatar.setImage(new Image(new FileInputStream(ServerServices.get_file(AppProperties.currUser.getProfile_image_path()))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        setup_recent_msg_list();
        setup_username_to_find();
        setup_recived_friends_requests_list();
        setup_freinds_list();
        setup_chat_msg_list();
        setup_groups_grid_view();

        // this has to be if the end of the constractor
        recentMessageFetcher=new RecentMessageFetcher(recent_msgs_list);
        recentMessageFetcher.start();
    }

    private void setup_groups_grid_view() {
        groups_list.setCellFactory(new Callback<GridView<Group>, GridCell<Group>>() {
            @Override
            public GridCell<Group> call(GridView<Group> param) {
                GroupListCell cell=new GroupListCell();
                // on item clicked
                cell.setOnMouseClicked(event -> {
                    Group group=null;
                    group=cell.getItem();
                    if(group==null||group.getGroup_id()==0)return;
                    if(group.getGroup_id()==-1) { // add group dialog
                        AppProperties.get_screen("Create Group", "/chatapp/ui/create_group/create_group.fxml").show();
                    }else{
                        nothing_to_showPane.setVisible(false);
                        if(chatMessageFetcher!=null){
                            if(chatMessageFetcher.getGroup_id()==group.getGroup_id()){
                                // DO Nothing
                            }else{
                                chatMessageFetcher.stopThread();
                                chatMessageFetcher=null;
                            }
                            chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, 0, group.getGroup_id());
                            chatMessageFetcher.start();
                        }else{
                            chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, 0,group.getGroup_id());
                            chatMessageFetcher.start();
                        }
                    }


                });
                return cell;
            }
        });
        groups_list.setCellWidth(100);
        groups_list.setCellHeight(110);


    }

    private void setup_chat_msg_list() {
        chat_msg_list.getStylesheets().add(getClass().getResource("ListStyles/chat_msg_list_style.css").toExternalForm());
        chat_msg_list.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ChatMessageListCell();
            }
        });

    }

    private void setup_freinds_list() {
        freinds_list.getStylesheets().add(getClass().getResource("ListStyles/friends_list_style.css").toExternalForm());
        freinds_list.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new FriendListCell();
            }
        });
        freinds_list.setExpanded(true);
        freinds_list.depthProperty().set(1);
        // on item clicked
        freinds_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            User user=null;
            if(newValue!=null)user=newValue;
            else{user=oldValue;}
            if(user==null)return;
            nothing_to_showPane.setVisible(false);
            if(chatMessageFetcher!=null){
                if(chatMessageFetcher.getSender_id()==user.getId()){
                    // DO Nothing
                }else{
                    chatMessageFetcher.stopThread();
                }
                chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, user.getId(), 0);
                chatMessageFetcher.start();
            }else{
                chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, user.getId(), 0);
                chatMessageFetcher.start();
            }


        });
    }

    private void setup_recived_friends_requests_list() {
        recived_friends_requests.setCellFactory(new Callback<ListView<FriendRequest>, ListCell<FriendRequest>>() {
            @Override
            public ListCell<FriendRequest> call(ListView<FriendRequest> param) {
                return new FriendRequestCell();
            }
        });
        recived_friends_requests.setExpanded(true);
        recived_friends_requests.depthProperty().set(1);
    }

    private void setup_username_to_find() {
        setup_people_list();
        username_to_find.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.isEmpty()){
                    people_list.getItems().clear();
                }else {
                    people_list.getItems().clear();

                    for (User item:ServerServices.get_people_by_usrename(newValue))
                        people_list.getItems().add(item);
                }
            }
        });
    }

    private void setup_people_list() {
        people_list.setCellFactory(new Callback<ListView<User>, ListCell<User>>(){
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new PeopleListCell();
            }
        });
        people_list.setExpanded(true);
        people_list.depthProperty().set(1);
    }

    private void setup_recent_msg_list() {
        recent_msgs_list.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>(){
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new RecentListCell();
            }
        });
        recent_msgs_list.setExpanded(true);
        recent_msgs_list.depthProperty().set(1);
        recent_msgs_list.getStylesheets().add(getClass().getResource("ListStyles/recent_list_style.css").toExternalForm());
        // on item clicked
        recent_msgs_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Message msg=null;
            if(newValue!=null)msg=newValue;
            else{msg=oldValue;};
            if(msg==null)return;
            nothing_to_showPane.setVisible(false);
            if(chatMessageFetcher!=null){
                if(chatMessageFetcher.getSender_id()==msg.getSenderId() || chatMessageFetcher.getGroup_id()==msg.getGroupId()){
                    // DO Nothing
                }else{
                    chatMessageFetcher.stopThread();
                }
                chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, msg.getSenderId(), msg.getGroupId());
                chatMessageFetcher.start();
            }else{
                chatMessageFetcher=new ChatMessageFetcher(chat_msg_list, msg.getSenderId(), msg.getGroupId());
                chatMessageFetcher.start();
            }


        });
    }

    @FXML
    void logout(ActionEvent event) {
        ServerServices.logout_user(AppProperties.currUser);
        close();
    }

    @FXML
    void ask_typing_msg(MouseEvent event) {
        root_chat_tools_pane.setVisible(false);
        msg_typing_pane.setVisible(true);
        send_text_content.setText("");
        send_text_content.requestFocus();
    }

    @FXML
    void send_text_msg(ActionEvent event) {
        if(!send_text_content.getText().isEmpty()){
            Message message=new Message();
            message.setSenderId(AppProperties.currUser.getId());
            message.setReceiver_id(chatMessageFetcher.getSender_id());
            message.setGroupId(chatMessageFetcher.getGroup_id());
            message.setFile(false);
            message.setContent(send_text_content.getText());
            Message recived_msg=ServerServices.send_message(message);
            if(recived_msg!=null && recived_msg.getId()!=0){
                chat_msg_list.getItems().add(recived_msg);
            }
        }
    }

    @FXML
    void return_to_root_chat_tools(ActionEvent event) {
        root_chat_tools_pane.setVisible(true);
        msg_typing_pane.setVisible(false);
        send_text_content.setText("");
    }

   /* void send(ActionEvent event) {
        if(!send_text_content.getText().isEmpty()){
            Message message=new Message();
            message.setSenderId(AppProperties.currUser.getId());
            message.setReceiver_id(chatMessageFetcher.getSender_id());
            message.setGroupId(chatMessageFetcher.getGroup_id());
            message.setFile(false); // this can be change according to selected tab
            message.setContent(send_text_content.getText());
            Message recived_msg=ServerServices.send_message(message);
            if(recived_msg!=null && recived_msg.getId()!=0){
                chat_msg_list.getItems().add(recived_msg);
            }
        }
    }*/

    @FXML
    void show_edit_acount(ActionEvent event) {
        if(!edit_profile_pane.isVisible()){
            people_root_pane.setVisible(false);
            main_root_pane.setVisible(false);
            edit_profile_pane.setVisible(true);
            initEditPane();
        }
    }

    @FXML
    void show_frindes(ActionEvent event) {
        if(friendRequestFetcher==null){
            friendRequestFetcher=new FriendRequestFetcher(recived_friends_requests);
            friendRequestFetcher.start();
        }
        if(friendFetcher==null){
            friendFetcher=new FriendFetcher(freinds_list);
            friendFetcher.start();
        }
        if(!main_root_pane.isVisible()){
            people_root_pane.setVisible(false);
            edit_profile_pane.setVisible(false);
            main_root_pane.setVisible(true);
        }
        tab_name.setText("Friends");
        freinds_list.setVisible(true);
        freinds_list.toFront();
    }

    @FXML
    void show_groups(ActionEvent event) {
        if(groupFetcher==null){
            groupFetcher=GroupFetcher.getInstance(groups_list);
            groupFetcher.start();
        }
        if(!main_root_pane.isVisible()){
            people_root_pane.setVisible(false);
            edit_profile_pane.setVisible(false);
            main_root_pane.setVisible(true);
        }
        tab_name.setText("Groups");
        groups_list.setVisible(true);
        groups_list.toFront();
    }

    @FXML
    void show_recent(ActionEvent event) {
        if(!main_root_pane.isVisible()){
            people_root_pane.setVisible(false);
            edit_profile_pane.setVisible(false);
            main_root_pane.setVisible(true);
        }
        tab_name.setText("Recent Messages");
        recent_msgs_list.setVisible(true);
        recent_msgs_list.toFront();
    }

    @FXML
    void show_people_pane(ActionEvent event) {
        if(!people_root_pane.isVisible()){
            main_root_pane.setVisible(false);
            edit_profile_pane.setVisible(false);
            people_root_pane.setVisible(true);
        }
    }

    @FXML
    void show_settings(ActionEvent event) {
    }


    private void connect_user() {
        User retUser= ServerServices.login_user(AppProperties.currUser.getEmail(), AppProperties.currUser.getPassword_sha1());
        if(retUser!=null&&retUser.getId()!=0){
            AppProperties.currUser=retUser;
            if(!retUser.isIs_confirmed()){
                AppProperties.get_screen("Chat App", "/chatapp/ui/mainView/main_view.fxml").show();
                close();
            }
        }else{ // show error msg (User Not Found)
            // handle this case
            close();
        }

    }


    private void close() {
        System.exit(1);
    }

    // ========================== Edit Profile Pane =======================================
    @FXML
    private AnchorPane edit_profile_pane;

    @FXML
    private JFXTextField edit_username;

    @FXML
    private JFXTextField edit_email;

    @FXML
    private ImageView edit_profile_image;

    @FXML
    private JFXPasswordField edit_password;

    @FXML
    private JFXPasswordField edit_re_password;

    @FXML
    private JFXButton save_changes_btn;

    private InvalidationListener data_change_listener;

    private void initEditPane(){
        edit_username.setText(AppProperties.currUser.getUsername());
        edit_email.setText(AppProperties.currUser.getEmail());
        edit_profile_image.setAccessibleText(null);

        if(AppProperties.currUser.getProfile_image_path()!=null && !AppProperties.currUser.getProfile_image_path().isEmpty()) {
            try {
                edit_profile_image.setImage((new Image(new FileInputStream(ServerServices.get_file(AppProperties.currUser.getProfile_image_path())))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            edit_profile_image.setImage((new Image(getClass().getClassLoader().getResourceAsStream("chatapp/images/defualt_user_avatar.png"))));
        }

        //configure change listener
        data_change_listener=(observable) -> {
            if(
                    edit_username.getText().equals(AppProperties.currUser.getUsername()) &&
                    edit_email.getText().equals(AppProperties.currUser.getEmail()) &&
                    edit_password.getText().isEmpty() &&
                    ( edit_profile_image.getAccessibleText()==null || edit_profile_image.getAccessibleText().isEmpty() )
            ){ // data not changed
                save_changes_btn.setDisable(true);
            }else{ // data changed
                save_changes_btn.setDisable(false);
            }

        };

        edit_username.textProperty().addListener(data_change_listener);
        edit_email.textProperty().addListener(data_change_listener);
        edit_password.textProperty().addListener(data_change_listener);
        // It didn't work well with image property
        edit_profile_image.accessibleTextProperty().addListener(data_change_listener);
    }

    @FXML
    void cancel_changes(ActionEvent event) {
        edit_username.textProperty().removeListener(data_change_listener);
        edit_email.textProperty().removeListener(data_change_listener);
        edit_password.textProperty().removeListener(data_change_listener);
        edit_profile_image.imageProperty().removeListener(data_change_listener);
        data_change_listener=null;
        show_recent(null);
    }
    @FXML
    void save_changes(ActionEvent event) {
        User new_data=new User();
        new_data.setUsername(edit_username.getText());
        if(! edit_password.getText().isEmpty()){
            new_data.setPassword_sha1(Encryption.encryptThisString(edit_password.getText()));
        }
        if(edit_profile_image.getAccessibleText()!=null&& edit_profile_image.getAccessibleText().equals("changed")){
            String image_server_path=ServerServices.upload_file(image_to_input_stream(edit_profile_image.getImage()),new_data.getUsername()+"_login_image.png");

            new_data.setProfile_image_path(image_server_path);
        }
        boolean no_email_changed=true;
        boolean email_confirmed=false;
        if(!edit_email.getText().equals(AppProperties.currUser.getEmail())){ //email changed
            no_email_changed=false;
            //send email conformation
            EmailConfirmation.newInstance(edit_email.getText());
            EmailConfirmation.getInstance().send_email_confirmation();
            // confirm email
            email_confirmed=show_confirm_email_dialog(new_data);

        }
        if(no_email_changed || email_confirmed)
            show_save_dialog(new_data);
        else // DO Nothing
            return;

    }


    @FXML
    void edit_pick_img_file(ActionEvent event) {
        File sel_file= Dialogs.pick_file(Dialogs.ContentType.Image);
        if(sel_file!=null){
            try {
                edit_profile_image.setImage(new Image(sel_file.toURI().toURL().toExternalForm()));
                edit_profile_image.setAccessibleText("changed");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    void edt_take_shot(ActionEvent event) {
        Image sel_image=Dialogs.take_a_picture();
        if(sel_image!=null){
            edit_profile_image.setImage(sel_image);
            edit_profile_image.setAccessibleText("changed");
        }

    }



    private void show_save_dialog(User new_data) {
        Alert save_alert=new Alert(Alert.AlertType.CONFIRMATION);
        save_alert.setTitle("Update Confirmation");
        save_alert.setHeaderText("Update User Data Confirmation");
        save_alert.setContentText("Do you want to save new information ?");
        Optional<ButtonType> result= save_alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            User updated_user= ServerServices.update_user(new_data);
            if(updated_user!=null && updated_user.getId()!=0){
                AppProperties.currUser=updated_user;
                // update info
                account_username.setText(AppProperties.currUser.getUsername());
                // update profile image
                if(edit_profile_image.getAccessibleText()!=null && edit_profile_image.getAccessibleText().equals("changed")){
                    acount_avatar.setImage(edit_profile_image.getImage());
                    // save change tpo app prperties
                    if(AppProperties.getProperties().containsKey("global.email")){
                            if(AppProperties.getProperties().containsKey("global.avatar")){
                                new File(AppProperties.getProperties().getProperty("global.avatar")).delete();
                            }
                            String local_path =save_image(edit_profile_image.getImage(),updated_user.getUsername()+"_login_image.png" );
                            AppProperties.getProperties().put("global.avatar",local_path );
                    }
                }
                if(AppProperties.getProperties().containsKey("global.email")){
                    AppProperties.getProperties().put("global.email", updated_user.getEmail());
                    AppProperties.getProperties().put("global.password_sha1", updated_user.getPassword_sha1());
                }
                AppProperties.showNotification("Update user data", "User data successfully updated", Notification.SUCCESS_ICON);
               AppProperties.save_changes();
                cancel_changes(null);
            }else{
                AppProperties.showNotification("Update user data", "An Error has occurred", Notification.ERROR_ICON);
            }
        }
    }


    private boolean show_confirm_email_dialog(User new_data) {
        TextInputDialog confirm_dialog=new TextInputDialog();
        confirm_dialog.setTitle("Confirm Email");
        confirm_dialog.setHeaderText("Confirm New Email");
        confirm_dialog.setContentText("we've sended confirmation code to your new email \n Plz,Enter confirmation number");
        boolean right_res=false;
        do {
            Optional<String> res= confirm_dialog.showAndWait();
            try{
                if (!res.isPresent()) {
                    return false;
                } else if (!res.get().isEmpty()
                        && EmailConfirmation.getInstance().confirm_email(Integer.parseInt(res.get()))) { //cofirm nb right
                    EmailConfirmation.removeInstance();
                    // continue
                    new_data.setEmail(edit_email.getText());
                    right_res = true;
                } else { //cofirm nb wrong
                    AppProperties.showNotification("Confirm Email Failed", "confirm number wrong", Notification.ERROR_ICON);
                }
            } catch (NumberFormatException ex) {
            }
        }while (!right_res);
        return right_res;
    }

    private String save_image(Image sel_image,String filename) {
        try {
            File image_file=new File(filename);
            ImageIO.write(SwingFXUtils.fromFXImage(sel_image, null),"png" ,image_file );
            return image_file.toURI().toURL().toExternalForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private InputStream image_to_input_stream(Image image){
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", outputStream);
            byte[] res  = outputStream.toByteArray();
            return new ByteArrayInputStream(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
