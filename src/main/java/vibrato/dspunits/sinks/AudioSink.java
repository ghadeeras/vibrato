package vibrato.dspunits.sinks;

import vibrato.dspunits.DspSink;
import vibrato.dspunits.DspUnit;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.utils.FixedPointSample;
import vibrato.vectors.RealVector;

import javax.sound.sampled.*;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

import static javax.sound.sampled.AudioFormat.Encoding;

public class AudioSink implements DspUnit, State {

    private final RealVector input;
    private final AudioFormat format;
    private final FixedPointSample sample;
    private final OutputStream stream;

    private final Consumption consumption = new Consumption();

    private AudioSink(RealVector input, AudioFormat format, OutputStream audioStream) {
        boolean signed = format.getEncoding().equals(Encoding.PCM_SIGNED);
        int bufferSize = format.getFrameSize() * Math.round(format.getFrameRate() / 15);
        this.input = input;
        this.format = format;
        this.stream = new BufferedOutputStream(audioStream, bufferSize);
        this.sample = new FixedPointSample(format.getSampleSizeInBits(), signed, format.isBigEndian());
    }

    private void outputFrame() {
        for (int i = 0; i < format.getChannels(); i++) {
            sample.write(stream, input.value(i));
        }
    }

    @Override
    public Operation[] operations() {
        return DspUnit.ops(consumption);
    }

    public static DspSink<RealVector> of(AudioFormat format) {
        return into(new AudioLineOutputStream(format), format);
    }

    public static DspSink<RealVector> into(OutputStream audioStream, AudioFormat format) {
        return input -> new AudioSink(input, format, audioStream);
    }

    private class Consumption implements Operation {

        @Override
        public State state() {
            return AudioSink.this;
        }

        @Override
        public void readPhase() {
            outputFrame();
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
