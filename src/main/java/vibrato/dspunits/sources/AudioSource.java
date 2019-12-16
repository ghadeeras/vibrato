package vibrato.dspunits.sources;

import vibrato.dspunits.DspUnit;
import vibrato.utils.FixedPointSample;
import vibrato.vectors.RealVector;

import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import java.io.InputStream;

import static javax.sound.sampled.AudioFormat.Encoding;
import static vibrato.oscillators.Oscillator.Operation;
import static vibrato.oscillators.Oscillator.State;

public class AudioSource extends DspUnit implements RealVector, State {

    private final InputStream stream;
    private final FixedPointSample sample;
    private final double[] frame;

    private final Generation generation = new Generation();

    public AudioSource(AudioFormat format, InputStream audioStream) {
        stream = new BufferedInputStream(audioStream, bufferSize(format));
        sample = new FixedPointSample(format.getSampleSizeInBits(), isSignedPCM(format), format.isBigEndian());
        frame = new double[format.getChannels()];
        generation.writePhase();
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

    @Override
    protected Operation[] operations() {
        return ops(generation);
    }

    @Override
    public int size() {
        return frame.length;
    }

    @Override
    public double firstValue() {
        return value(0);
    }

    @Override
    public double lastValue() {
        return value(size() - 1);
    }

    @Override
    public double value(int index) {
        return 0 <= index && index < frame.length ? frame[index] : 0;
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
            for (int i = 0; i < size(); i++) {
                frame[i] = sample.read(stream);
            }
        }

    }

}
