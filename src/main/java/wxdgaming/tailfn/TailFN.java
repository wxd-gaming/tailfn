package wxdgaming.tailfn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 实现了文件监听 类似 tail -300f 命令
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-27 15:47
 */
public class TailFN extends Thread {

    private final Path filePath;
    private int n;
    private final Consumer<String> consumer;

    public TailFN(String filePath, int n, Consumer<String> consumer) {
        this(Paths.get(filePath), n, consumer);
    }

    public TailFN(Path filePath, int n, Consumer<String> consumer) {
        super("listening" + filePath.toAbsolutePath());
        this.filePath = filePath.toAbsolutePath();
        this.n = n;
        this.consumer = consumer;
        setDaemon(true);
        start();
    }


    @Override public void run() {
        listening();
    }

    long skipped(RandomAccessFile file) throws Exception {
        // 记录文件的末尾处
        long filePointer = file.length();
        while (n > 0 && filePointer > 0) {
            filePointer--;
            /*把指针移动到上次读取的位置*/
            file.seek(filePointer);
            int read = file.read();
            if (read != -1) {
                if ((char) read == '\n') {
                    n--;
                }
            }
        }
        return filePointer;
    }

    protected void listening() {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r");
             WatchService watcher = FileSystems.getDefault().newWatchService()) {
            System.out.println("Listening for changes to: " + filePath);

            FileChannel channel = file.getChannel();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Channels.newInputStream(channel), StandardCharsets.UTF_8));

            /*当指定最后的行数的时候需要跳转到对应的位置*/
            long filePointer = skipped(file);
            readLastLine(bufferedReader);

            // 针对文件所在的文件夹注册监听事件
            Path dir = filePath.getParent();
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);


            while (!Thread.interrupted()) {
                try {
                    WatchKey key = watcher.poll(5, TimeUnit.SECONDS);
                    if (key == null) continue;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path changedFilePath = dir.resolve(pathEvent.context());

                        // 判定是我们需要监听的文件
                        if (changedFilePath.equals(filePath)) {
                            /*把指针移动到上次读取的位置*/
                            file.seek(filePointer);

                            readLastLine(bufferedReader);
                            // 将文件指针更新为当前文件大小，跳到当前末尾处
                            filePointer = file.getFilePointer();
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        System.out.println("Listening for changes to: " + filePath + " stopped.");
    }

    void readLastLine(BufferedReader bufferedReader) throws IOException {
        // 使用 Channels 和 CharsetDecoder 来读取 UTF-8 编码的内容
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            consumer.accept(line);
        }

    }

}
