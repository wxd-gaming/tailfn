package wxdgaming.tailfn;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 控制台
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 16:13
 **/
public class ConsoleController {

    public WebView webView;
    public TailFN tailFN;
    public ConsoleOutput consoleOutput = null;
    public Menu menu_file;

    public static String readHtml() {
        try (InputStream resourceAsStream = ConsoleController.class.getResourceAsStream("/consolebox.html")) {
            if (resourceAsStream == null) {
                throw new RuntimeException("加载文件失败");
            }
            byte[] bytes = readInputStream(resourceAsStream);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("加载文件失败");
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public void init() {

        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // 处理未捕获的异常
            GraalvmUtil.appendFile("未捕获的异常: " + throwable.getMessage());
            GraalvmUtil.appendFile(throwable.toString());
            // 可以在这里记录日志或显示错误信息
        });

        webView.getEngine().loadContent(readHtml());
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                try {
                    webView.getEngine().executeScript("showMaxLine=%s".formatted(ViewConfig.ins.getShowMaxLine()));
                    if (StringUtils.isNotBlank(ViewConfig.ins.getBgColor())) {
                        webView.getEngine().executeScript("setBg('%s');".formatted(ViewConfig.ins.getBgColor()));
                    }
                    if (ViewConfig.ins.getFontSize() > 0) {
                        webView.getEngine().executeScript("setFontSize(%s);".formatted(ViewConfig.ins.getFontSize()));
                    }
                    if (ViewConfig.ins.isAutoWarp()) {
                        webView.getEngine().executeScript("setSpanWarp();");
                    } else {
                        webView.getEngine().executeScript("setSpanNonWarp();");
                    }

                    consoleOutput = new ConsoleOutput(webView, 2, 1);

                    initTailFN();

                } catch (Exception e) {
                    GraalvmUtil.appendFile(e.toString());
                }
            }
        });

        List<ViewConfig.MenuConfig> menuConfigs = ViewConfig.ins.getMenuConfigs();
        if (menuConfigs != null && !menuConfigs.isEmpty()) {
            int index = 1;
            SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
            menu_file.getItems().add(index, separatorMenuItem);
            for (ViewConfig.MenuConfig menuConfig : menuConfigs) {
                MenuItem menuItem;
                if ("——".equals(menuConfig.getName())) {
                    menuItem = new SeparatorMenuItem();
                } else {
                    menuItem = new MenuItem(menuConfig.getName());
                    menuItem.setText(menuConfig.getName());
                    menuItem.setUserData(menuConfig.getPath());
                    menuItem.setOnAction(event -> {
                        try {
                            File file = new File(menuConfig.getPath());
                            if (!file.exists()) {
                                appendLine("执行命令：" + menuConfig.getName() + ", 文件不存在: " + menuConfig.getPath());
                                return;
                            }
                            GraalvmUtil.asyncExeLocalCommand(menuConfig.getPath());
                        } finally {
                            try {
                                if (menuConfig.isExit()) {
                                    Runtime.getRuntime().halt(0);
                                }
                            } catch (Exception e) {
                                GraalvmUtil.appendFile(e.toString());
                            }
                        }
                    });
                }
                index++;
                menu_file.getItems().add(index, menuItem);
            }

        }

        // webView.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
        //     @Override public void handle(KeyEvent event) {
        //
        //     }
        // });
        //
        // // 添加键盘事件监听器
        // webView.setOnKeyPressed(new EventHandler<KeyEvent>() {
        //     @Override
        //     public void handle(KeyEvent event) {
        //         // 处理按键事件
        //         switch (event.getCode()) {
        //             case UP:
        //                 // 处理向上箭头键
        //                 break;
        //             case DOWN:
        //                 // 处理向下箭头键
        //                 break;
        //             // 其他按键处理...
        //             default:
        //                 break;
        //         }
        //     }
        // });

    }

    public void exit(ActionEvent event) {
        Runtime.getRuntime().halt(0);
    }

    public void initTailFN() {
        if (tailFN != null) tailFN.stop();
        if (StringUtils.isBlank(ViewConfig.ins.getFilePath())) {
            appendLine("尚未选择文件");
            return;
        }
        Path filePath = Paths.get(ViewConfig.ins.getFilePath());
        if (!Files.exists(filePath.toAbsolutePath().getParent())) {
            appendLine("需要监听的文件夹: " + filePath.toAbsolutePath() + " 异常");
            return;
        }
        tailFN = new TailFN(filePath, ViewConfig.ins.getLastN(), this::appendLine);
        ConsoleApplication.__primaryStage.setTitle("日志阅读器 " + ViewConfig.ins.getFilePath());
    }

    public void appendLine(String line) {
        this.consoleOutput.add(line);
    }

    public void selectFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择 日志 文件");
        File directory = new File(System.getProperty("user.dir"));
        if (StringUtils.isNotBlank(ViewConfig.ins.getFilePath())) {
            File file = new File(ViewConfig.ins.getFilePath());
            if (file.getParentFile().exists()) {
                directory = file.getParentFile();
            }
        }
        fileChooser.setInitialDirectory(directory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("txt Files", "*.log", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(webView.getScene().getWindow());
        if (selectedFile != null) {
            appendLine("选择的文件路径: " + selectedFile.getAbsolutePath());
            ViewConfig.ins.setFilePath(selectedFile.getAbsolutePath());
            ViewConfig.ins.save();
            initTailFN();
        }

    }

    /** 设置深色模式 */
    public void setDarkBgColor(ActionEvent event) {
        webView.getEngine().executeScript("setBg('body_dark');");
        ViewConfig.ins.setBgColor("body_dark");
        ViewConfig.ins.save();
    }

    /** 设置浅色模式 */
    public void setLightBgColor(ActionEvent event) {
        webView.getEngine().executeScript("setBg('body_light');");
        ViewConfig.ins.setBgColor("body_light");
        ViewConfig.ins.save();
    }

    public void setSpanWarp(ActionEvent event) {
        webView.getEngine().executeScript("setSpanWarp();");
        ViewConfig.ins.setAutoWarp(true);
        ViewConfig.ins.save();
    }

    public void setSpanNonWarp(ActionEvent event) {
        webView.getEngine().executeScript("setSpanNonWarp();");
        ViewConfig.ins.setAutoWarp(false);
        ViewConfig.ins.save();
    }

    public void setZoomIn(ActionEvent event) {
        ViewConfig.ins.setFontSize(ViewConfig.ins.getFontSize() + 3);
        webView.getEngine().executeScript("setFontSize(%s);".formatted(ViewConfig.ins.getFontSize()));
        ViewConfig.ins.save();
    }

    public void setZoomReset(ActionEvent event) {
        ViewConfig.ins.setFontSize(13);
        webView.getEngine().executeScript("setFontSize(%s);".formatted(ViewConfig.ins.getFontSize()));
        ViewConfig.ins.save();
    }

    public void setZoomOut(ActionEvent event) {
        ViewConfig.ins.setFontSize(ViewConfig.ins.getFontSize() - 3);
        webView.getEngine().executeScript("setFontSize(%s);".formatted(ViewConfig.ins.getFontSize()));
        ViewConfig.ins.save();
    }


}
