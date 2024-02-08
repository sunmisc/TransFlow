import org.bytedeco.javacpp.Loader;

public class Kek {

    private static final String FFMPEG =
            Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    public static void main(String[] args) {
        System.out.println(FFMPEG);
    }
}
