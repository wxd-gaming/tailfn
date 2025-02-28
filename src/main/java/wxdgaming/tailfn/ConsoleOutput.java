package wxdgaming.tailfn;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.web.WebView;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 输出
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-28 14:56
 **/
public class ConsoleOutput extends Thread {

    public WebView webView;
    private int limit;
    private int duration;
    private List<List<String>> es = new LinkedList<>();
    private List<String> parts;

    public ConsoleOutput(WebView webView, int limit, int duration) {
        this.webView = webView;
        this.limit = limit;
        this.duration = duration;
        reset();
        this.setDaemon(true);
        this.start();
    }

    void reset() {
        synchronized (ConsoleOutput.this) {
            es.add(parts);
            parts = new ArrayList<>(limit + 1);
        }
    }

    public List<String> removeFirst() {
        synchronized (ConsoleOutput.this) {
            return es.removeFirst();
        }
    }

    public void add(String e) {
        synchronized (ConsoleOutput.this) {
            parts.add(e);
            if (parts.size() >= limit) {
                reset();
            }
        }
    }

    @Override public void run() {
        while (!this.isInterrupted()) {
            try {
                Thread.sleep(duration);
                synchronized (ConsoleOutput.this) {
                    if (es.isEmpty() && !parts.isEmpty()) {
                        reset();
                    }
                }

                if (es.isEmpty()) continue;

                List<String> list = removeFirst();
                PlatformImpl.runAndWait(() -> {
                    for (String line : list) {
                        try {
                            String escapedLine = StringEscapeUtils.escapeEcmaScript(line);
                            webView.getEngine().executeScript("append(\"" + escapedLine + "\");");
                        } catch (Exception e) {
                            System.err.println(line);
                            e.printStackTrace(System.err);
                        }
                    }
                });
            } catch (Throwable ignored) {}
        }
    }
}
