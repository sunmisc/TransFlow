package me.sunmisc.transflow;

import me.sunmisc.transflow.vk.pipeline.VDownload;

import java.nio.file.Path;
import java.util.Spliterator;
import java.util.concurrent.StructuredTaskScope;

public class DownloadSink implements Runnable {
    private final Fallback<Audio, AudioDownloadException> fallback;
    private final PipeSource source;
    private final Path to;

    public DownloadSink(PipeSource source, Path path) {
        this(new Fallback<>() {
            @Override public void success(Audio result) { }
            @Override public void exception(AudioDownloadException e) {}
        }, source, path);
    }

    public DownloadSink(Fallback<Audio, AudioDownloadException> fallback,
                        PipeSource source, Path path) {
        this.fallback = fallback;
        this.source = source;
        this.to = path;
    }

    @Override
    public void run() {
        Spliterator<Audio> sp = source;
        Download<Audio> download = new VDownload(to);
        do {
            try (StructuredTaskScope<Void> scope = new StructuredTaskScope<>()) {
                sp.forEachRemaining(p -> scope.fork(() -> {
                    try {
                        download.download(p);
                        fallback.success(p);
                    } catch (Exception e) {
                        fallback.exception(new AudioDownloadException(p, e));
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
    }
}
