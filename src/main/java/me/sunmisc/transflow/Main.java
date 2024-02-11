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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;
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

        out.println("size estimate: " + size);

        Set<CharSequence> skipped = ConcurrentHashMap.newKeySet();

        Download<Audio> download = new VDownload(to);
        final AtomicInteger progress = new AtomicInteger();
        final long start = System.currentTimeMillis();
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

        long end = System.currentTimeMillis() - start;

        LocalTime time = LocalTime.MIN
                .plusSeconds(TimeUnit.MILLISECONDS.toSeconds(end));

        out.println(
                new FormattedText("""
                        
                        elapsed time: %s
                        skipped: %s
                        %s
                        """,
                        DateTimeFormatter.ISO_TIME.format(time),
                        skipped.size(),
                        new ConcatText(skipped, "\n")
                )
        );
    }
    private static void progressBar(double currentProgress) {
        out.print(new ProgressBarText(currentProgress));
        out.flush();
    }
}
