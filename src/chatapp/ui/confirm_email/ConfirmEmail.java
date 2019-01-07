package chatapp.ui.confirm_email;

import chatapp.classes.AppProperties;
import chatapp.classes.ServerInfo;
import chatapp.classes.ServerServices;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmEmail implements Initializable {


    @FXML
    private Text msg;

    @FXML
    private JFXTextField confirmation_code;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!ServerInfo.isOn())
            msg.setText("Server is down , Plz try later");
    }

    @FXML
    void close(MouseEvent event) {
        //Node src= (Node) event.getSource();
        Window win=msg.getScene().getWindow();
        win.hide();
    }


    @FXML
    void confirm(ActionEvent event) {
        if(!ServerInfo.isOn())
            msg.setText("Server is OFF , Plz try later");
        // send confirmation msg to server
        boolean res=ServerServices.confirm_user(AppProperties.currUser.getId(), Integer.parseInt(confirmation_code.getText()));
        if(res){
            AppProperties.currUser.setIs_connected(false);
            show_screen("Chat App", "/chatapp/ui/mainView/main_view.fxml");
            close(null);
        }else{
            msg.setText("Confirmation Code is Wrong");
        }
    }

    private void show_screen(String title,String path_to_fxml){
        try {
            Stage primaryStage=new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(path_to_fxml));
            primaryStage.setScene(new Scene(root));
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle(title);
            primaryStage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    AppProperties.save_changes();
                }
            });
            primaryStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
