package chatapp.ui.dialogs;

import chatapp.classes.AppProperties;
import chatapp.classes.ContentType;
import chatapp.ui.dialogs.capture_a_video.CaptureAVideo;
import chatapp.ui.dialogs.take_a_picture.TakeAPicture;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Dialogs {

    public static File pick_file(ContentType type){
        FileChooser chooser=new FileChooser();
        String descr;
        if(type.equals(ContentType.File)){
            descr="All Files";
        }else{
            descr=type+" Files";
        }
        chooser.setTitle("Pick "+type.name()+" File");
        chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter(descr,type.exts));
        return chooser.showOpenDialog(null);

    }


    public static Image take_a_picture(){
        Stage take_a_pic_stage=AppProperties.get_screen("Take a Picture", "/chatapp/ui/dialogs/take_a_picture/take_a_picture.fxml");
        take_a_pic_stage.showAndWait();
        return TakeAPicture.captured_image;
    }

    public static File capture_a_video(){
        Stage take_a_pic_stage=AppProperties.get_screen("Capture a video", "/chatapp/ui/dialogs/capture_a_video/capture_a_video.fxml");
        take_a_pic_stage.showAndWait();
        return CaptureAVideo.captured_video;
    }


}
