package vibrato.dspunits.sources;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.utils.FixedPointSample;
import vibrato.vectors.RealVector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

import static javax.sound.sampled.AudioFormat.Encoding;

public class AudioSource implements State {

    private final InputStream stream;
    private final FixedPointSample sample;
    private final double[] frame;

    private final Channels channels = new Channels();
    private final Generation generation = new Generation();

    private AudioSource(InputStream audioStream, AudioFormat format) {
        stream = new BufferedInputStream(audioStream, bufferSize(format));
        sample = new FixedPointSample(format.getSampleSizeInBits(), isSignedPCM(format), format.isBigEndian());
        frame = new double[format.getChannels()];
        loadNextFrame();
    }

    private int bufferSize(AudioFormat format) {
        return Math.round(format.getFrameSize() * format.getFrameRate());
    }

    private boolean isSignedPCM(AudioFormat format) {
        Encoding encoding = format.getEncoding();
        if (Encoding.PCM_SIGNED.equals(encoding)) {
            return true;
        } else if (Encoding.PCM_UNSIGNED.equals(encoding)) {
            return false;
        } else {
            throw new RuntimeException("Unsupported encoding: " + encoding);
        }
    }

    private void loadNextFrame() {
        for (int i = 0; i < frame.length; i++) {
            frame[i] = sample.read(stream);
        }
    }

    public static DspSource<RealVector> create(AudioInputStream audioStream) {
        return create(audioStream, audioStream.getFormat());
    }

    public static DspSource<RealVector> create(InputStream audioStream, AudioFormat format) {
        AudioSource source = new AudioSource(audioStream, format);
        return DspSource.create(source.channels, source.generation);
    }

    private double channel(int index) {
        return 0 <= index && index < frame.length ? frame[index] : 0;
    }

    private class Channels implements RealVector {

        @Override
        public int size() {
            return frame.length;
        }

        @Override
        public double value(int index) {
            return channel(index);
        }

    }

    private class Generation implements Operation {

        @Override
        public State state() {
            return AudioSource.this;
        }

        @Override
        public void readPhase() {
        }

        @Override
        public void writePhase() {
            loadNextFrame();
        }

    }

}
