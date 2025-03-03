package wxdgaming.tailfn;

import javafx.application.Application;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            String configPath = "tail-view.yml";
            if (args.length > 0) {
                configPath = args[0];
                GraalvmUtil.appendFile("外包配置文件路径：" + new File(configPath).getAbsolutePath());
            }

            ViewConfig.loadYaml(configPath);

            buildGraalvm();
            if (args.length > 1) {
                File file = new File(args[1]);
                if (file.isFile() && file.getParentFile().exists()) {
                    GraalvmUtil.appendFile("启动传递监听文件：" + file.getAbsolutePath());
                    ViewConfig.ins.setFilePath(file.getAbsolutePath());
                } else {
                    GraalvmUtil.appendFile("启动传递监听文件：" + file.getAbsolutePath() + ", 路径不存在");
                }
            }

            Application.launch(ConsoleApplication.class, args);
        } catch (Exception e) {
            GraalvmUtil.appendFile(e.toString());
        }
    }

    public static void buildGraalvm() throws Exception {
        if (StringUtils.isBlank(System.getProperty("build.graalvm"))) return;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        List<String> strings = GraalvmUtil.jarResources();
        for (String string : strings) {
            URL resource = contextClassLoader.getResource(string);
            GraalvmUtil.appendFile(String.format("%s - %s", string, resource));
        }

        reflectAction("com.sun.javafx");
        reflectAction("javafx");
        reflectAction("wxdgaming");
    }

    public static void reflectAction(String packageName) {
        ReflectAction reflectAction = ReflectAction.of();

    }
}