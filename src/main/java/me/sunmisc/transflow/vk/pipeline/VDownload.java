package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Download;
import org.bytedeco.javacpp.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VDownload implements Download<Audio> {

    private static final String FFMPEG =
            Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);


    private final Path path;

    public VDownload(Path path) {
        this.path = path;
    }

    @Override
    public void download(Audio input) throws Exception {
        String name = input.name();
        Path to = path.resolve(name + ".mp3");
        if (Files.exists(to)) return;

        input.stream().ifPresent(bytes -> {
            try {
                Path tempFile = Files.createTempFile(path, name, ".ts");
                try {
                    Files.write(tempFile, bytes);
                    ProcessBuilder pb = new ProcessBuilder(
                            FFMPEG, "-y", "-i",
                            tempFile.toString(), "-dn",
                            "-loglevel", "quiet",
                            "-write_id3v2", "1",
                            "-metadata", "artist=" + input.author(),
                            "-metadata", "title=" + input.name(),
                            "-metadata", "album=" + input.properties()
                            .getOrDefault("album", "Unknown"),
                            "-c", "copy", to.toString());

                    pb.inheritIO().start().waitFor();
                } finally {
                    Files.deleteIfExists(tempFile);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
