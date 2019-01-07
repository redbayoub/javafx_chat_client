package chatapp.ui.mainView.ListCells;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class ChatMessageListCell extends JFXListCell<Message> {

    public ChatMessageListCell() {

        /*HBox hBox=new HBox();

        Label content=new Label();

        Pane pane=new Pane();
        content.setWrapText(true);
        content.setPadding(new Insets(5));
        content.setMaxWidth(270); // currn max is 292
        content.getStylesheets().add(getClass().getResource("content_label_style.css").toExternalForm());
        hBox.setHgrow(pane, Priority.ALWAYS);*/
        /*hBox.setStyle("-fx-background-color: #15181A;");
        setStyle("fx-background-color:#2e3436;");*/

    }

    @Override
    public void updateItem(Message item, boolean empty) {



        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if(item!=null&&!empty){
            if(getIndex()==0&&item.getId()!=0&&item.getId()!=-1){ // ids used for spetial resones
                // add load more btn by cretain msg with id -1
                Message sp_msg=new Message();
                sp_msg.setId(-1);

                getListView().getItems().add(0, sp_msg);
            }

            if (item.getId()==0){ // add no more data
                Label label=new Label("No More data !");
                label.setTextFill(Color.WHITE);
                StackPane lab_holder=new StackPane(label);
                lab_holder.setAlignment(Pos.CENTER);
                setGraphic(lab_holder);
                return;
            }
            if (item.getId()==-1){ // add load more btn
                JFXButton loadBtn=new JFXButton("Load More");
                loadBtn.setTextFill(Color.WHITE);
                StackPane loadBtn_holder=new StackPane(loadBtn);
                loadBtn_holder.setAlignment(Pos.CENTER);
                loadBtn.setOnAction(event -> {
                    int btn_ind=getIndex();
                    Message fMsg=getListView().getItems().get(btn_ind+1);
                    // get more items
                    Message tmp=new Message();
                    tmp.setSenderId(fMsg.getSenderId());
                    tmp.setReceiver_id(fMsg.getReceiver_id());
                    tmp.setGroupId(fMsg.getGroupId());
                    tmp.setSending_date(fMsg.getSending_date());
                    List<Message> old_msgs= ServerServices.getChatMessages(tmp, false);
                    if (old_msgs.size()==0){
                        Message sp_msg=new Message(); // add no more data by crating msg with id 0
                        getListView().getItems().add(0, sp_msg);
                        btn_ind++;
                    }else{
                        getListView().getItems().addAll(0, old_msgs);
                        btn_ind+=old_msgs.size()+1;
                    }
                    // remove btn
                    getListView().getItems().remove(btn_ind);
                });
                setGraphic(loadBtn_holder);
                return;
            }
            // else
            HBox hBox=new HBox();

            Label content=new Label();

            Pane pane=new Pane();
            content.setWrapText(true);
            content.setPadding(new Insets(5));
            content.setMaxWidth(270); // currn max is 292
            content.getStylesheets().add(getClass().getResource("content_label_style.css").toExternalForm());
            HBox.setHgrow(pane, Priority.ALWAYS);
            content.setText(item.getContent());
            if(item.getSenderId()== AppProperties.currUser.getId()){
                content.getStyleClass().add("sended");
                hBox.getChildren().addAll(pane,content);
            }else{
                content.getStyleClass().add("recived");
                //check if the perv mesage from the same sender
                if((getIndex()-1>=0)&&
                        getListView().getItems().get(getIndex()-1).getSenderId()!=getItem().getSenderId()){
                    Image profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
                    ImageView profile_image_view=new ImageView(profile_image);
                    profile_image_view.setFitHeight(25);
                    profile_image_view.setFitWidth(25);
                    profile_image_view.setPreserveRatio(true);
                    AnchorPane.setTopAnchor(profile_image_view, 0.0);
                    AnchorPane.setLeftAnchor(profile_image_view,0.0 );
                    AnchorPane.setTopAnchor(content, 28.0);
                    AnchorPane.setLeftAnchor(content, 28.0);
                    hBox.getChildren().addAll(new AnchorPane(profile_image_view,content),pane);
                }else{
                    Pane margin=new Pane();
                    margin.setMaxWidth(28);
                    margin.setPrefWidth(28);
                    margin.setMinWidth(28);
                    hBox.getChildren().addAll(margin,content,pane);
                }

            }

            setGraphic(hBox);
        }
    }


}
