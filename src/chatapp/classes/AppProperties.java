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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {
    private static Properties appProperties;
    public static User currUser;

    public static Properties getProperties(){
        if (appProperties == null) {
            appProperties = new Properties();
            try {
                appProperties.load(new FileInputStream("app.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return appProperties;
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
