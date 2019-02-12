package chatapp.ui.mainView.ListCells;

import chatapp.classes.AppProperties;
import chatapp.classes.CacheController;
import chatapp.classes.ContentType;
import chatapp.classes.ServerServices;
import chatapp.classes.model.Message;
import chatapp.ui.mainView.MusicPlayer.MediaPlayerUI;
import com.jfoenix.controls.JFXListCell;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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
            /*if(getIndex()==0&&item.getId()!=0&&item.getId()!=-1){ // ids used for spetial resones
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
            }*/
            // else
            HBox hBox=new HBox();
            Pane pane=new Pane();

            if (item.isFile()){
                setGraphic(new ProgressIndicator());
                Thread thread=new Thread(() -> {
                    File down_file=CacheController.get_file(item.getContent());
                    Platform.runLater(() -> {
                        Pane content_pane=new Pane();
                        content_pane.setMaxWidth(250.0);
                        switch (ContentType.get_type(down_file)){
                            case Audio:{

                                try {
                                    MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, down_file,true );
                                    item.setUserData(playerUI);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case Video:{
                                try {
                                    MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, down_file,true );
                                    item.setUserData(playerUI);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case Image:{
                                try {
                                    ImageView imageView=new ImageView(down_file.toURI().toURL().toString());
                                    imageView.setPreserveRatio(true);
                                    imageView.setFitWidth(250);
                                    //content_pane.setPrefHeight(200);
                                    imageView.setFitWidth(200);
                                    content_pane.getChildren().add(imageView);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case File:{

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
                /*Task<File> get_file=new Task<File>() {
                    @Override
                    protected File call() throws Exception {
                        System.out.println("downloding "+item.getId());
                        return  CacheController.get_file(item.getContent());
                    }
                };

                get_file.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        Pane content_pane=new Pane();
                        content_pane.setMaxWidth(250.0);
                        switch (ContentType.get_type(get_file.getValue())){
                            case Audio:{

                                try {
                                    MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, get_file.getValue(),true );
                                    item.setUserData(playerUI);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case Video:{
                                try {
                                    MediaPlayerUI playerUI=new MediaPlayerUI(content_pane, get_file.getValue(),true );
                                    item.setUserData(playerUI);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case Image:{
                                try {
                                    ImageView imageView=new ImageView(get_file.getValue().toURI().toURL().toString());
                                    imageView.setPreserveRatio(true);
                                    imageView.setFitWidth(250);
                                    //content_pane.setPrefHeight(200);
                                    imageView.setFitWidth(200);
                                    content_pane.getChildren().add(imageView);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case File:{

                                break;
                            }
                        }
                        if(item.getSenderId()== AppProperties.currUser.getId()){
                            hBox.getChildren().setAll(pane,content_pane);
                        }else{
                            hBox.getChildren().setAll(content_pane,pane);
                        }

                    }
                });
                Thread getting_file_th=new Thread(get_file);
                getting_file_th.start();*/
                thread.start();

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
                        if(profile_image==null)profile_image=new Image("/chatapp/images/defualt_user_avatar.png");
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
