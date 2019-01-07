package chatapp.ui.mainView.ListCells;

import chatapp.classes.model.User;
import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class FriendListCell extends JFXListCell<User> {

    private HBox hBox=new HBox();
    private Image profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
    private ImageView profile_image_view=new ImageView(profile_image);
    private Label profile_username=new Label();
    private FontAwesomeIconView iconView=new FontAwesomeIconView(FontAwesomeIcon.CIRCLE);

    private Pane pane=new Pane();
    public FriendListCell() {
        profile_username.setTextFill(Color.WHITE);

        profile_image_view.setFitHeight(50);
        profile_image_view.setFitWidth(50);
        profile_image_view.setPreserveRatio(true);
        StackPane iconHolder=new StackPane(iconView);
        iconHolder.setAlignment(Pos.CENTER);

        hBox.getChildren().addAll(profile_image_view,profile_username ,pane,iconHolder);
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
            if(item.isIs_connected()){
                iconView.setFill(Color.LIME);
            }else{
                iconView.setFill(Color.GAINSBORO);
            }
            // TODO set profile image
            setGraphic(hBox);
        }
    }


}
