package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Download;
import org.bytedeco.javacpp.Loader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FfmpegDownload implements Download<Audio> {

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
        String name = input.name();
        Path to = path.resolve(name + ".mp3");
        if (Files.exists(to)) return;

        ProcessBuilder pb = new ProcessBuilder(FFMPEG,
                "-y", "-i", url, "-dn",
                "-loglevel", "error",
                "-write_id3v2", "1",
                "-metadata", "artist=" + input.author(),
                "-metadata", "title=" + input.name(),
                "-metadata", "album=" + input.properties()
                .getOrDefault("album", "Unknown"),
                "-c", "copy", to.toString());
        pb.inheritIO().start().waitFor();

    }
}
