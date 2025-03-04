package wxdgaming.tailfn.plugin;

import wxdgaming.tailfn.GraalvmUtil;
import wxdgaming.tailfn.IJSPlugin;

/**
 * java 调用 cmd
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 14:16
 **/
public class JCmd implements IJSPlugin {

    @Override public String getName() {
        return "JCmd";
    }

    public void exec(boolean async, String batFile) {
        GraalvmUtil.execLocalCommand(async, batFile);
    }

}
