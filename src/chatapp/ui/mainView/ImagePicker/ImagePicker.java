package chatapp.ui.mainView.ImagePicker;

import chatapp.classes.ContentType;
import chatapp.ui.dialogs.Dialogs;
import chatapp.ui.mainView.DetailedAction;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImagePicker extends DetailedAction {
    public ImagePicker(Pane container_pane) {
        super(container_pane, "png");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ImagePicker.fxml"));
        loader.setController(this);
        try {
            initRootPane(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    private ImageView image_view;

    @FXML
    void pick_a_picture_from_files(ActionEvent event) throws IOException {
        File sel_file=Dialogs.pick_file(ContentType.Image);
        if(sel_file!=null){
            Files.copy(new FileInputStream(sel_file), getResult_file().toPath(),StandardCopyOption.REPLACE_EXISTING);
            Image sel_img=new Image(sel_file.toURI().toURL().toString());
            image_view.setFitHeight(sel_img.getHeight());
            image_view.setImage(sel_img);

        }

    }

    @FXML
    void take_a_picture(ActionEvent event) throws IOException {
        Image sel_img=Dialogs.take_a_picture();
        if(sel_img!=null){
            Files.copy(image_to_input_stream(sel_img), getResult_file().toPath(), StandardCopyOption.REPLACE_EXISTING);
            image_view.setFitHeight(sel_img.getHeight());
            image_view.setImage(sel_img);
        }
    }

    @Override
    public void cleanup() {
        getResult_file().delete();
    }

    private InputStream image_to_input_stream(Image image){
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", outputStream);
            byte[] res  = outputStream.toByteArray();
            return new ByteArrayInputStream(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
