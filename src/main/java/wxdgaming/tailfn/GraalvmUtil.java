package wxdgaming.tailfn;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GraalvmUtil {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static void writeFile(Path path, String content) {
        try {
            Files.writeString(
                    path,
                    content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void appendFile(String content) {
        try {
            Path path = Paths.get("target/logs/tail-%s.log".formatted(formatter.format(new Date())));
            try {
                path.toAbsolutePath().toFile().getParentFile().mkdirs();
            } catch (Exception ignored) {}
            Files.writeString(
                    path,
                    "[%s] - %s\n".formatted(formatter2.format(new Date()), content),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static String javaClassPath() {
        return System.getProperty("java.class.path");
    }

    public static List<String> jarResources() throws Exception {
        List<String> resourcesPath = new ArrayList<>();
        String x = javaClassPath();
        String[] split = x.split(File.pathSeparator);
        List<String> collect = Arrays.stream(split).sorted().collect(Collectors.toList());
        for (String string : collect) {

            Path start = Paths.get(string);
            if (!string.endsWith(".jar") && !string.endsWith(".war") && !string.endsWith(".zip")) {
                if (string.endsWith("classes")) {
                    try (Stream<Path> stream = Files.walk(start)) {
                        String target = start.toString();
                        stream
                                .map(Path::toString)
                                .filter(s -> s.startsWith(target) && s.length() > target.length())
                                .map(s -> {
                                    String replace = s.replace(target + File.separator, "");
                                    if (replace.endsWith(".class")) {
                                        replace = replace.replace(".class", "").replace(File.separator, ".");
                                    }
                                    return replace;
                                })
                                .forEach(resourcesPath::add);
                    }
                    continue;
                }
                GraalvmUtil.appendFile(string);
                continue;
            }

            try (InputStream inputStream = Files.newInputStream(start);
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                ZipEntry nextEntry = null;
                while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                    /* todo 读取的资源字节可以做解密操作 */
                    resourcesPath.add(nextEntry.getName());
                }
            }
        }
        Collections.sort(resourcesPath);
        return resourcesPath;
    }

    public static class Tuple<F, S> {

        public final F f;
        public final S s;

        public Tuple(F f, S s) {
            this.f = f;
            this.s = s;
        }

    }

}
