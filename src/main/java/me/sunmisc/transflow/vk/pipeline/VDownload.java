package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Download;
import me.sunmisc.transflow.io.QBytesOutputStream;
import me.sunmisc.transflow.text.FilenameNormalized;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VDownload implements Download<Audio> {
    private static final VarHandle AA = MethodHandles
            .byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);
    private static final int PACKET_SIZE = 188, PID = 256;

    private final Path path;

    public VDownload(Path path) {
        this.path = path;
    }

    @Override
    public void download(Audio input) throws Exception {
        String name = new FilenameNormalized(
                input.name()
        ).toString();
        Path to = path.resolve(name + ".mp3");

        if (Files.exists(to)) return;

        try (InputStream is = input.stream();
             OutputStream os = Files.newOutputStream(to);
             QBytesOutputStream buff = new QBytesOutputStream()) {

            byte[] bytes = is.readAllBytes();

            for (int i = 0, n = bytes.length; i < n; i += PACKET_SIZE) {
                int p = i;
                byte syncByte = bytes[p++];
                if (syncByte != 0x47)
                    throw new RuntimeException(
                            "Invalid sync byte: 0x" + Integer.toHexString(syncByte));
                byte    secondByte = bytes[p++],
                        thirdByte  = bytes[p++],
                        fourthByte = bytes[p++];

                int transportErrorIndicator = secondByte & 0x80;

                if (transportErrorIndicator != 0)
                    throw new RuntimeException("Transport error indicator is set");

                int payloadUnitStartIndicator = (secondByte & 0x40) >> 6;

                int pid = ((secondByte & 0x1F) << 8) | (thirdByte & 0xFF);

                int adaptationFieldControl = (fourthByte & 0x30) >> 4;

                if (adaptationFieldControl == 2 || adaptationFieldControl == 3) {
                    int adaptationFieldLength = Byte.toUnsignedInt(bytes[p++]);
                    p += adaptationFieldLength;
                }

                if (pid == PID) {
                    if (payloadUnitStartIndicator == 1) {
                        p += 4;
                        int len = Short.toUnsignedInt((short) AA.get(bytes, p));
                        p += Short.BYTES;
                        if (len != 0) {
                            p += 2;
                            int pesHeaderDataLength = Byte.toUnsignedInt(bytes[p++]);
                            p += pesHeaderDataLength;
                        }
                    }
                    int tail = p - i;
                    buff.write(bytes, p, PACKET_SIZE - tail);
                }
            }
            buff.writeTo(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
