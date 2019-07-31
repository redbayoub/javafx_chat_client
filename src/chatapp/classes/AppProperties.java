package chatapp.classes;

import chatapp.classes.model.User;
import eu.hansolo.enzo.notification.Notification;
import eu.hansolo.enzo.notification.NotifierBuilder;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {
    private static Properties appProperties;
    public static User currUser;
    private static File tmp_dir;


    public static File getTmp_dir() {
        return tmp_dir;
    }

    public static Properties getProperties(){
        if (appProperties == null) {
            appProperties = new Properties();
            try {
                appProperties.load(new FileInputStream("app.properties"));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        initAbsentProperties();
        if(tmp_dir==null){
            tmp_dir=new File(System.getProperty("java.io.tmpdir"),"ChatApp");
        }
        return appProperties;
    }

    private static void initAbsentProperties() {
        boolean absent_exist=false;
        if( appProperties.putIfAbsent("refresh.chat.interval", 20)==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("refresh.friend.request.interval",60 )==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("refresh.recent.msg.interval", 60)==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("refresh.friend.interval",60 )==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("refresh.group.interval",60 )==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("server.uri", "http\\://localhost\\:8080/")==null&&!absent_exist){
            absent_exist=true;
        };
        if(appProperties.putIfAbsent("cache.directory.max.size", "200M")==null&&!absent_exist){
            absent_exist=true;
        };

        if (!appProperties.containsKey("cache.directory.uri")){
            if(!absent_exist)absent_exist=true;
            File cache_dir=new File("cache");
            cache_dir.mkdir();
            appProperties.put("cache.directory.uri", cache_dir.toURI().toString());
        }

        //appProperties.putIfAbsent("cache.folder.path",new File("cache").mkdir( );
        if(absent_exist) { // save cahnges
            save_changes();
        }
    }

    public static void save_changes(){
        if(appProperties==null)return;
        try {
            appProperties.store(new FileOutputStream("app.properties"), "App properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showNotification(String title, String content, Image icon){
        Notification notification=new Notification(title, content,icon);

        Notification.Notifier notifire= NotifierBuilder.create()
                .popupLifeTime(Duration.seconds(5))
                .popupLocation(Pos.TOP_RIGHT)
                .build();
        notifire.notify(notification);
        notifire.setOnHideNotification((ev)->{
            notifire.stop();
        });
    }

    // to make window dragable
    private static double xOffset=0;
    private static double yOffset=0;

    public static Stage get_screen(String title,String path_to_fxml){
        Parent root = null;
        try {
            Stage primaryStage=new Stage();
            root = FXMLLoader.load(AppProperties.class.getResource(path_to_fxml));
            Scene scene=new Scene(root);
            scene.setOnMousePressed(event -> {});
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle(title);


            // to make window dragable
            root.setOnMousePressed((ev)->{
                xOffset=ev.getSceneX();
                yOffset=ev.getSceneY();
            });
            root.setOnMouseDragged((ev2)->{
                primaryStage.setX(ev2.getScreenX()- xOffset);
                primaryStage.setY(ev2.getScreenY()- yOffset);
            });


            // Need This if we change app properties
            /*primaryStage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    AppProperties.save_changes();
                }
            });*/
            return primaryStage;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
