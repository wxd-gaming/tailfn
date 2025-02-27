package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.event.ActionEvent;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 控制台
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 16:13
 **/
public class ConsoleController {

    public WebView webView;
    public TailFN tailFN;

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


                    tailFN = new TailFN(ViewConfig.ins.getFilePath(), ViewConfig.ins.getLastN(), line -> {
                        PlatformImpl.runAndWait(() -> {
                            try {
                                String escapedLine = StringEscapeUtils.escapeEcmaScript(line);
                                webView.getEngine().executeScript("append(\"" + escapedLine + "\");");
                            } catch (Exception e) {
                                System.err.println(line);
                                e.printStackTrace(System.err);
                            }
                        });
                    });

                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        });
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
