package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.web.WebView;
import org.apache.commons.text.StringEscapeUtils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 输出
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-28 14:56
 **/
public class ConsoleOutput extends Thread {

    private final WebView webView;
    private final int limit;
    private final int duration;
    private final LinkedBlockingQueue<String> es = new LinkedBlockingQueue<>();

    public ConsoleOutput(WebView webView, int limit, int duration) {
        this.webView = webView;
        this.limit = limit;
        this.duration = duration;
        this.setDaemon(true);
        this.start();
    }

    public void add(String e) {
        es.add(e);
    }

    @Override public void run() {
        while (!this.isInterrupted()) {
            try {
                String line = es.take();
                PlatformImpl.runAndWait(() -> {
                    try {
                        String escapedLine = StringEscapeUtils.escapeEcmaScript(line);
                        webView.getEngine().executeScript("append(\"" + escapedLine + "\");");
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace(System.err);
                    }
                });
            } catch (InterruptedException e) {
                break;
            } catch (Throwable throwable) {
                GraalvmUtil.appendFile(throwable.toString());
            }
        }
    }
}
