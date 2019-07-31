package chatapp.main;

import chatapp.classes.AppProperties;
import chatapp.classes.CacheController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.ws.rs.core.HttpHeaders;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/chatapp/ui/signView/sign.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Login / Sign");
        primaryStage.setOnHidden((event)-> {
                AppProperties.save_changes();

        });
        // to make window dragable
        root.setOnMousePressed((ev)->{
            xOffset=ev.getSceneX();
            yOffset=ev.getSceneY();
        });
        root.setOnMouseDragged((ev2)->{
            primaryStage.setX(ev2.getScreenX()- xOffset);
            primaryStage.setY(ev2.getScreenY()- yOffset);
        });

        primaryStage.show();

    }

    @Override
    public void stop() throws Exception { // this method are automatically called before stoping the app
        super.stop();
        // clear tmp dir
        for(File f:AppProperties.getTmp_dir().listFiles()){
            f.delete();
        }
        // cleanup cache dir
        CacheController.cleanup();
        System.exit(1);
    }

    // to make window dragable
    private double xOffset=0;
    private double yOffset=0;

    public static void main(String[] args) {

        launch(args);
        //test();
    }

    private static void test() {
        try {
            URL url=new URL("http://localhost:8080/upload/files/5c162a71f90084122459633a");
            URLConnection connection=url.openConnection();
            String name=connection.getHeaderField(HttpHeaders.CONTENT_DISPOSITION);
            System.out.println(name);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
