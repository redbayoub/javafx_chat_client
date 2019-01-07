package chatapp.ui.dialogs.take_a_picture;

import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TakeAPicture implements Initializable {

    private static class FeedThread extends Thread{
        public FeedThread(Webcam webcam, ImageView feed_view,JFXProgressBar progress_bar) {
            this.webcam = webcam;
            this.feed_view=feed_view;
            this.progress_bar=progress_bar;
        }
        private ImageView feed_view;
        private JFXProgressBar progress_bar;
        private Webcam webcam;

        private boolean running=false;
        private boolean paused=false;
        @Override
        public void run() {
            running=true;
            webcam.open();
            progress_bar.setVisible(false);
            while(running){
                if(!paused){
                    feed_view.setImage(SwingFXUtils.toFXImage(webcam.getImage(), null));
                }
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void pauseThread(){
            paused=true;
        }

        private void restartThread(){
            paused=false;
        }

        private void stopThread(){
            running=false;
            webcam.close();
        }
    }

    @FXML
    private JFXProgressBar progress_bar;

    @FXML
    private ImageView feed_view;

    @FXML
    private JFXButton cpt_brn;

    @FXML
    private JFXButton redo_btn;

    private Webcam webcam;
    private FeedThread feedThread;
    public static Image captured_image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webcam=Webcam.getDefault();
        Dimension dimension=webcam.getViewSizes()[webcam.getViewSizes().length-1];
        webcam.setViewSize(dimension);
        feed_view.setFitHeight(dimension.height);
        feed_view.setFitWidth(dimension.width);
        feed_view.setSmooth(true);
        feedThread=new FeedThread(webcam,feed_view,progress_bar);
        feedThread.start();
        redo_btn.setDisable(true);
    }

    @FXML
    void capture(ActionEvent event) {
        feedThread.pauseThread();
        cpt_brn.setDisable(true);
        redo_btn.setDisable(false);
    }

    @FXML
    void redo(ActionEvent event) {
        feedThread.restartThread();
        cpt_brn.setDisable(false);
        redo_btn.setDisable(true);
    }

    @FXML
    void save(ActionEvent event) {
        captured_image=feed_view.getImage();
        close(null);
    }

    @FXML
    void close(MouseEvent event) {
        feedThread.stopThread();
        feed_view.getScene().getWindow().hide();
    }

}
