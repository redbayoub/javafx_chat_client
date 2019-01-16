package chatapp.classes;

import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;

public final class CacheController {
    private static final byte BYTE=1;
    private static final int KILOBYTE=1024*BYTE;
    private static final long MEGABYTE=1024*KILOBYTE;
    private static final long GEGABYTE=1024*MEGABYTE;

    // file_id,file_uri
    private static HashMap<String,String> cached_files=new HashMap<>();
    // file_id,file_uri
    private static HashMap<String,String> cached_avatars=new HashMap<>();

    private final static File CACHE_DIR=new File(URI.create(AppProperties.getProperties().getProperty("cache.directory.uri")));
    private final static File FILES_DIR;
    private final static File AVATARS_DIR;
    private final static long MAX_SIZE;
    static {
        if(!CACHE_DIR.exists())CACHE_DIR.mkdir();
        MAX_SIZE=getMaxSize();
        FILES_DIR=new File(CACHE_DIR,"files");
        FILES_DIR.mkdir();
        AVATARS_DIR=new File(CACHE_DIR,"avatars");
        AVATARS_DIR.mkdir();
    }
    private static long getMaxSize(){
        String raw_max_size=AppProperties.getProperties().getProperty("cache.directory.max.size");
        String max_size=raw_max_size.substring(0, raw_max_size.length()-1);
        char size_unit=raw_max_size.charAt(raw_max_size.length()-1);
        long max_size_in_bytes=0;
        switch (size_unit){
            case 'B':{
                max_size_in_bytes=Integer.parseInt(max_size)*BYTE;
                break;
            }
            case 'K':{
                max_size_in_bytes=Integer.parseInt(max_size)*KILOBYTE;
                break;
            }
            case 'M':{
                max_size_in_bytes=Integer.parseInt(max_size)*MEGABYTE;
                break;
            }
            case 'G':{
                max_size_in_bytes=Integer.parseInt(max_size)*GEGABYTE;
                break;
            }
        }
        return max_size_in_bytes;
    }
    private static File download_file_to_cache(String file_id) throws Exception {
        File recived_file = ServerServices.get_file(file_id);
        check_max_size(recived_file);

        File dest_file = new File(FILES_DIR, recived_file.getName());
        if (FilesOperations.cut_file(recived_file, dest_file)) {
            cached_files.put(file_id, dest_file.toURI().toString());
            return dest_file;
        } else {
            throw new Exception("file cannot copied");
        }
    }
    private static File download_avatar_to_cache(String file_id) throws Exception {
        File recived_file=ServerServices.get_file(file_id);
        File dest_file = new File(AVATARS_DIR, recived_file.getName());
        if (FilesOperations.cut_file(recived_file, dest_file)) {
            cached_avatars.put(file_id, dest_file.toURI().toString());
            return dest_file;
        } else {
            throw new Exception("file cannot copied");
        }
    }
    private static void check_max_size(File recived_file) {
        while (FILES_DIR.length()+recived_file.length()>MAX_SIZE){
            // DELETE THE BIG FILE
            File removed_file=null;
            for (File f:FILES_DIR.listFiles()) {
                if(removed_file==null || removed_file.length() < f.length() ){
                    removed_file=f;
                }
            }
            if(removed_file==null){
                return;
            }else{
                removed_file.delete();
                //delete the entry from cached_files
            }
        }

    }

    public static File get_file(String file_id){
        if(file_id==null || file_id.isEmpty())return null;
        if(cached_files.containsKey(file_id)){
            return new File(URI.create(cached_files.get(file_id)));
        }else{
            try {
                return download_file_to_cache(file_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Image get_avatar(String avatar_id){
        if(avatar_id==null || avatar_id.isEmpty())return null;
        if(cached_avatars.containsKey(avatar_id)){
            try {
                return new Image(URI.create(cached_avatars.get(avatar_id)).toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                return new Image(download_avatar_to_cache(avatar_id).toURI().toURL().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void cleanup(){
        for (File f:CACHE_DIR.listFiles()) {
            f.delete();
        }
        cached_files.clear();
        cached_avatars.clear();
    }
}
