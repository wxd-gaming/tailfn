package wxdgaming.tailfn;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 实现了文件监听 类似 tail -300f 命令
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 15:47
 */
public class TailFN {

    private final Path filePath;
    private int n;
    private final Consumer<String> consumer;
    private final AtomicLong filePointer = new AtomicLong();

    FileAlterationMonitor monitor = null;

    public TailFN(String filePath, int n, Consumer<String> consumer) {
        this(Paths.get(filePath), n, consumer);
    }

    public TailFN(Path filePath, int n, Consumer<String> consumer) {
        this.filePath = filePath.toAbsolutePath();
        this.n = n;
        this.consumer = consumer;
        try {
            skipped();
            readLastLine();
            long pollingInterval = 1000; // Poll every second
            FileAlterationObserver fileAlterationObserver = FileAlterationObserver.builder().setFile(this.filePath.toFile().getParentFile()).get();
            monitor = new FileAlterationMonitor(pollingInterval);
            FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    try {
                        // 判定是我们需要监听的文件
                        if (filePath.getFileName().toString().equalsIgnoreCase(file.getName())) {
                            readLastLine();
                        }
                    } catch (IOException e) {
                        GraalvmUtil.appendFile(e.toString());
                    }
                }
            };

            fileAlterationObserver.addListener(listener);
            monitor.addObserver(fileAlterationObserver);
            monitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            GraalvmUtil.appendFile(e.toString());
        }
    }

    void skipped() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
            // 记录文件的末尾处
            long findFilePointer = file.length();
            while (n > 0 && findFilePointer > 0) {
                findFilePointer--;
                /*把指针移动到上次读取的位置*/
                file.seek(findFilePointer);
                int read = file.read();
                if (read != -1) {
                    if ((char) read == '\n') {
                        n--;
                    }
                }
            }
            filePointer.set(findFilePointer);
        }
    }

    void readLastLine() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
            /*把指针移动到上次读取的位置*/
            if (file.length() > filePointer.get()) {
                file.seek(filePointer.get());
            }
            try (FileChannel channel = file.getChannel();
                 InputStream inputStream = Channels.newInputStream(channel);
                 InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);) {

                try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                    // 使用 Channels 和 CharsetDecoder 来读取 UTF-8 编码的内容
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        consumer.accept(line);
                    }
                    // 将文件指针更新为当前文件大小，跳到当前末尾处
                    filePointer.set(file.getFilePointer());
                }

            }
        }
    }

}
