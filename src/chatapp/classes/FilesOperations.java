package chatapp.classes;

import java.io.*;
import java.nio.file.Files;

public class FilesOperations {
    public static boolean copy_file(File source,File dest){
        if(dest.exists())return true;
        try(OutputStream dest_os=new FileOutputStream(dest)) {

            long written_bytes=Files.copy(source.toPath(), dest_os);
            if(written_bytes>0)return true;
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean copy_file(InputStream source, File dest){
        if(dest.exists())return true;
        try {
            long written_bytes=Files.copy(source, dest.toPath());
            if(written_bytes>0)return true;
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean cut_file(File source,File dest){
        boolean res=copy_file(source, dest);
        source.delete();
        return res;
    }


}
