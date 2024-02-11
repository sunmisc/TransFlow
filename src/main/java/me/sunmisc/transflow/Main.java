package me.sunmisc.transflow;

import me.sunmisc.transflow.text.ConcatText;
import me.sunmisc.transflow.text.FormattedText;
import me.sunmisc.transflow.text.ProgressBarText;
import me.sunmisc.transflow.vk.pipeline.VDownload;
import me.sunmisc.transflow.vk.pipeline.VPipeSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

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

        final long size = source.estimateSize();

        System.out.println("size estimate: " + size);

        System.out.println("loading playlists...");

        Set<CharSequence> skipped = ConcurrentHashMap.newKeySet();

        Download<Audio> download = new VDownload(to);
        final AtomicInteger progress = new AtomicInteger();
        do {
            try (final var scope = new StructuredTaskScope<>()) {
                sp.forEachRemaining(p -> scope.fork(() -> {
                    try {
                        download.download(p);
                    } catch (Exception e) {
                        skipped.add(
                                new FormattedText(
                                        "%s | exception: %s",
                                        new ConcatText("",
                                                p.name(), p.authors()
                                                .map(Author::name)
                                                .collect(joining(", "))
                                        ),
                                        e.getMessage()
                                )
                        );
                    } finally {
                        double q = ((double)
                                progress.incrementAndGet() / size) * 100;
                        progressBar(q);
                    }
                    return null;
                }));
                // we will process the batch before moving
                // on to the second one (the service may alarm)
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while ((sp = sp.trySplit()) != null);

        System.out.println("\nskipped: " + skipped.size());
        System.out.println(String.join("\n", skipped));
    }
    private static void progressBar(double currentProgress) {
        System.out.print(new ProgressBarText(currentProgress));
        System.out.flush();
    }
}
