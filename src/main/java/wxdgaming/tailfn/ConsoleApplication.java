package wxdgaming.tailfn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import java.net.URL;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 16:37
 */
public class ConsoleApplication extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        Class<ConsoleApplication> helloApplicationClass = ConsoleApplication.class;
        URL resource = helloApplicationClass.getResource("console.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent loaded = fxmlLoader.load();
        ConsoleController controller = fxmlLoader.getController();
        controller.init();
        Scene scene = new Scene(loaded, 1000, 600, false, SceneAntialiasing.BALANCED);
        primaryStage.setTitle("日志阅读器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
