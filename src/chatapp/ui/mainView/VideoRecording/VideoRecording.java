package chatapp.ui.mainView.VideoRecording;

import chatapp.classes.ContentType;
import chatapp.ui.dialogs.Dialogs;
import chatapp.ui.mainView.DetailedAction;
import chatapp.ui.mainView.MediaPlayer.MediaPlayerUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class VideoRecording extends DetailedAction {
    public VideoRecording(Pane container_pane) {
        super(container_pane, "mp4");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VideoRecording.fxml"));
        loader.setController(this);
        try {
            initRootPane(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





     @FXML
    private StackPane play_recorded_pane;
    private MediaPlayerUI playerUI;

    @FXML
    void pick_a_video_from_files(ActionEvent event) {
        File sel_file=Dialogs.pick_file(ContentType.Video);
        if(sel_file!=null){
            try {
                Files.copy(new FileInputStream(sel_file), getResult_file().toPath(),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(playerUI!=null){
                playerUI.stop(null);
                playerUI=null;
            }
            try {
                playerUI=new MediaPlayerUI(play_recorded_pane, getResult_file().toURI().toURL(),ContentType.Video,false );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    void take_a_video(ActionEvent event) {
        File sel_file=Dialogs.capture_a_video();
        if(sel_file!=null){
            try {
                Files.copy(new FileInputStream(sel_file), getResult_file().toPath(),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(playerUI!=null){
                playerUI.stop(null);
                playerUI=null;
            }
            try {
                playerUI=new MediaPlayerUI(play_recorded_pane, getResult_file().toURI().toURL(),ContentType.Video,false );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void cleanup() {
        getResult_file().delete();
    }

}
