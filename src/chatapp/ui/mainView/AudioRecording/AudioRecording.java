package chatapp.ui.mainView.AudioRecording;

import chatapp.classes.ContentType;
import chatapp.ui.mainView.DetailedAction;
import chatapp.ui.mainView.MediaPlayer.MediaPlayerUI;
import com.jfoenix.controls.JFXButton;
import com.sun.istack.internal.NotNull;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioRecording extends DetailedAction {

    /*private VBox root_pane;*/
    public AudioRecording(@NotNull Pane container_pane) {
        super(container_pane,"wav");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AudioRecording.fxml"));
        loader.setController(this);
        try {
            initRootPane(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        init_recorder();
    }


    private final byte RECORD =1;
    private final byte STOP=2;
    private final byte RESET=3;

    private void init_recorder() {
        play_recorded_pane.setVisible(false);
        recording_status_pane.setVisible(false);
        recoredbtn.setUserData(RECORD);
        recoredbtn.setText("RECORD");
        recoredBtnIcon.setGlyphName("CIRCLE");

        recorder=new JavaSoundRecorder(getResult_file());
        recorder.getElpased_time().addListener((observable, oldValue, newValue) -> {
            if(newValue.longValue()>oldValue.longValue()){
                recordedDuration.setText(durationToString(newValue.longValue()));
            }
        });
    }


    @FXML
    private HBox recording_status_pane;

    @FXML
    private Text recordedDuration;

    @FXML
    private StackPane play_recorded_pane;

    @FXML
    private JFXButton recoredbtn;

    @FXML
    private FontAwesomeIconView recoredBtnIcon;

    private JavaSoundRecorder recorder;

    @FXML
    void startOrStopOrReset(ActionEvent event) {
        switch ((byte)recoredbtn.getUserData()){
            case RECORD:{
                recorder.start();

                switch_to_next_state();
                break;
            }
            case STOP:{
                try {
                    recorder.finish();

                    init_play_recorded_pane();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch_to_next_state();
                break;
            }
            case RESET:{
                recorder=null;
                init_recorder();
                //switch_to_next_state();
                break;
            }
        }
    }

    private void init_play_recorded_pane() {
        try {
            play_recorded_pane.setVisible(true);
            MediaPlayerUI player=new MediaPlayerUI(play_recorded_pane, getResult_file().toURI().toURL(), ContentType.Audio,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switch_to_next_state() {
        switch ((byte)recoredbtn.getUserData()){
            case RECORD:{
                recording_status_pane.setVisible(true);
                recoredbtn.setUserData(STOP);
                recoredbtn.setText("STOP");
                recoredBtnIcon.setGlyphName("SQUARE");
                break;
            }
            case STOP:{
                recording_status_pane.setVisible(false);
                recoredbtn.setUserData(RESET);
                recoredbtn.setText("RESET");
                recoredBtnIcon.setGlyphName("ROTATE_LEFT");
                break;
            }
            case RESET:{
                recording_status_pane.setVisible(false);
                recoredbtn.setUserData(RECORD);
                recoredbtn.setText("RECORD");
                recoredBtnIcon.setGlyphName("CIRCLE");
                break;
            }
        }
    }

    @Override
    public void cleanup() {
        getResult_file().delete();

    }


    private String durationToString(long milis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milis),
                TimeUnit.MILLISECONDS.toMinutes(milis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milis)%TimeUnit.MINUTES.toSeconds(1));
    }

}
