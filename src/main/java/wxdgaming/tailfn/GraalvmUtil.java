package wxdgaming.tailfn;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraalvmUtil {

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

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
            Files.write(
                    path,
                    content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void appendFile(String content) {
        try {
            Path path = Paths.get(String.format("target/logs/%s-%s.log", ViewConfig.viewPath.getFileName(), formatter.format(new Date())));
            try {
                path.toAbsolutePath().toFile().getParentFile().mkdirs();
            } catch (Exception ignored) {}
            Files.write(
                    path,
                    String.format("[%s] - %s\n", formatter2.format(new Date()), content).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

}
