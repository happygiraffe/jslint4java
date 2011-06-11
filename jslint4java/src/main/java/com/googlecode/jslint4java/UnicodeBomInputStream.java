package com.googlecode.jslint4java;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * The <code>UnicodeBOMInputStream</code> class wraps any
 * <code>InputStream</code> and detects the presence of any Unicode BOM (Byte
 * Order Mark) at its beginning, as defined by <a
 * href="http://www.faqs.org/rfcs/rfc3629.html">RFC 3629 - UTF-8, a
 * transformation format of ISO 10646</a>
 *
 * <p>
 * Use the {@link #getBOM()} method to know whether a BOM has been detected or
 * not.
 * </p>
 * <p>
 * Use the {@link #skipBOM()} method to remove the detected BOM from the wrapped
 * <code>InputStream</code> object.
 * </p>
 *
 * @see {@code http://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java/1835529#1835529}
 */
public class UnicodeBomInputStream extends InputStream {

    public static enum BOM {
        NONE(new byte[] {}, "NONE"),
        /** UTF-8 BOM */
        UTF_8(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }, "UTF-8"),
        /** UTF-16 little endian BOM */
        UTF_16_LE(new byte[] { (byte) 0xFF, (byte) 0xFE }, "UTF-16 little-endian"),
        /** UTF-16 big endian BOM */
        UTF_16_BE(new byte[] { (byte) 0xFE, (byte) 0xFF }, "UTF-16 big-endian"),
        /** UTF-32 little endian BOM */
        UTF_32_LE(new byte[] { (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00 },
                "UTF-32 little-endian"),
        /** UTF-32 big endian BOM */
        UTF_32_BE(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF },
                "UTF-32 big-endian");

        private BOM(byte[] bytes, String description) {
            this.bytes = bytes;
            this.description = description;
        }

        private final byte[] bytes;
        private final String description;

        public byte[] getBytes() {
            final byte[] result = new byte[bytes.length];
            System.arraycopy(bytes, 0, result, 0, bytes.length);
            return result;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * Constructs a new <code>UnicodeBOMInputStream</code> that wraps the
     * specified <code>InputStream</code>.
     *
     * @param inputStream
     *            an <code>InputStream</code>.
     *
     * @throws NullPointerException
     *             when <code>inputStream</code> is <code>null</code>.
     * @throws IOException
     *             on reading from the specified <code>InputStream</code> when
     *             trying to detect the Unicode BOM.
     */
    public UnicodeBomInputStream(final InputStream inputStream) throws NullPointerException,
            IOException

    {
        if (inputStream == null) {
            throw new NullPointerException("invalid input stream: null is not allowed");
        }

        in = new PushbackInputStream(inputStream, 4);

        final byte bom[] = new byte[4];
        final int read = in.read(bom);

        switch (read) {
        case 4:
            if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE && bom[2] == (byte) 0x00
                    && bom[3] == (byte) 0x00) {
                this.bom = BOM.UTF_32_LE;
                break;
            } else if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00 && bom[2] == (byte) 0xFE
                    && bom[3] == (byte) 0xFF) {
                this.bom = BOM.UTF_32_BE;
                break;
            }

        case 3:
            if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
                this.bom = BOM.UTF_8;
                break;
            }

        case 2:
            if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE) {
                this.bom = BOM.UTF_16_LE;
                break;
            } else if (bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF) {
                this.bom = BOM.UTF_16_BE;
                break;
            }

        default:
            this.bom = BOM.NONE;
            break;
        }

        if (read > 0) {
            in.unread(bom, 0, read);
        }
    }

    /**
     * Returns the <code>BOM</code> that was detected in the wrapped
     * <code>InputStream</code> object.
     *
     * @return a <code>BOM</code> value.
     */
    public final BOM getBOM() {
        return bom;
    }

    /**
     * Skips the <code>BOM</code> that was found in the wrapped
     * <code>InputStream</code> object.
     *
     * @return this <code>UnicodeBOMInputStream</code>.
     *
     * @throws IOException
     *             when trying to skip the BOM from the wrapped
     *             <code>InputStream</code> object.
     */
    public final synchronized UnicodeBomInputStream skipBOM() throws IOException {
        if (!skipped) {
            skip(bom.bytes.length);
            skipped = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return in.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte b[]) throws IOException, NullPointerException {
        return in.read(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte b[], final int off, final int len) throws IOException,
            NullPointerException {
        return in.read(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long n) throws IOException {
        return in.skip(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        return in.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        in.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void mark(final int readlimit) {
        in.mark(readlimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    private final PushbackInputStream in;
    private final BOM bom;
    private boolean skipped = false;

}
