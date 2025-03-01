package code;

import wxdgaming.tailfn.IPlugin;

public class TestPlugin implements IPlugin {

    @Override public void exec() {
        System.out.println("TestPlugin");
    }

}
