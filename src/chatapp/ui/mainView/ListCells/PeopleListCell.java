package chatapp.ui.mainView.ListCells;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerInfo;
import chatapp.classes.ServerServices;
import chatapp.classes.model.User;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import eu.hansolo.enzo.notification.Notification;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class PeopleListCell extends JFXListCell<User> {

    private HBox hBox=new HBox();
    private Image profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
    private ImageView profile_image_view=new ImageView(profile_image);
    private Label profile_username=new Label();
    private JFXButton jfxButton=new JFXButton("Send Friend Request");
    private Pane pane=new Pane();
   public PeopleListCell() {
       FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.SEND);
       //iconView.setFill(Color.WHITE);
       jfxButton.setGraphic(iconView);
       jfxButton.setContentDisplay(ContentDisplay.TOP);
       AnchorPane anchorPane = new AnchorPane(jfxButton);
       AnchorPane.setBottomAnchor(jfxButton, 10.0);
       hBox.getChildren().addAll(profile_image_view, profile_username, pane, anchorPane);
       HBox.setHgrow(pane, Priority.ALWAYS);
       /*hBox.setStyle("-fx-background-color: #15181A;");
       setStyle("fx-background-color:#2e3436;");*/

       jfxButton.setOnAction((e) -> {
           boolean res=ServerServices.send_friend_request(getItem().getId());
           if(res)
               AppProperties.showNotification("Sucsess", "Friend Request has been sent", Notification.SUCCESS_ICON);
           else
               if(ServerInfo.isOn()){
                   AppProperties.showNotification("Error", "Friend Request has been sent before", Notification.ERROR_ICON);
               }else
                   AppProperties.showNotification("Sucsess", "Friend Request has been sent", Notification.SUCCESS_ICON);
       });
   }


    @Override
    public void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if(item!=null&&!empty){
            profile_username.setText(item.getUsername());
            // TODO set profile image
            setGraphic(hBox);
        }
    }
}
