package me.sunmisc.transflow.vk.pipe;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Download;
import org.bytedeco.javacpp.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VDownload implements Download<Audio> {

    private static final String FFMPEG =
            Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);


    private final Path path;

    public VDownload(Path path) {
        this.path = path;
    }

    @Override
    public void download(Audio input) throws Exception {
        input.stream().ifPresent(bytes -> {
            try {
                String name = input.name();
                Path to = path.resolve(name + ".mp3");
                if (Files.exists(to)) return;
                Path tempFile = Files.createTempFile(path, name, ".ts");
                Files.write(tempFile, bytes);

                ProcessBuilder pb = new ProcessBuilder(
                        FFMPEG, "-y", "-i",
                        tempFile.toString(),
                        "-map", "0", "-dn",
                        "-loglevel", "error",
                        "-hide_banner",
                        "-write_id3v2", "1",
                        "-c", "copy", to.toString());

                // non-blocking
                pb.inheritIO()
                        .start()
                        .toHandle()
                        .onExit()
                        .whenComplete((r,t) -> {
                            try {
                                Files.deleteIfExists(tempFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
