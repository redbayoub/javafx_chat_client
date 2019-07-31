package chatapp.ui.mainView.ListCells;

import chatapp.classes.*;
import chatapp.classes.model.Message;
import chatapp.ui.mainView.MediaPlayer.MediaPlayerUI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
            if(Messages.is_load_more_data_msg(item)){
                JFXButton loadBtn=new JFXButton("Load More");
                loadBtn.setTextFill(Color.WHITE);
                loadBtn.setStyle("-fx-background-color:#3465A4;" +
                        "-fx-background-radius:10;" +
                        "-fx-border-color: white;" +
                        " -fx-border-radius: 10;" +
                        " -fx-border-width: 2;");
                StackPane loadBtn_holder=new StackPane(loadBtn);
                loadBtn_holder.setAlignment(Pos.CENTER);
                loadBtn.setOnAction(event -> {
                    //int btn_ind=getIndex();
                    Message fMsg=getListView().getItems().get(1);
                    // get more items
                    Message tmp=new Message();
                    tmp.setSenderId(fMsg.getSenderId());
                    tmp.setReceiver_id(fMsg.getReceiver_id());
                    tmp.setGroupId(fMsg.getGroupId());
                    tmp.setSending_date(fMsg.getSending_date());
                    List<Message> old_msgs= ServerServices.getChatMessages(tmp, false);
                    if (old_msgs.size()==0){
                        // remove btn
                        getListView().getItems().remove(0);
                        getListView().getItems().add(0, Messages.get_no_more_data_msg());
                        //btn_ind++;

                    }else{
                        getListView().getItems().addAll(1, old_msgs);
                        //btn_ind+=old_msgs.size()+1;
                    }

                });
                setGraphic(loadBtn_holder);
                return;
            }
            if(Messages.is_no_more_data_msg(item)){
                Label label=new Label("No More data !");
                label.setTextFill(Color.WHITE);
                StackPane lab_holder=new StackPane(label);
                lab_holder.setAlignment(Pos.CENTER);
                setGraphic(lab_holder);
                return;
            }

            // else
            HBox hBox=new HBox();
            Pane pane=new Pane();

            if (item.isFile()){
                if(Messages.mediaPlayers.containsKey(item.getId())){
                    MediaPlayerUI playerUI=Messages.mediaPlayers.get(item.getId());
                    Pane content_pane=new Pane();
                    content_pane.setMaxWidth(250.0);
                    playerUI.change_container_pane(content_pane);
                    if(item.getSenderId()== AppProperties.currUser.getId()){
                        hBox.getChildren().setAll(pane,content_pane);
                    }else{
                        hBox.getChildren().setAll(content_pane,pane);
                    }
                    HBox.setHgrow(pane, Priority.ALWAYS);
                    setGraphic(hBox);
                }else {
                    setGraphic(new ProgressIndicator());
                    Thread thread=new Thread(() -> {
                        // TODO use url instead of files
                        // ERORR cells are updating
                        //File down_file=CacheController.get_file(item.getContent());
                        URL url=null;
                        String filename=null;
                        try {
                            url=new URL(AppProperties.getProperties().getProperty("server.uri")+"/upload/files/"+item.getContent());
                            URLConnection connection=url.openConnection();
                            filename=connection.getHeaderField(HttpHeaders.CONTENT_DISPOSITION);


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ContentType file_type=ContentType.get_type(filename);

                        URL finalUrl = url;
                        Platform.runLater(() -> {
                            Pane content_pane=new Pane();
                            content_pane.setMaxWidth(250.0);
                            //switch (ContentType.get_type(down_file)){
                            switch (file_type){
                                case Audio:{

                                    try {
                                        MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, finalUrl,file_type,true );
                                        //item.setUserData(playerUI);
                                        Messages.mediaPlayers.put(item.getId(),playerUI );
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                case Video:{
                                    try {
                                        MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, finalUrl,file_type,true );
                                        //item.setUserData(playerUI);
                                        Messages.mediaPlayers.put(item.getId(),playerUI );
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                case Image:{
                                    ImageView imageView=new ImageView(finalUrl.toString());
                                    imageView.setPreserveRatio(true);
                                    imageView.setFitWidth(250);
                                    //content_pane.setPrefHeight(200);
                                    imageView.setFitWidth(200);
                                    content_pane.getChildren().add(imageView);

                                    break;
                                }
                                case File:{
                                    // TODO
                                    break;
                                }
                            }
                            if(item.getSenderId()== AppProperties.currUser.getId()){
                                hBox.getChildren().setAll(pane,content_pane);
                            }else{
                                hBox.getChildren().setAll(content_pane,pane);
                            }
                            HBox.setHgrow(pane, Priority.ALWAYS);
                            setGraphic(hBox);
                        });
                    });
                    thread.start();

                }

            }else{
                Label content=new Label();
                content.setWrapText(true);
                content.setPadding(new Insets(5));
                content.setMaxWidth(270); // currn max is 292
                content.getStylesheets().add(getClass().getResource("content_label_style.css").toExternalForm());

                content.setText(item.getContent());
                if(item.getSenderId()== AppProperties.currUser.getId()){
                    content.getStyleClass().add("sended");
                    hBox.getChildren().addAll(pane,content);
                }else{
                    content.getStyleClass().add("recived");
                    //check if the perv mesage from the same sender
                    if((getIndex()-1>=0)&&
                            getListView().getItems().get(getIndex()-1).getSenderId()!=getItem().getSenderId()){
                        Image profile_image=CacheController.get_avatar(ServerServices.get_user_by_id(item.getSenderId()).getProfile_image_path());
                        if(profile_image==null)profile_image=new Image("/images/defualt_user_avatar.png");
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
                HBox.setHgrow(pane, Priority.ALWAYS);
                setGraphic(hBox);
            }
            /*HBox.setHgrow(pane, Priority.ALWAYS);
            setGraphic(hBox);*/
        }
    }


}
