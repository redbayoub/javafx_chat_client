package chatapp.ui.mainView.ListCells;

import chatapp.classes.ServerServices;
import chatapp.classes.model.FriendRequest;
import chatapp.classes.model.User;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class FriendRequestCell extends JFXListCell<FriendRequest> {



    private HBox hBox=new HBox();
    private Image profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
    private ImageView profile_image_view=new ImageView(profile_image);
    private Label profile_username=new Label();
    private JFXButton acceptButton=new JFXButton("Accept");
    private JFXButton declineButton=new JFXButton("Decline");
    private Pane pane=new Pane();
    public FriendRequestCell() {
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        iconView.setFill(Color.LIME);
        acceptButton.setGraphic(iconView);
        acceptButton.setContentDisplay(ContentDisplay.TOP);
        acceptButton.setOnAction((e) -> { // accept friend request
            ServerServices.accept_friend_request(getItem());
            getListView().getItems().remove(getIndex());
        });

        FontAwesomeIconView iconView2 = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
        iconView2.setFill(Color.RED);
        declineButton.setGraphic(iconView2);
        declineButton.setContentDisplay(ContentDisplay.TOP);
        declineButton.setOnAction((e) -> { // decline friend request friend request
            ServerServices.decline_friend_request(getItem());
            getListView().getItems().remove(getIndex());
        });

        HBox btnHolder=new HBox(acceptButton,declineButton);

        AnchorPane anchorPane = new AnchorPane(profile_username,btnHolder);
        // setting for itemes in anchor pane
        AnchorPane.setBottomAnchor(btnHolder, 10.0);
        AnchorPane.setLeftAnchor(btnHolder, 5.0);
        AnchorPane.setTopAnchor(profile_username, 10.0);
        AnchorPane.setLeftAnchor(profile_username, 5.0);

        hBox.getChildren().addAll(profile_image_view, pane, anchorPane);
        HBox.setHgrow(pane, Priority.ALWAYS);
        /*hBox.setStyle("-fx-background-color: #15181A;");
        setStyle("fx-background-color:#2e3436;");*/

    }


    @Override
    public void updateItem(FriendRequest item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if(item!=null&&!empty){
            User friend=ServerServices.get_user_by_id(item.getSenderId());
            profile_username.setText(friend.getUsername());
            // TODO set profile image
            setGraphic(hBox);
        }
    }
}
