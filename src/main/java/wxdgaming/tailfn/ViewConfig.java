package wxdgaming.tailfn;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

/**
 * 插件配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-26 15:22
 **/

public class ViewConfig {

    public static Path viewPath = null;

    public static ViewConfig ins = null;

    public static void loadYaml(String path) {
        try {
            viewPath = Paths.get(path);
            if (Files.exists(viewPath)) {
                DumperOptions dumperOptions = new DumperOptions();
                Representer representer = new Representer(dumperOptions);
                representer.getPropertyUtils().setSkipMissingProperties(true);
                Yaml yaml = new Yaml(representer, dumperOptions);
                ins = yaml.loadAs(Files.newInputStream(viewPath), ViewConfig.class);
            } else {
                ins = new ViewConfig();
                ins.save();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int showMaxLine = 1500;
    private int fontSize = 13;
    private String bgColor = "body_light";
    private boolean autoWarp = false;
    public String tailFNPath = null;

    public void save() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer, dumperOptions);
        LinkedHashMap<String, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
        objectObjectLinkedHashMap.put("showMaxLine", showMaxLine);
        objectObjectLinkedHashMap.put("fontSize", fontSize);
        objectObjectLinkedHashMap.put("bgColor", bgColor);
        objectObjectLinkedHashMap.put("autoWarp", autoWarp);
        String string = yaml.dumpAsMap(objectObjectLinkedHashMap);
        GraalvmUtil.writeFile(viewPath, string);
    }

    public int getShowMaxLine() {
        return showMaxLine;
    }

    public void setShowMaxLine(int showMaxLine) {
        this.showMaxLine = showMaxLine;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public boolean isAutoWarp() {
        return autoWarp;
    }

    public void setAutoWarp(boolean autoWarp) {
        this.autoWarp = autoWarp;
    }
}
