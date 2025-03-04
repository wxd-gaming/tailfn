package wxdgaming.tailfn;

import javafx.application.Application;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

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

            ViewConfig.loadYaml(configPath);

            if (args.length > 1) {
                File file = new File(args[1]);
                if (file.isFile() && file.getParentFile().exists()) {
                    GraalvmUtil.appendFile("启动传递监听文件：" + file.getAbsolutePath());
                    ViewConfig.ins.setFilePath(file.getAbsolutePath());
                } else {
                    GraalvmUtil.appendFile("启动传递监听文件：" + file.getAbsolutePath() + ", 路径不存在");
                }
            }

            Application.launch(ConsoleApplication.class);
        } catch (Throwable e) {
            String content = Throw.ofString(e);
            System.out.println(content);
            GraalvmUtil.appendFile(content);
        }
    }

    public static void buildGraalvm() throws Exception {
        if (!"true".equalsIgnoreCase(System.getProperty("build.graalvm"))) return;
        System.setProperty("build.graalvm", "");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        List<String> strings = GraalvmUtil.jarResources();
        for (String string : strings) {
            try {
                URL resource = contextClassLoader.getResource(string);
                if (resource != null) {
                    System.out.println(String.format("%s - %s", string, resource));
                }
            } catch (Throwable throwable) {
                String content = Throw.ofString(throwable);
                System.out.println(content);
            }
        }
        ReflectAction reflectAction = ReflectAction.of();

        reflectAction.action(JarFile.class, JarFile.class.getPackageName());
        reflectAction.action(ZipFile.class, ZipFile.class.getPackageName());
        reflectAction.action(Rotate.class, Rotate.class.getPackageName());

        List<Class<?>> classes = GraalvmUtil.jarClasses("wxdgaming");
        for (Class<?> cls : classes) {
            reflectAction.action(cls, cls.getPackageName());
        }
        Thread.sleep(2000);
    }
}