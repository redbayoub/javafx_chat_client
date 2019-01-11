package chatapp.ui.mainView.MusicPlayer;

import com.jfoenix.controls.JFXButton;
import com.sun.istack.internal.NotNull;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MusicPlayer {
    private AnchorPane root_pane;
    private File audio_file;
    public MusicPlayer(@NotNull Pane container_pane, File audio_file) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MusicPlayer.fxml"));
        loader.setController(this);
        root_pane=loader.load();
        container_pane.getChildren().add(root_pane);
        this.audio_file=audio_file;
        prepare_mediaPlayer.run();
        //init_player();
    }
    Task<Void> prepare_mediaPlayer=new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            mediaPlayer=new MediaPlayer(new Media(audio_file.toURI().toString()));
            mediaPlayer.setOnReady(() -> {
                playBtn.setDisable(false);
                playBtn.setUserData("Play");
                stopBtn.setDisable(false);
                max_duration.setText(durationToString(mediaPlayer.getMedia().getDuration()));
                progress_slider.setMin(1);
                progress_slider.setValue(1);
                progress_slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
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
            return null;
        }
    };
    private void init_player() {

        /*mediaPlayer.setOnReady(() -> {
            playBtn.setDisable(false);
            playBtn.setUserData("Play");
            stopBtn.setDisable(false);
            max_duration.setText(durationToString(mediaPlayer.getMedia().getDuration()));
            progress_slider.setMin(1);
            progress_slider.setValue(1);
            progress_slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    curr_progres.setText(durationToString(mediaPlayer.getCurrentTime()));
                    progress_slider.setValue(newValue.toSeconds());
            });
            progress_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                int diff=newValue.intValue() - oldValue.intValue();
            *//* this is just a trick to know if the user will have to change
                more than "1 second"Or the player changed duiration  *//*
                if((mediaPlayer!=null)&&((diff>1)||(diff<0))) {
                    double newDur = newValue.intValue() * 1000;
                    mediaPlayer.seek(new Duration(newDur));
                }
            });
        });*/
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

    @FXML
    void playOrPause(ActionEvent event) {
        if(playBtn.getUserData().equals("Play")){
            mediaPlayer.play();
            playBtn.setText("Pause");
            playBtn.setUserData("Pause");
        }else {
            mediaPlayer.pause();
            playBtn.setText("Play");
            playBtn.setUserData("Play");
        }

    }

    @FXML
    void stop(ActionEvent event) {
        mediaPlayer.stop();
        playBtn.setText("Play");
        playBtn.setUserData("Play");
    }

    private String durationToString(Duration durationToConvert) {
        long milis=(long)durationToConvert.toMillis();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milis),
                TimeUnit.MILLISECONDS.toMinutes(milis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milis)%TimeUnit.MINUTES.toSeconds(1));
    }

}
