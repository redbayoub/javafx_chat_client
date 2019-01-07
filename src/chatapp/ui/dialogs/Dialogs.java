package chatapp.ui.dialogs;

import chatapp.classes.AppProperties;
import chatapp.ui.dialogs.take_a_picture.TakeAPicture;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Dialogs {
    public enum ContentType {
        Image(Arrays.asList(new String[]{"*.png","*.jpg","*.jpeg","*.PNG","*.JPG","*.JPEG"})),
        Video(Arrays.asList(new String[]{"*.mp4","*.3gp","*.flv","*.avi","*.MP4","*.3GP","*.FLV","*.AVI"})),
        Audio(Arrays.asList(new String[]{"*.mp3","*.wav","*.MP3","*.WAV"})),
        File(Arrays.asList(new String[]{"*"}));
        List<String> exts;

        ContentType(List<String> exts) {
            this.exts=exts;
        }
    }
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


    private static Image caputred_image=null;
    public static Image take_a_picture(){
        Stage take_a_pic_stage=AppProperties.get_screen("Take a Picture", "/chatapp/ui/dialogs/take_a_picture/take_a_picture.fxml");
        take_a_pic_stage.setOnHidden(event -> {
            caputred_image=TakeAPicture.captured_image;
        });
        take_a_pic_stage.showAndWait();
        return caputred_image;
    }


}
