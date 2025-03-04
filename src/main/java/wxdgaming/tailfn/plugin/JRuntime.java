package wxdgaming.tailfn.plugin;

import wxdgaming.tailfn.IJSPlugin;

/**
 * 日志
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 11:33
 **/
public class JRuntime implements IJSPlugin {
    @Override public String getName() {
        return "JRuntime";
    }

    public void exit() {
        Runtime.getRuntime().exit(0);
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

}
