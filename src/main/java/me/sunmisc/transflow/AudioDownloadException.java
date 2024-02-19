package me.sunmisc.transflow;

public class AudioDownloadException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = -8456346387135621372L;

    private final Audio audio;


    public AudioDownloadException(Audio audio, Exception source) {
        super(source);
        this.audio = audio;
    }

    public Audio audio() {
        return audio;
    }
}
