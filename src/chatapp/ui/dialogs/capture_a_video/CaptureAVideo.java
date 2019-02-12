package chatapp.ui.dialogs.capture_a_video;

import chatapp.classes.AppProperties;
import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CaptureAVideo implements Initializable {


    @FXML
    private JFXProgressBar progress_bar;
    @FXML
    private ImageView feed_view;

    @FXML
    private JFXButton cpt_brn;

    @FXML
    private JFXButton redo_btn;

    private VideoRecorder videoRecorder;
    public static File captured_video;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Webcam webcam=Webcam.getDefault();
        Dimension dimension=webcam.getViewSizes()[webcam.getViewSizes().length-1];
        webcam.setViewSize(dimension);
        feed_view.setFitHeight(dimension.height);
        feed_view.setFitWidth(dimension.width);
        feed_view.setSmooth(true);
        captured_video=new File(AppProperties.getTmp_dir(),String.format("%d.mp4",System.currentTimeMillis()));
        videoRecorder=new VideoRecorder(captured_video, feed_view,webcam,progress_bar );
        redo_btn.setDisable(true);
    }

    private final int CAPTURING=1;
    private final int PAUSED=2;

    @FXML
    void capture(ActionEvent event) {
        if(cpt_brn.getUserData()==null || cpt_brn.getUserData().equals(CAPTURING)){
            videoRecorder.capture();
            cpt_brn.setUserData(PAUSED);
            cpt_brn.setText("Pause");
        }else {
            videoRecorder.pause();
            cpt_brn.setUserData(CAPTURING);
            cpt_brn.setText("Continue");
        }
        redo_btn.setDisable(false);
    }

    @FXML
    void redo(ActionEvent event) {
        videoRecorder.redo();
        cpt_brn.setUserData(CAPTURING);
        cpt_brn.setText("Capture");
        redo_btn.setDisable(true);
    }

    @FXML
    void save(ActionEvent event) {
        videoRecorder.save();
        feed_view.getScene().getWindow().hide();
    }

    @FXML
    void close(MouseEvent event) {
        videoRecorder.clean_up();
        feed_view.getScene().getWindow().hide();
    }

}
