package wxdgaming.tailfn;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

/**
 * 自定义弹出框
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-12 15:57
 **/
public class DiyAlert {

    public StackPane root;
    public Label lab_title;
    public Label lab_Content;

    public Stage stage;
    private double xOffSet = 0;
    private double yOffSet = 0;

    public static DiyAlert build() throws Exception {
        Stage stage = new Stage();
        Class<DiyAlert> diyAlertClass = DiyAlert.class;
        URL resource = diyAlertClass.getResource("diyalert.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent load = fxmlLoader.load();
        DiyAlert diyAlert = fxmlLoader.getController();
        diyAlert.stage = stage;
        diyAlert.initDrag();
        Scene scene = new Scene(load, 400, 200, false, SceneAntialiasing.BALANCED);
        // 设置场景的填充颜色为透明
        scene.setFill(new Color(0, 0, 0, 0));
        {
            String replace = diyAlertClass.getPackage().getName().replace(".", "/");
            ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.removeIf(v -> v.contains(replace));
            String name = "/" + replace + "/" + ViewConfig.ins.getBgColor() + "/theme-alert.css";
            URL cssResource = diyAlertClass.getResource(name);
            scene.getStylesheets().add(cssResource.toExternalForm());
        }
        /* alert 方式打开窗体 */
        stage.initModality(Modality.APPLICATION_MODAL);
        /*设置主窗体*/
        stage.initOwner(ConsoleApplication.__primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        return diyAlert;
    }

    public void btn_exit(ActionEvent event) {
        stage.close();
    }

    int btn_count = 0;

    public void addButton(String text, Runnable runnable) {
        Button btn = new Button(text);
        btn.setOnAction(event -> {
            runnable.run();
            stage.close();
        });
        btn.setPrefWidth(60);
        int right = 10;
        if (btn_count > 0) {
            right += btn_count * 60;
            right += (btn_count) * 10;
        }
        btn.getStyleClass().add("btn_diy");
        StackPane.setMargin(btn, new Insets(0, right, 10, 0));
        StackPane.setAlignment(btn, Pos.BOTTOM_RIGHT);
        root.getChildren().add(btn);
        btn_count++;
    }

    public void initDrag() {
        lab_title.setOnMousePressed(event -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });

        lab_title.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffSet);
            stage.setY(event.getScreenY() - yOffSet);
        });
    }


}
