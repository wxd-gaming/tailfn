package wxdgaming.tailfn;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class GraalvmUtil {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        return readFile(file);
    }

    public static String readFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = readInputStream(fileInputStream);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

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
            Path path = Paths.get("target/logs/%s-%s.log".formatted(ViewConfig.viewPath.getFileName(), formatter.format(new Date())));
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

    public static List<Class<?>> jarClasses(String... packageNames) throws Exception {
        List<String> strings = jarResources();
        List<Class<?>> classes = new ArrayList<>();
        Predicate<String> predicate = string -> {
            for (String packageName : packageNames) {
                if (string.startsWith(packageName)) {
                    return true;
                }
            }
            return false;
        };
        for (String string : strings) {
            if (string.endsWith(".class")) {
                String substring = string.substring(0, string.length() - 6);
                String replace = substring.replace('/', '.');
                replace = replace.replace('\\', '.');
                try {
                    if (predicate.test(replace)) {
                        Class<?> aClass = GraalvmUtil.class.getClassLoader().loadClass(replace);
                        classes.add(aClass);
                    }
                } catch (Throwable ignored) {}
            }
        }
        return classes;
    }

    public static List<String> jarResources() throws Exception {
        List<String> resourcesPath = new ArrayList<>();
        String x = javaClassPath();
        String[] split = x.split(File.pathSeparator);
        List<String> collect = Arrays.stream(split).sorted().toList();
        for (String string : collect) {
            // System.out.println(string);
            Path start = Paths.get(string);
            if (!string.endsWith(".jar") && !string.endsWith(".war") && !string.endsWith(".zip")) {
                if (string.endsWith("classes")) {
                    try (Stream<Path> stream = Files.walk(start)) {
                        String target = start.toString();
                        stream
                                .map(Path::toString)
                                .filter(s -> s.startsWith(target) && s.length() > target.length())
                                .map(s -> s.substring(target.length() + 1))
                                .forEach(resourcesPath::add);
                    }
                    continue;
                }
                continue;
            }

            try (JarFile jarFile = new JarFile(string)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    resourcesPath.add(entryName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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

    /***
     * 执行本地命令
     * @param batFile 本地bat文件路径
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-03-01 11:05
     */
    public static void execLocalCommand(boolean async, File workDir, String batFile) {
        try {
            // 构建包含 start 命令的命令数组
            String[] command;
            if (async) {
                command = new String[]{"cmd.exe", "/c", "start", "cmd.exe", "/c", batFile};
            } else {
                command = new String[]{"cmd.exe", "/c", batFile};
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workDir);
            // 设置属性子进程的I/O源或目标将与当前进程的相同,两者相互独立
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            // 执行命令进程
            Process start = pb.start();
            start.waitFor();
            start.destroy();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            appendFile(e.toString());
        }
    }

}
