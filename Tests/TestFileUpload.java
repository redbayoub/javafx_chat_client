import chatapp.classes.ServerServices;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFileUpload {
    @Test
    public void isUploadingFiles() throws FileNotFoundException {
        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.showOpenDialog(null);
        File selF=jFileChooser.getSelectedFile();
        String id= ServerServices.upload_file(new FileInputStream(selF),selF.getName());
        System.out.println(id);
        Assert.assertNotNull(id);
    }

    @Test
    public void isGettingFile() throws IOException {
        File file= ServerServices.get_file("5c138230f900843c41bdde3f");

        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.showSaveDialog(null);
        File selF=jFileChooser.getSelectedFile();

        Files.copy(Paths.get(file.toURI()), new FileOutputStream(selF));
        Assert.assertNotNull(file);
    }
}
