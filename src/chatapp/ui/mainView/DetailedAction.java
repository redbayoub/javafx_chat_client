package chatapp.ui.mainView;

import chatapp.classes.AppProperties;
import com.sun.istack.internal.NotNull;
import javafx.scene.layout.Pane;

import java.io.File;

public abstract class DetailedAction {
    private File result_file;
    private Pane container_pane;
    private Pane root_pane;
    public DetailedAction(@NotNull Pane container_pane,@NotNull String result_file_format){
        this.container_pane=container_pane;
        container_pane.setUserData(this);
        StringBuilder sb=new StringBuilder(String.valueOf(System.currentTimeMillis()));
        sb.append('.').append(result_file_format);
        this.result_file=new File(AppProperties.getTmp_dir(),sb.toString());
    }

    protected void initRootPane(Pane pane){
        this.root_pane=pane;
        container_pane.getChildren().add(root_pane);
    }

    public File getResult_file() {
        return result_file;
    }

    public abstract void cleanup();
}
