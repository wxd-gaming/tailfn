package wxdgaming.tailfn;

import javafx.application.Application;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.io.File;
import java.net.URL;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        buildGraalvm();
        ViewConfig ins = ViewConfig.ins;
        if (args.length > 0) {
            String filePath = args[0];
            File file = new File(filePath);
            if (file.isFile() && file.getParentFile().exists()) {
                GraalvmUtil.appendFile("启动传递监听文件：" + filePath);
                ins.setFilePath(filePath);
            } else {
                GraalvmUtil.appendFile("启动传递监听文件：" + filePath + ", 路径不存在");
            }

        }
        Application.launch(ConsoleApplication.class, args);
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
        Reflections reflections = new Reflections(packageName);
        reflections.getSubTypesOf(Object.class).stream().parallel().forEach(clazz -> {
            try {
                reflectAction.action(clazz, false);
            } catch (Exception ignored) {}
        });
    }
}