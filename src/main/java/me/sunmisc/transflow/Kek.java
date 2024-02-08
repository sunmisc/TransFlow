package me.sunmisc.transflow;

import me.sunmisc.transflow.text.PercentBarText;
import me.sunmisc.transflow.vk.pipe.VDownload;
import me.sunmisc.transflow.vk.pipe.VPipeSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Spliterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Kek {

    private static final int PARALLELISM = Math.clamp(
            Runtime.getRuntime().availableProcessors() >>> 1,
            1, 8
    );
    private final PipeSource source;
    private final Path to;

    public Kek(PipeSource source, Path path) {
        this.source = source;
        this.to = path;
    }


    public static void main(String[] args) {
        try (InputStream inputStream =
                     Kek.class.getResourceAsStream(
                             "/config.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                String token = properties.getProperty("access_token");
                int id = Integer.parseInt(properties.getProperty("playlist_id"));
                Path path = Path.of(properties.getProperty("save_files_to"));

                new Kek(
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

        System.out.println("size estimate: "+size);
        try (ExecutorService executor =
                     // maybe fiber, but deep stack and native...
                     Executors.newWorkStealingPool(PARALLELISM)) {
            Download<Audio> download = new VDownload(to);
            int progress = 0;
            progressBar(0); // init
            do {
                List<Callable<Object>> batch = new LinkedList<>();

                sp.forEachRemaining(p -> batch.add(
                        Executors.callable(() -> {
                            try {
                                download.download(p);
                            } catch (Exception ignored) { }
                        }))
                );
                // waiting for batch to load
                for (Future<?> ignored : executor.invokeAll(batch)) {
                    double p = ((double) progress++ / size) * 100;
                    progressBar(p);
                }
            } while ((sp = sp.trySplit()) != null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void progressBar(double currentProgress) {
        System.out.print(new PercentBarText(currentProgress));
        System.out.flush();
    }
}
