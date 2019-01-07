package chatapp.ui.mainView.ListCells;

import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import com.jfoenix.controls.JFXListCell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.DateFormat;

 /*
    HOW TO USE
    entriesListView.setCellFactory(new Callback<ListView<JournalPost>, ListCell<JournalPost>>(){
        @Override
        public ListCell<JournalPost> call(ListView<JournalPost> param) {
            return new PostCell();
        }
    });
    */

public class RecentListCell extends JFXListCell<Message> {
    private Image profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
    private ImageView profile_image_view=new ImageView(profile_image);

    private Label sender_username=new Label();
    private Label msg_sending_time=new Label();
    private Label message_perview_content=new Label();

    private AnchorPane pane=new AnchorPane();

    public RecentListCell(){
        message_perview_content.setWrapText(true);
        message_perview_content.setMaxHeight(40);
        message_perview_content.setTextFill(Color.WHITE);

        profile_image_view.setFitHeight(30);
        profile_image_view.setFitWidth(30);
        profile_image_view.setPreserveRatio(true);

        sender_username.setTextFill(Color.WHITE);
        sender_username.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));

        msg_sending_time.setTextFill(Color.GAINSBORO);


        pane.getChildren().setAll(profile_image_view,sender_username,msg_sending_time,message_perview_content);

        AnchorPane.setTopAnchor(profile_image_view, 5.0);
        AnchorPane.setLeftAnchor(profile_image_view, 5.0);

        AnchorPane.setTopAnchor(sender_username, 5.0);
        AnchorPane.setLeftAnchor(sender_username, 5.0+profile_image_view.getFitWidth()+5);

        AnchorPane.setTopAnchor(msg_sending_time, 20.0);
        AnchorPane.setLeftAnchor(msg_sending_time, 5.0+profile_image_view.getFitWidth()+5);

        AnchorPane.setTopAnchor(message_perview_content, 5.0+profile_image_view.getFitHeight()+5);
        AnchorPane.setLeftAnchor(message_perview_content, 3.0);
        AnchorPane.setRightAnchor(message_perview_content, 3.0);

    }

    @Override
    public void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if(item!=null&&!empty){
            sender_username.setText(ServerServices.get_user_by_id(item.getSenderId()).getUsername());
            msg_sending_time.setText(DateFormat.getDateTimeInstance().format(item.getSending_date()));
            message_perview_content.setText(item.getContent());
            // TODO set profile image
            setGraphic(pane);
        }
    }

}
