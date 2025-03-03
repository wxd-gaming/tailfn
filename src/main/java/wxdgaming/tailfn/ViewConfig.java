package wxdgaming.tailfn;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
                PluginConfig pluginConfig = new PluginConfig();
                pluginConfig.setName("测试插件");
                pluginConfig.setPath("reload-game.bat");
                ins.getPluginList().add(pluginConfig);
                ins.save();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String filePath;
    private int lastN = 150;
    private int showMaxLine = 1500;
    private int fontSize = 13;
    private String bgColor = "body_light";
    private boolean autoWarp = false;
    private List<PluginConfig> pluginList = new ArrayList<>();

    public void save() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer, dumperOptions);
        LinkedHashMap<String, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
        objectObjectLinkedHashMap.put("filePath", filePath);
        objectObjectLinkedHashMap.put("lastN", lastN);
        objectObjectLinkedHashMap.put("showMaxLine", showMaxLine);
        objectObjectLinkedHashMap.put("fontSize", fontSize);
        objectObjectLinkedHashMap.put("bgColor", bgColor);
        objectObjectLinkedHashMap.put("autoWarp", autoWarp);
        ArrayList<LinkedHashMap<String, Object>> arrayList = new ArrayList<>();

        for (PluginConfig pluginConfig : pluginList) {
            LinkedHashMap<String, Object> objectObjectLinkedHashMap1 = new LinkedHashMap<>();
            objectObjectLinkedHashMap1.put("name", pluginConfig.getName());
            if (StringUtils.isNotBlank(pluginConfig.getPath()))
                objectObjectLinkedHashMap1.put("path", pluginConfig.getPath());
            if (pluginConfig.isCode())
                objectObjectLinkedHashMap1.put("code", true);
            if (pluginConfig.isAsync())
                objectObjectLinkedHashMap1.put("async", true);
            if (pluginConfig.isExit())
                objectObjectLinkedHashMap1.put("exit", true);
            arrayList.add(objectObjectLinkedHashMap1);
        }

        objectObjectLinkedHashMap.put("pluginList", arrayList);

        String string = yaml.dumpAsMap(objectObjectLinkedHashMap);
        GraalvmUtil.writeFile(viewPath, string);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLastN() {
        return lastN;
    }

    public void setLastN(int lastN) {
        this.lastN = lastN;
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

    public List<PluginConfig> getPluginList() {
        return pluginList;
    }

    public void setPluginList(List<PluginConfig> pluginList) {
        this.pluginList = pluginList;
    }

    public static class PluginConfig {

        private String name;
        private String path;
        private boolean code = false;
        private boolean async = false;
        private boolean exit = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isCode() {
            return code;
        }

        public void setCode(boolean code) {
            this.code = code;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public boolean isExit() {
            return exit;
        }

        public void setExit(boolean exit) {
            this.exit = exit;
        }
    }

}
