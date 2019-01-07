package chatapp.ui.signView;

import chatapp.classes.AppProperties;
import chatapp.classes.Encryption;
import chatapp.classes.ServerInfo;
import chatapp.classes.ServerServices;
import chatapp.classes.model.User;
import chatapp.ui.dialogs.Dialogs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.enzo.notification.Notification;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Sign implements Initializable {

    @FXML
    private ImageView localProfileAvatar;

    @FXML
    private JFXTextField localUsername;

    @FXML
    private JFXCheckBox local_rem_log_info;

    @FXML
    private JFXButton logn_btn;

    @FXML
    private JFXButton sign_btn;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private ImageView loginProfileAvatar;

    @FXML
    private JFXTextField login_email;

    @FXML
    private JFXPasswordField login_password;

    @FXML
    private JFXCheckBox login_rem_log_info;

    @FXML
    private AnchorPane SignUpPane;

    @FXML
    private JFXTextField signUsername;

    @FXML
    private JFXTextField signEmail;

    @FXML
    private JFXPasswordField signPassword;

    @FXML
    private JFXPasswordField signRePassorwd;

    @FXML
    private JFXCheckBox sign_rem_log_info;

    @FXML
    private ImageView signUpAvatar;

    @FXML
    private AnchorPane globalPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!ServerInfo.isOn()) {
            globalPane.setDisable(true);

        }else { // init global pane
            loginPane.setVisible(true);
            SignUpPane.setVisible(false);
            login_email.setText(AppProperties.getProperties().getProperty("global.email", null));
            if(AppProperties.getProperties().containsKey("global.password_sha1")){
                login_password.setText(AppProperties.getProperties().getProperty("global.password_sha1",null ));
                login_password.setAccessibleText("sha1");
                ChangeListener listener=new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        login_password.setText("");
                        login_password.setAccessibleText("");
                        login_password.textProperty().removeListener(this);

                        loginProfileAvatar.setImage(new Image(getClass().getClassLoader().getResourceAsStream("chatapp/images/defualt_user_avatar.png")));

                        // remove properties
                        AppProperties.getProperties().remove("global.password_sha1");
                        AppProperties.getProperties().remove("global.email");
                        if(AppProperties.getProperties().containsKey("global.avatar") && !AppProperties.getProperties().getProperty("global.avatar").isEmpty()){
                            //remove file with the property
                            new File(AppProperties.getProperties().getProperty("global.avatar")).delete();
                            AppProperties.getProperties().remove("global.avatar");
                        }
                        login_email.textProperty().removeListener(this);
                        login_password.textProperty().removeListener(this);

                    }
                };
                login_email.textProperty().addListener(listener);
                login_password.textProperty().addListener(listener);
            }


            if (AppProperties.getProperties().containsKey("global.avatar"))
                loginProfileAvatar.setImage(new Image(AppProperties.getProperties().getProperty("global.avatar", null)));
        }
        localUsername.setText(AppProperties.getProperties().getProperty("local.username", null));
        if (AppProperties.getProperties().containsKey("local.avatar"))
            localProfileAvatar.setImage(new Image(AppProperties.getProperties().getProperty("local.avatar", null)));


    }

    @FXML
    void close(MouseEvent event) {
        //Node src= (Node) event.getSource();
        Window win=sign_btn.getScene().getWindow();
        win.hide();
    }

    @FXML
    void enter_chat(ActionEvent event) {
        if(localUsername.getText().isEmpty())return;
        if(local_rem_log_info.isSelected()){
            AppProperties.getProperties().put("local.username", localUsername.getText());
            if(localProfileAvatar.getAccessibleText().isEmpty()){ // empty whene photo changed
                try {
                    File image=new File("localProfileAvatar.jpg");
                    ImageIO.write(SwingFXUtils.fromFXImage(localProfileAvatar.getImage(), null),"JPG" ,image );
                    AppProperties.getProperties().put("local.avatar", image.toURI().toURL().toExternalForm());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // TODO Enter local chat
    }

    @FXML
    void global_pick_a_pic(ActionEvent event) throws MalformedURLException {
        File chosen_file=Dialogs.pick_file(Dialogs.ContentType.Image);
        if(chosen_file!=null){
            signUpAvatar.setImage(new Image(chosen_file.toURI().toURL().toExternalForm()));
            signUpAvatar.setAccessibleText("");
        }
    }

    @FXML
    void global_take_a_pic(ActionEvent event) {
        Image sel_image=Dialogs.take_a_picture();
        if(sel_image!=null){
            signUpAvatar.setImage(sel_image);
            signUpAvatar.setAccessibleText("");
        }


    }


    @FXML
    void logIn(ActionEvent event) {
        if(loginPane.isVisible()){
            // login
            // validate inputs

            String pass_sha1;
            if(login_password.getAccessibleText()!=null && login_password.getAccessibleText().equals("sha1")){
                pass_sha1=login_password.getText();
            }else{
                pass_sha1=Encryption.encryptThisString(login_password.getText());
            }

            User retUser=ServerServices.login_user(login_email.getText(), pass_sha1);
            if(retUser!=null&&retUser.getId()!=0){
                AppProperties.currUser=retUser;

                if(login_rem_log_info.isSelected()) {
                    AppProperties.getProperties().put("global.email", retUser.getEmail());
                    AppProperties.getProperties().put("global.password_sha1", retUser.getPassword_sha1());
                    if(retUser.getProfile_image_path()!=null && !retUser.getProfile_image_path().isEmpty()){
                        // save image to files
                        try {
                            File serv_file=ServerServices.get_file(retUser.getProfile_image_path());
                            Image image=new Image(new FileInputStream(serv_file));
                            String local_path =save_image(image,retUser.getUsername()+"_login_image.png" );

                            AppProperties.getProperties().put("global.avatar",local_path );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    AppProperties.save_changes();
                }
                if(retUser.isIs_confirmed()){
                    AppProperties.get_screen("Chat App", "/chatapp/ui/mainView/main_view.fxml").show();
                }else{
                    AppProperties.get_screen("Email Confirmation", "/chatapp/ui/confirm_email/confirm_email.fxml").show();
                }
                close(null);
            }else{ // TODO show error msg
                String err_msg="";
                if(retUser!=null){
                    err_msg=retUser.getUsername();
                }
                AppProperties.showNotification("Login Failed", err_msg, Notification.ERROR_ICON);

            }


        }else{ // show login
            SignUpPane.setVisible(false);
            loginPane.setVisible(true);
        }

    }

    @FXML
    void pic_a_pic(ActionEvent event) throws MalformedURLException {
        File sel_file= Dialogs.pick_file(Dialogs.ContentType.Image);
        if(sel_file!=null){
            localProfileAvatar.setImage(new Image(sel_file.toURI().toURL().toExternalForm()));
            localProfileAvatar.setAccessibleText("");
        }

    }

    @FXML
    void signUp(ActionEvent event) {
        if(SignUpPane.isVisible()){
            // SignUp
            // validate inputs

            User newUser=new User();
            newUser.setUsername(signUsername.getText());
            newUser.setEmail(signEmail.getText());
            newUser.setPassword_sha1(Encryption.encryptThisString(signPassword.getText()));
            newUser.setIs_connected(false);
            newUser.setIs_confirmed(false);
            if (signUpAvatar.getAccessibleText().isEmpty()){// image changed
                String image_server_path=ServerServices.upload_file(image_to_input_stream(signUpAvatar.getImage()),signUsername.getText()+"_login_image.png");
                newUser.setProfile_image_path(image_server_path);
            }
            AppProperties.currUser=newUser;

            User retUser=ServerServices.add_user(newUser);
            if(retUser!=null&&retUser.getId()!=0){
                AppProperties.currUser=retUser;
                if(sign_rem_log_info.isSelected()){
                    AppProperties.getProperties().put("global.email", newUser.getEmail());
                    AppProperties.getProperties().put("global.password_sha1", newUser.getPassword_sha1());
                    if(signUpAvatar.getAccessibleText().isEmpty()){ // empty whene photo changed
                        String image_path=save_image(signUpAvatar.getImage(),signUsername.getText()+"_login_image.png");
                        AppProperties.getProperties().put("global.avatar", image_path);
                    }
                    AppProperties.save_changes();
                }
                AppProperties.get_screen("Email Confirmation", "/chatapp/ui/confirm_email/confirm_email.fxml").show();
                close(null);
            }else{
                String err_msg="";
                if(retUser!=null){
                    err_msg=retUser.getUsername();
                }
                AppProperties.showNotification("Sign Up Failed", err_msg, Notification.ERROR_ICON);
            }
        }else{ // show SignUp
            loginPane.setVisible(false);
            SignUpPane.setVisible(true);
        }
    }

    private String save_image(Image sel_image,String filename) {
        try {
            File image_file=new File(filename);
            ImageIO.write(SwingFXUtils.fromFXImage(sel_image, null),"png" ,image_file );
            return image_file.toURI().toURL().toExternalForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    void take_pic(ActionEvent event) {
        Image sel_image=Dialogs.take_a_picture();
        if(sel_image!=null){
            localProfileAvatar.setImage(sel_image);
            localProfileAvatar.setAccessibleText("");
        }

    }

    private InputStream image_to_input_stream(Image image){
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", outputStream);
            byte[] res  = outputStream.toByteArray();
            outputStream.close();
            return new ByteArrayInputStream(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
