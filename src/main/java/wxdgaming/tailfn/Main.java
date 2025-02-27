package wxdgaming.tailfn;

import javafx.application.Application;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.net.URL;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        buildGraalvm();
        Application.launch(ConsoleApplication.class, args);
    }

    public static void buildGraalvm() throws Exception {
        if (StringUtils.isBlank(System.getProperty("build.graalvm"))) return;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        List<String> strings = GraalvmUtil.jarResources();
        for (String string : strings) {
            URL resource = contextClassLoader.getResource(string);
            System.out.println(String.format("%s - %s", string, resource));
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