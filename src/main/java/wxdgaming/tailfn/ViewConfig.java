package wxdgaming.tailfn;


import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-26 15:22
 **/
@Getter
@Setter
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
        String string = yaml.dumpAsMap(this);
        GraalvmUtil.writeFile(viewPath, string);
    }


    @Setter
    @Getter
    public static class PluginConfig {

        private String name;
        private String path;
        private Boolean code = null;
        private Boolean async = null;
        private Boolean exit = null;

    }

}
