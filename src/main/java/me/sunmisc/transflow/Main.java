package me.sunmisc.transflow;

import me.sunmisc.transflow.text.PercentBarText;
import me.sunmisc.transflow.vk.pipeline.FfmpegDownload;
import me.sunmisc.transflow.vk.pipeline.VPipeSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final PipeSource source;
    private final Path to;

    public Main(PipeSource source, Path path) {
        this.source = source;
        this.to = path;
    }

    public static void main(String[] args) {
        try (InputStream inputStream =
                     Main.class.getResourceAsStream(
                             "/config.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                String token = properties.getProperty("access_token");
                int id = Integer.parseInt(properties.getProperty("playlist_id"));
                Path path = Path.of(properties.getProperty("save_files_to"));

                new Main(
                        new VPipeSource(
                                httpClient,
                                token, id
                        ), path
                ).download();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void download() {
        Spliterator<Audio> sp = source;

        long size = source.estimateSize();

        System.out.println("size estimate: " + size);

        System.out.println("loading playlists...");
        // You can use FJP with asyncMode = true
        // Then we have a small guarantee on FIFO
        // therefore, it is better to call invokeAll not in the reverse order
        // (as for LIFO), but in the same order, but can be optimized a little
        try (ExecutorService executor =
                     // maybe fiber, but deep stack...
                     Executors.newVirtualThreadPerTaskExecutor()) {
            Download<Audio> download = new FfmpegDownload(to);
            Queue<Future<?>> batch = new LinkedList<>();

            final AtomicInteger progress = new AtomicInteger();
            do {
                sp.forEachRemaining(p -> batch.add(
                        executor.submit(() -> {
                            try {
                                download.download(p);
                            } catch (Exception ignored) {
                            } finally {
                                double q = ((double)
                                        progress.getAndIncrement() / size) * 100;
                                progressBar(q);
                            }
                        }))
                );
                // waiting for batch to load
                // clearing rather than creating a new queue to help GC
                Future<?> f;
                while ((f = batch.poll()) != null)
                    f.get(); // wait
            } while ((sp = sp.trySplit()) != null);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    private static void progressBar(double currentProgress) {
        System.out.print(new PercentBarText(currentProgress));
        System.out.flush();
    }
}
