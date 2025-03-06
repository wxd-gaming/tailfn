package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 输出
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-28 14:56
 **/
public class ConsoleOutput extends Thread {

    public static final int limit = 20000;

    public static ConsoleOutput ins = null;

    public static void build(WebView webView) {
        ins = new ConsoleOutput(webView);
    }

    private final WebView webView;

    private final LinkedBlockingQueue<String> es = new LinkedBlockingQueue<>();

    private ConsoleOutput(WebView webView) {
        this.webView = webView;
        this.setDaemon(true);
        this.start();
        if ("true".equalsIgnoreCase(System.getProperty("build.graalvm"))) {
            /*为了处理如果字符太长会导致exe进程崩掉*/
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= limit; i++) {
                    sb.append("a");
                }
                String string = sb.toString();
                ConsoleOutput.ins.add(string);
            }).start();
        }
    }

    public void add(String e) {
        if (e.length() > limit) e = e.substring(0, limit);
        es.add(e);
    }

    @Override public void run() {
        while (!this.isInterrupted()) {
            try {
                String line = es.take();
                PlatformImpl.runAndWait(() -> {
                    try {
                        String escapedLine = StringEscapeUtils.escapeEcmaScript(line);
                        escapedLine = StringEscapeUtils.escapeHtml4(escapedLine);
                        webView.getEngine().executeScript("append(\"" + escapedLine + "\");");
                    } catch (Throwable e) {
                        System.err.println(line);
                        e.printStackTrace(System.err);
                    }
                });
            } catch (InterruptedException e) {
                break;
            } catch (Throwable throwable) {
                String string = Throw.ofString(throwable);
                System.err.println(string);
                GraalvmUtil.appendFile(string);
            }
        }
    }
}
