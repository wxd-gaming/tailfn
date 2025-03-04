package wxdgaming.tailfn.plugin;

import wxdgaming.tailfn.ConsoleOutput;
import wxdgaming.tailfn.GraalvmUtil;
import wxdgaming.tailfn.IJSPlugin;

/**
 * 日志
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 11:33
 **/
public class JLog implements IJSPlugin {
    @Override public String getName() {
        return "JLog";
    }

    public void print(String msg) {
        ConsoleOutput.ins.add(msg);
    }

    public void appendFile(String msg) {
        GraalvmUtil.appendFile(msg);
    }

}
