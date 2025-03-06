package wxdgaming.tailfn;

import javafx.application.Application;

import java.util.Date;

/**
 * 测试启动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-06 09:39
 **/
public class TestMain {

    public static void main(String[] args) {
        ViewConfig.loadYaml("target/tail-view.yml");
        String format = GraalvmUtil.formatter.format(new Date());
        ViewConfig.ins.tailFNPath = String.format("target/logs/tail-view.yml-%s.log", format);
        Application.launch(ConsoleApplication.class);
    }

}
