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
    private JFXButton save_btn;
    @FXML
    private JFXButton redo_btn;

    private VideoRecorder videoRecorder;
    private Webcam webcam;
    public static File captured_video;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webcam=Webcam.getDefault();
        Dimension dimension=webcam.getViewSizes()[webcam.getViewSizes().length-1];
        webcam.setViewSize(dimension);
        feed_view.setFitHeight(dimension.height);
        feed_view.setFitWidth(dimension.width);
        feed_view.setSmooth(true);
        captured_video=new File(AppProperties.getTmp_dir(),String.format("%d.mp4",System.currentTimeMillis()));
        videoRecorder=new VideoRecorder(captured_video, feed_view,webcam,progress_bar );

    }


    @FXML
    void capture(ActionEvent event) {
        videoRecorder.capture();
        redo_btn.setDisable(false);
        save_btn.setDisable(false);
        cpt_brn.setDisable(true);
    }

    @FXML
    void redo(ActionEvent event) {
        //videoRecorder.redo();
        videoRecorder.clean_up();
        videoRecorder=new VideoRecorder(captured_video, feed_view,webcam,progress_bar );
        cpt_brn.setDisable(false);
        save_btn.setDisable(true);
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
