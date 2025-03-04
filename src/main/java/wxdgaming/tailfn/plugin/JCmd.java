package wxdgaming.tailfn.plugin;

import wxdgaming.tailfn.GraalvmUtil;
import wxdgaming.tailfn.IJSPlugin;

import java.io.File;

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

    public void exec(boolean async, String workDir, String batFile) {
        GraalvmUtil.execLocalCommand(async, new File(workDir), batFile);
    }

}
