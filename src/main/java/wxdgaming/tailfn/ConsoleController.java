package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * 控制台
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 16:13
 **/
public class ConsoleController {

    public static Consumer<ConsoleController> initEndCallback = null;

    public WebView webView;
    public Menu menu_file;

    TailFN tailFN;

    public static String readHtml() {
        try (InputStream resourceAsStream = ConsoleController.class.getResourceAsStream("/consolebox.html")) {
            if (resourceAsStream == null) {
                throw new RuntimeException("加载文件失败");
            }
            byte[] bytes = GraalvmUtil.readInputStream(resourceAsStream);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("加载文件失败");
        }
    }

    public void init() {

        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // 处理未捕获的异常
            String string = Throw.ofString(throwable);
            GraalvmUtil.appendFile(thread + " 未捕获的异常\n" + string);
            // 可以在这里记录日志或显示错误信息
        });
        addMenuItem("退出进程", () -> System.exit(0));
        webView.getEngine().loadContent(readHtml());
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                new Thread(() -> {
                    try {
                        PlatformImpl.runAndWait(() -> {
                            webView.getEngine().executeScript(String.format("showMaxLine=%s", ViewConfig.ins.getShowMaxLine()));
                            setBg();
                            setFontSize();
                            setWarp();
                        });

                        ConsoleOutput.build(webView);
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ignored) {}
                            initTailFN();
                            if (initEndCallback != null) {
                                initEndCallback.accept(this);
                            }
                        }).start();
                    } catch (Exception e) {
                        String string = Throw.ofString(e);
                        System.err.println(string);
                        GraalvmUtil.appendFile(string);
                    }
                }).start();
            }
        });

    }

    void initTailFN() {
        if (StringUtils.isBlank(ViewConfig.ins.tailFNPath)) {
            ConsoleOutput.ins.add("尚未选择文件");
            return;
        }
        Path filePath = Paths.get(ViewConfig.ins.tailFNPath);
        if (!Files.exists(filePath.toAbsolutePath().getParent())) {
            ConsoleOutput.ins.add("需要监听的文件夹: " + filePath.toAbsolutePath() + " 异常");
            return;
        }
        tailFN = new TailFN(filePath, 1, ConsoleOutput.ins::add);
    }

    public void addMenuItem(String text, Runnable runnable) {
        addMenuItem(menu_file.getItems().size(), text, runnable);
    }

    public void addMenuItem(int index, String text, Runnable runnable) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(event -> runnable.run());
        addMenuItem(index, menuItem);
    }

    public void addMenuItem(int index, MenuItem item) {
        menu_file.getItems().add(index, item);
    }

    /** 插入分割符合 */
    public void addSeparatorMenuItem(int index) {
        menu_file.getItems().add(index, new SeparatorMenuItem());
    }

    /** 设置深色模式 */
    public void setDarkBgColor(ActionEvent event) {
        ViewConfig.ins.setBgColor("body_dark");
        setBg();
        ViewConfig.ins.save();
    }

    /** 设置浅色模式 */
    public void setLightBgColor(ActionEvent event) {
        ViewConfig.ins.setBgColor("body_light");
        setBg();
        ViewConfig.ins.save();
    }

    public void setSpanWarp(ActionEvent event) {
        ViewConfig.ins.setAutoWarp(true);
        setWarp();
        ViewConfig.ins.save();
    }

    public void setSpanNonWarp(ActionEvent event) {
        ViewConfig.ins.setAutoWarp(false);
        setWarp();
        ViewConfig.ins.save();
    }

    public void setZoomIn(ActionEvent event) {
        ViewConfig.ins.setFontSize(ViewConfig.ins.getFontSize() + 3);
        setFontSize();
        ViewConfig.ins.save();
    }

    public void setZoomReset(ActionEvent event) {
        ViewConfig.ins.setFontSize(13);
        setFontSize();
        ViewConfig.ins.save();
    }

    public void setZoomOut(ActionEvent event) {
        ViewConfig.ins.setFontSize(ViewConfig.ins.getFontSize() - 3);
        setFontSize();
        ViewConfig.ins.save();
    }

    public void setWarp() {
        if (ViewConfig.ins.isAutoWarp()) {
            webView.getEngine().executeScript("setSpanWarp();");
        } else {
            webView.getEngine().executeScript("setSpanNonWarp();");
        }
    }

    public void setBg() {
        if (StringUtils.isBlank(ViewConfig.ins.getBgColor())) {
            ViewConfig.ins.setBgColor("body_light");
        }
        webView.getEngine().executeScript(String.format("setBg('%s');", ViewConfig.ins.getBgColor()));
    }

    public void setFontSize() {
        if (ViewConfig.ins.getFontSize() < 5) {
            ViewConfig.ins.setFontSize(5);
        }
        webView.getEngine().executeScript(String.format("setFontSize(%s);", ViewConfig.ins.getFontSize()));
    }
}
