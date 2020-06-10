package vibrato.examples;

import vibrato.dspunits.filters.WaveOscillator;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.MasterOscillator;
import vibrato.vectors.WaveTable;

import javax.sound.sampled.AudioFormat;
import java.util.Random;
import java.util.stream.DoubleStream;

public class CMajor extends DspApp {

    public CMajor(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());
        var audioSink = AudioSink.of(audioFormat);

        double[] scaleFrequencies = DoubleStream.of(
            261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25,
            523.25, 493.88, 440.00, 392.00, 349.23, 329.63, 293.66, 261.63
        ).map(freq -> freq * zHertz).toArray();
        var scalePlayer = WaveOscillator.from(new WaveTable.Simple(scaleFrequencies), Interpolator.truncating);

        double[] wave = randomSamples(2048);
        var notePlayer = WaveOscillator.from(new WaveTable.AntiAliased(WaveTable.dynamicRange(-0.5, +0.5, wave)));

        var tempo = constant(4 * zHertz / scaleFrequencies.length);

        tempo.then(scalePlayer).then(notePlayer).then(audioSink);
    }

    private double[] randomSamples(int count) {
        return new Random(getClass().getSimpleName().hashCode()).doubles(count).toArray();
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 1, true, false);
        var cMajor = new CMajor(audioFormat);
        var oscillator = new MasterOscillator(audioFormat.getFrameRate());
        cMajor.connectTo(oscillator);

        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
