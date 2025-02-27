package wxdgaming.tailfn;

import javafx.application.Application;
import org.apache.commons.lang3.StringUtils;

public class Main {

    public static void main(String[] args) {
        Application.launch(ConsoleApplication.class, args);
    }

    public static void buildGraalvm() {
        if (StringUtils.isBlank(System.getProperty("build.graalvm"))) return;
    }

}