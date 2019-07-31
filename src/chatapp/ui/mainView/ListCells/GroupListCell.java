package chatapp.ui.mainView.ListCells;

import chatapp.classes.model.Group;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.GridCell;

public class GroupListCell extends GridCell<Group> {
    private VBox vBox=new VBox();
    private Label group_name=new Label();
    private Label mem_nb=new Label();
    public GroupListCell(){
        group_name.setPadding(new Insets(5));
        group_name.setTextFill(Color.WHITE);
        mem_nb.setPadding(new Insets(5));
        mem_nb.setTextFill(Color.WHITE);

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().setAll(group_name,mem_nb);
        vBox.setFillWidth(true);

    }
    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if(item!=null&&!empty){
            if(item.getGroup_id()==-1){ // add group
                Label plus=new Label("+");
                plus.setTextFill(Color.WHITE);
                plus.setFont(Font.font(20));
                StackPane plus_holder=new StackPane(plus);
                plus_holder.setAlignment(Pos.CENTER);
                setGraphic(plus_holder);
                setStyle("-fx-background-color: #000000; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 0; -fx-padding: 10; -fx-pref-width: 145; -fx-max-width: 200; -fx-max-width: 200; -fx-pref-height: 130; -fx-max-height: 130; -fx-effect: dropshadow(three-pass-box, #93948d, 10, 0, 0, 0);");
            }else{
                if(getIndex()==getGridView().getItems().size()-1){
                    Group sp_group=new Group(); // id = -1 for adding group
                    sp_group.setGroup_id(-1);
                    getGridView().getItems().add(sp_group);
                }
                    group_name.setText(item.getName());
                    mem_nb.setText(item.getMembers().size()+" Member");
                    setGraphic(vBox);
                    setStyle("-fx-background-color: #16191A; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 0; -fx-padding: 10; -fx-pref-width: 145; -fx-max-width: 200; -fx-max-width: 200; -fx-pref-height: 130; -fx-max-height: 130; -fx-effect: dropshadow(three-pass-box, #93948d, 10, 0, 0, 0);");



            }
        }

    }
}
