package chatapp.ui.create_group;

import chatapp.classes.model.User;
import com.jfoenix.controls.JFXListCell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class MemberListCell extends JFXListCell<User> {

    private HBox hBox=new HBox();
    private Image profile_image=new Image("/images/defualt_user_avatar.png");
    private ImageView profile_image_view=new ImageView(profile_image);
    private Label profile_username=new Label();
    private Pane pane=new Pane();
   public MemberListCell() {
       profile_username.setTextFill(Color.WHITE);
       profile_image_view.setFitWidth(50);
       profile_image_view.setFitHeight(50);
       profile_image_view.setPreserveRatio(true);
       hBox.getChildren().addAll(profile_image_view, profile_username, pane);
       HBox.setHgrow(pane, Priority.ALWAYS);
       /*hBox.setStyle("-fx-background-color: #15181A;");
       setStyle("fx-background-color:#2e3436;");*/
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
