package vibrato.dspunits.sinks;

import vibrato.dspunits.DspUnit;
import vibrato.utils.FixedPointSample;
import vibrato.vectors.RealVector;

import javax.sound.sampled.*;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

import static javax.sound.sampled.AudioFormat.Encoding;
import static vibrato.oscillators.Oscillator.Operation;
import static vibrato.oscillators.Oscillator.State;

public class AudioSink extends DspUnit implements State {

    private final AudioFormat format;
    private final RealVector channels;
    private final FixedPointSample sample;
    private final OutputStream stream;

    private final Consumption consumption = new Consumption();

    public AudioSink(RealVector channels, AudioFormat format) {
        this(channels, format, new AudioLineOutputStream(format));
    }

    public AudioSink(RealVector channels, AudioFormat format, OutputStream audioStream) {
        boolean signed = format.getEncoding().equals(Encoding.PCM_SIGNED);
        int bufferSize = Math.round(format.getFrameSize() * format.getFrameRate());
        this.format = format;
        this.stream = new BufferedOutputStream(audioStream, bufferSize);
        this.sample = new FixedPointSample(format.getSampleSizeInBits(), signed, format.isBigEndian());
        this.channels = channels;
    }

    @Override
    protected Operation[] operations() {
        return ops(consumption);
    }

    private class Consumption implements Operation {

        @Override
        public State state() {
            return AudioSink.this;
        }

        @Override
        public void readPhase() {
            for (int i = 0; i < format.getChannels(); i++) {
                sample.write(stream, channels.value(i));
            }
        }

        @Override
        public void writePhase() {
        }

    }

    public static class AudioLineOutputStream extends OutputStream {

        private final SourceDataLine line;

        public AudioLineOutputStream(AudioFormat format) {
            try {
                Line.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
                line = (SourceDataLine) AudioSystem.getLine(lineInfo);
                line.open(format);
                line.start();
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
        }

        public void write(int b) {
            write(new byte[] {(byte) b}, 0, 1);
        }

        public void write(byte[] b, int off, int len) {
            line.write(b, off, len);
        }

        public void flush() {
            line.drain();
        }

        public void close() {
            line.stop();
            line.close();
        }

    }

}
