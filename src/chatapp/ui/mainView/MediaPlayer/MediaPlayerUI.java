package chatapp.ui.mainView.MediaPlayer;

import chatapp.classes.ContentType;
import com.jfoenix.controls.JFXButton;
import com.sun.istack.internal.NotNull;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MediaPlayerUI {
    private VBox root_pane;
    private File played_file;
    private MediaView  mediaView;
    private boolean mini_player=false;

    public MediaPlayerUI(@NotNull Pane container_pane, File played_file,boolean mini_ui) throws IOException {
        FXMLLoader loader=null;
        if(mini_ui){
            loader = new FXMLLoader(getClass().getResource("MiniMediaPlayerUI.fxml"));
        }else{
            loader = new FXMLLoader(getClass().getResource("MediaPlayerUI.fxml"));
        }
        this.mini_player=mini_ui;
        loader.setController(this);
        root_pane=loader.load();
        container_pane.getChildren().add(root_pane);
        this.played_file=played_file;

        init_player(mini_ui);
    }
    Task<Void> prepare_mediaPlayer=new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            mediaPlayer=new MediaPlayer(new Media(played_file.toURI().toString()));
            if(mediaView!=null)mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnReady(() -> {
                if(!mini_player){
                    playBtn.setDisable(false);
                    playBtn.setUserData("Play");
                    stopBtn.setDisable(false);
                    max_duration.setText(durationToString(mediaPlayer.getMedia().getDuration()));
                }

                progress_slider.setMin(1);
                progress_slider.setValue(1);
                progress_slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if(!mini_player)
                        curr_progres.setText(durationToString(mediaPlayer.getCurrentTime()));
                    progress_slider.setValue(newValue.toSeconds());
                });
                progress_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int diff=newValue.intValue() - oldValue.intValue();
            /* this is just a trick to know if the user will have to change
                more than "1 second"Or the player changed duiration  */
                    if((mediaPlayer!=null)&&((diff>1)||(diff<0))) {
                        double newDur = newValue.intValue() * 1000;
                        mediaPlayer.seek(new Duration(newDur));
                    }
                });
            });
            mediaPlayer.setOnEndOfMedia(()->{
                    stop(null);
                });
            return null;
        }
    };
    private void init_player(boolean mini_ui) {
        if(mini_ui){
            switch (ContentType.get_type(played_file)){
                case Audio:{
                    break;
                }
                case Video:{
                    mediaView=new MediaView(mediaPlayer);
                    mediaView.setFitWidth(200);
                    mediaView.setPreserveRatio(true);
                    root_pane.getChildren().add(0,mediaView);
                    break;
                }
                default:{
                    throw new UnsupportedOperationException("Unsupported file type");
                }
            }
        }else{
            switch (ContentType.get_type(played_file)){
                case Audio:{
                    // don't do anything
                    prepare_mediaPlayer.run();
                    break;
                }
                case Video:{
                    mediaView=new MediaView();
                    mediaView.setFitWidth(298);
                    mediaView.setPreserveRatio(true);
                    root_pane.getChildren().add(0,mediaView);
                    prepare_mediaPlayer.run();
                    break;
                }
                default:{
                    throw new UnsupportedOperationException("Unsupported file type");
                }
            }
        }
    }

    @FXML
    private Text curr_progres;

    @FXML
    private Slider progress_slider;

    @FXML
    private Text max_duration;

    @FXML
    private JFXButton playBtn;

    @FXML
    private JFXButton stopBtn;

    private MediaPlayer mediaPlayer;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @FXML
    public void playOrPause(ActionEvent event) {
        if(mediaPlayer==null || mediaPlayer.getStatus()!= MediaPlayer.Status.PLAYING){
            if(mediaPlayer==null){
                prepare_mediaPlayer.run();
                prepare_mediaPlayer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        if(mediaView!=null && mediaView.getMediaPlayer()==null)
                            mediaView.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                        if(!mini_player){
                            playBtn.setText("Pause");
                            playBtn.setUserData("Pause");
                        }

                    }
                });
            }else{
                mediaPlayer.play();
                if(!mini_player){
                    playBtn.setText("Pause");
                    playBtn.setUserData("Pause");
                }

            }


        }else {
            mediaPlayer.pause();
            if(!mini_player){
                playBtn.setText("Play");
                playBtn.setUserData("Play");
            }

        }

    }

    @FXML
    public void stop(ActionEvent event) {
        mediaPlayer.stop();
        if (!mini_player){
            playBtn.setText("Play");
            curr_progres.setText("00:00");
            playBtn.setUserData("Play");
        }

    }

    private String durationToString(Duration durationToConvert) {
        long milis=(long)durationToConvert.toMillis();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milis),
                TimeUnit.MILLISECONDS.toMinutes(milis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milis)%TimeUnit.MINUTES.toSeconds(1));
    }

}
