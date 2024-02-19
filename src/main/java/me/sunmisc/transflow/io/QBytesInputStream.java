package me.sunmisc.transflow.io;

import java.io.InputStream;
import java.util.Objects;

public class QBytesInputStream extends InputStream {

    private final byte[] bytes;
    private int pos;

    public QBytesInputStream(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public int read() {
        return pos < bytes.length
                ? Byte.toUnsignedInt(bytes[pos++])
                : -1;
    }
    @Override
    public int read(byte[] b, int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        int count = bytes.length;
        if (pos >= count) return -1;
        int avail = count - pos;
        if (len > avail)
            len = avail;
        if (len <= 0)
            return 0;
        System.arraycopy(bytes, pos, b, off, len);
        pos += len;
        return len;
    }
}
