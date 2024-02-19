package me.sunmisc.transflow.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class QBytesOutputStream extends OutputStream {
    private static final byte[] EMPTY = new byte[0];
    private byte[] buf = EMPTY;
    private int cursor;

    public QBytesOutputStream() {}

    public QBytesOutputStream(int size) {
        if (size < 0)
            throw new IllegalArgumentException("Negative initial size: " + size);
        this.buf = new byte[size];
    }

    private void ensureCapacity(int minCapacity) {
        byte[] bytes = buf;
        int oldCapacity = bytes.length;
        int minGrowth = minCapacity - oldCapacity;
        if (minGrowth > 0) {
            int prefLength = oldCapacity + Math.max(minGrowth, oldCapacity);
            buf = Arrays.copyOf(bytes, prefLength);
        }
    }

    @Override
    public void write(int b) {
        int c = cursor, q = c + 1;
        ensureCapacity(q);
        buf[c] = (byte)b;
        cursor = q;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        int c = cursor, q = c + len;
        ensureCapacity(q);
        System.arraycopy(b, off, buf, c, len);
        cursor = q;
    }

    public void writeBytes(byte[] b) {
        write(b, 0, b.length);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, cursor);
    }

    @Override
    public String toString() {
        final byte[] bytes = buf;
        StringJoiner joiner = new StringJoiner(
                ", ",
                "[", "]");
        for (int i = 0, n = cursor; i < n; ++i)
            joiner.add(String.valueOf(bytes[i]));
        return joiner.toString();
    }
}
