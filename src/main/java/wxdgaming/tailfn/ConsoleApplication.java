package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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

    public static String __iconName = "logo.png";
    static AtomicBoolean icon_checked = new AtomicBoolean();

    public static Stage __primaryStage;

    @Override public void start(Stage primaryStage) throws Exception {

        /*阻止停止运行*/
        Platform.setImplicitExit(false);
        Image image_logo = new Image(__iconName);
        Class<ConsoleApplication> helloApplicationClass = ConsoleApplication.class;
        URL resource = helloApplicationClass.getResource("console.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent loaded = fxmlLoader.load();
        ConsoleController controller = fxmlLoader.getController();
        controller.init();
        Scene scene = new Scene(loaded, 1000, 600, false, SceneAntialiasing.BALANCED);
        primaryStage.setTitle("日志阅读器");
        primaryStage.getIcons().add(image_logo);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            closeSelect(primaryStage);
        });
        primaryStage.show();
        setIcon(primaryStage);
        __primaryStage = primaryStage;
    }

    /** 关闭事件选择 */
    public void closeSelect(Stage primaryStage) {
        if (icon_checked.get()) {
            Platform.runLater(primaryStage::hide);
        } else {
            /*最小化*/
            Platform.runLater(() -> primaryStage.setIconified(true));
        }
    }

    /** 开启系统托盘图标 */
    public void setIcon(Stage primaryStage) {
        try {
            if (SystemTray.isSupported()) {
                /*TODO 系统托盘图标*/
                SystemTray tray = SystemTray.getSystemTray();
                BufferedImage bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(__iconName));
                TrayIcon trayIcon = new TrayIcon(bufferedImage, "日志阅读器");
                trayIcon.setImageAutoSize(true);
                /*TODO 图标双击事件 */
                trayIcon.addActionListener(e -> PlatformImpl.runLater(primaryStage::show));
                // PopupMenu popupMenu = new PopupMenu();
                // {
                //     MenuItem mi_exit = new MenuItem("退出");
                //     // 指定支持中文的字体
                //     mi_exit.setFont(new Font("宋体", Font.PLAIN, 12));
                //     popupMenu.add(mi_exit);
                // }
                // trayIcon.setPopupMenu(popupMenu);
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
