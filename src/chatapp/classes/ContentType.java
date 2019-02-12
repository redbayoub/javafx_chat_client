package chatapp.classes;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    Image(Arrays.asList(new String[]{"*.png","*.jpg","*.jpeg","*.PNG","*.JPG","*.JPEG"})),
    Video(Arrays.asList(new String[]{"*.mp4","*.3gp","*.flv","*.avi","*.MP4","*.3GP","*.FLV","*.AVI"})),
    Audio(Arrays.asList(new String[]{"*.mp3","*.wav","*.MP3","*.WAV"})),
    File(Arrays.asList(new String[]{"*"}));

    public List<String> exts;
    ContentType(List<String> exts) {
        this.exts=exts;
    }

    public static ContentType get_type(java.io.File file){
        for (String image_ext:Image.exts) {
            if(file.getName().endsWith(image_ext.substring(image_ext.indexOf('.')+1))){
                return Image;
            }
        }
        for (String image_ext:Video.exts) {
            if(file.getName().endsWith(image_ext.substring(image_ext.indexOf('.')+1))){
                return Video;
            }
        }
        for (String image_ext:Audio.exts) {
            if(file.getName().endsWith(image_ext.substring(image_ext.indexOf('.')+1))){
                return Audio;
            }
        }
        return File;
    }
}