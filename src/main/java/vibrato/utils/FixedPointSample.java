package vibrato.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class FixedPointSample {

    private byte[] bytes;

    private final int lsb;
    private final int msb;
    private final int step;

    private final long max;
    private final long bias;
    private final long byteMask;

    public final double roundingError;

    public FixedPointSample(int bitsPerSample, boolean signed, boolean bigEndian) {
        if (bitsPerSample % 8 != 0 || bitsPerSample > 48 || bitsPerSample < 8) {
            throw new UnsupportedOperationException("Unsupported BPS: " + bitsPerSample);
        }
        bytes = new byte[bitsPerSample / 8];
        max = (1L << (bitsPerSample - 1)) - 1;
        bias = signed ? 0L : max + 1;
        byteMask = signed ? -1L : 0xFFL;
        roundingError = 1D / (2 * max);
        if (bigEndian) {
            lsb = bytes.length - 1;
            msb = 0;
            step = -1;
        } else {
            lsb = 0;
            msb = bytes.length - 1;
            step = +1;
        }
    }

    public double read(InputStream stream) {
        return doRead(stream) == bytes.length ? toFloatingPoint() : 0;
    }

    public void write(OutputStream stream, double sample) {
        fromFloatingPoint(sample);
        doWrite(stream);
    }

    private int doRead(InputStream stream) {
        try {
            return stream.read(bytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void doWrite(OutputStream stream) {
        try {
            stream.write(bytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private double toFloatingPoint() {
        long result = bytes[msb]; // Only most significant byte has the sign bit.
        result &= byteMask;
        for (int i = msb - step; i != lsb - step; i -= step) {
            long b = bytes[i];
            result <<= 8;
            result |= b & 0xFF;
        }
        result -= bias;
        return (double) result / (double) max;
    }

    private void fromFloatingPoint(double sample) {
        long result = Math.round(max * sample);
        result += bias;
        for (int i = lsb; i != msb + step; i += step) {
            long b = result & byteMask;
            bytes[i] = (byte) b;
            result >>= 8;
        }
    }

    public String contentAsString() {
        return IntStream.range(0, bytes.length)
            .map(i -> bytes[i]).boxed()
            .map(b -> b & 0xFF)
            .map(Integer::toHexString)
            .map(String::toUpperCase)
            .collect(joining(" "));
    }

}
