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

/**
 * 试图配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-26 15:22
 **/
@Getter
@Setter
public class ViewConfig {

    static Path viewPath = Paths.get("view.yml");

    public static ViewConfig ins = null;

    static {
        if (Files.exists(viewPath)) {
            loadYaml();
        } else {
            ins = new ViewConfig();
            ins.save();
        }
    }

    public static void loadYaml() {
        try {
            DumperOptions dumperOptions = new DumperOptions();
            Representer representer = new Representer(dumperOptions);
            representer.getPropertyUtils().setSkipMissingProperties(true);
            Yaml yaml = new Yaml(representer, dumperOptions);
            ins = yaml.loadAs(Files.newInputStream(viewPath), ViewConfig.class);
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


    public void save() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer, dumperOptions);
        String string = yaml.dumpAsMap(this);

        GraalvmUtil.writeFile(viewPath, string);
    }

}
