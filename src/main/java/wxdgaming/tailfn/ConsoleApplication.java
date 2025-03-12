package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 16:37
 */
public class ConsoleApplication extends Application {

    public static String __iconName = "game-logo.png";
    public static String __Title = "日志";
    public static AtomicBoolean icon_checked = new AtomicBoolean();

    public static Stage __primaryStage;
    public static ConsoleController __ConsoleController;
    public static Parent root = null;
    private static double xOffSet = 0;
    private static double yOffSet = 0;

    public static String lastSelectStyle = null;

    @Override public void start(Stage primaryStage) throws Exception {

        /*阻止停止运行*/
        Platform.setImplicitExit(false);
        Image image_logo = new Image(__iconName);
        Class<ConsoleApplication> helloApplicationClass = ConsoleApplication.class;
        URL resource = helloApplicationClass.getResource("console.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);

        root = fxmlLoader.load();

        __ConsoleController = fxmlLoader.getController();
        __ConsoleController.init();

        __ConsoleController.img_logo.setImage(image_logo);

        Scene scene = new Scene(root, 1000, 600, false, SceneAntialiasing.BALANCED);
        primaryStage.setTitle(__Title);
        __ConsoleController.lab_title.setText(__Title);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(image_logo);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            closeSelect();
        });

        setIcon(primaryStage);

        if (!StringUtils.isBlank(GraalvmUtil.classPath())) {
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
            primaryStage.setAlwaysOnTop(false);
        } else {
            primaryStage.setIconified(true);
        }
        __primaryStage = primaryStage;
        initDrag(__ConsoleController.lab_title);
        initDrag(__ConsoleController.mb);
    }

    public static void initDrag(Control control) {
        control.setOnMousePressed(event -> {
            ConsoleApplication.xOffSet = event.getSceneX();
            ConsoleApplication.yOffSet = event.getSceneY();
        });

        control.setOnMouseDragged(event -> {
            if (__primaryStage.isMaximized()) {
                __primaryStage.setMaximized(false);
                ConsoleApplication.xOffSet = event.getSceneX();
                ConsoleApplication.yOffSet = event.getSceneY();
            }
            __primaryStage.setX(event.getScreenX() - ConsoleApplication.xOffSet);
            __primaryStage.setY(event.getScreenY() - ConsoleApplication.yOffSet);
        });
    }

    /** 关闭事件选择 */
    public static void closeSelect() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText("确定要退出进程吗？");
        alert.setContentText("");
        alert.getButtonTypes().clear();
        ButtonType minButton = new ButtonType("最小化", ButtonBar.ButtonData.RIGHT);
        ButtonType exitButton = new ButtonType("退出", ButtonBar.ButtonData.RIGHT);
        ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().add(minButton);
        alert.getButtonTypes().add(exitButton);
        alert.getButtonTypes().add(cancelButton);
        alert.showAndWait().ifPresent(event -> {
            if (alert.getResult().equals(exitButton)) {
                /*走退出进程逻辑*/
                System.exit(0);
            } else if (alert.getResult().equals(minButton)) {
                window_min();
            }
        });

    }

    public static void window_max() {
        __primaryStage.setMaximized(!__primaryStage.isMaximized());
        __primaryStage.show();
    }

    public static void window_min() {
        if (icon_checked.get()) {
            Platform.runLater(__primaryStage::hide);
        } else {
            /*最小化*/
            Platform.runLater(() -> __primaryStage.setIconified(true));
        }
    }

    /** 开启系统托盘图标 */
    public void setIcon(Stage primaryStage) {
        try {
            if (SystemTray.isSupported()) {
                /*TODO 系统托盘图标*/
                SystemTray tray = SystemTray.getSystemTray();
                BufferedImage bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(__iconName));
                TrayIcon trayIcon = new TrayIcon(bufferedImage, __Title);
                trayIcon.setImageAutoSize(true);
                /*TODO 图标双击事件 */
                trayIcon.addActionListener(e -> {
                    PlatformImpl.runLater(() -> {
                        primaryStage.setIconified(false);
                        primaryStage.show();
                        primaryStage.setAlwaysOnTop(true);
                        primaryStage.setAlwaysOnTop(false);
                    });
                });
                tray.add(trayIcon);
                icon_checked.set(true);
                GraalvmUtil.appendFile("创建托盘图标");
            } else {
                GraalvmUtil.appendFile("当前系统不允许创建托盘图标");
            }
        } catch (Throwable e) {
            GraalvmUtil.appendFile("setIcon failed ");
            GraalvmUtil.appendFile(e.toString());
        }
    }

}
