package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Author;
import me.sunmisc.transflow.Download;
import me.sunmisc.transflow.text.FilenameNormalized;
import org.bytedeco.javacpp.Loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FfmpegDownload implements Download<Audio> {

    private static final String FFMPEG =
            Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

    private final Path path;

    public FfmpegDownload(Path path) {
        this.path = path;
    }

    @Override
    public void download(Audio input) throws Exception {
        String url = Objects.requireNonNull(
                input.properties().get("url"));
        String name = new FilenameNormalized(
                input.name()
        ).toString();
        Path to = path.resolve(name + ".mp3");
        if (Files.exists(to)) return;

        ProcessBuilder pb = new ProcessBuilder(FFMPEG,
                "-y", "-i", url, "-dn",
                "-loglevel", "error",
                "-write_id3v2", "1",
                "-metadata", "artist=" + input
                .authors()
                .map(Author::name)
                .collect(Collectors.joining(", ")),
                "-metadata", "title=" + input.name(),
                "-metadata", "album=" + input.properties()
                .getOrDefault("album", "Unknown"),
                "-c", "copy", to.toString());
        pb.inheritIO().start().waitFor();

    }
}
