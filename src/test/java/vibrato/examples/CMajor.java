package vibrato.examples;

import vibrato.dspunits.music.synthesis.generators.WaveOscillator;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.functions.Linear;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.MasterOscillator;
import vibrato.dspunits.music.synthesis.generators.WaveTable;

import javax.sound.sampled.AudioFormat;
import java.util.Random;
import java.util.stream.DoubleStream;

public class CMajor extends DspApp {

    public CMajor(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var scaleFrequencies = WaveTable.create(DoubleStream.of(
            261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25,
            523.25, 493.88, 440.00, 392.00, 349.23, 329.63, 293.66, 261.63
        ).map(freq -> freq * zHertz).toArray());
        var vibratoShape = WaveTable.create(1, 1.03, 1, 0.97)
            .withCachedInterpolation(Interpolator.cubic);
        var instrumentWave = WaveTable.create(dynamicRange(-0.5, +0.5, randomSamples(32)))
            .antiAliased()
            .withCachedInterpolation(Interpolator.cubic);

        var vibratoPlayer = WaveOscillator.from(vibratoShape);
        var notePlayer = WaveOscillator.from(scaleFrequencies, Interpolator.truncating);
        var instrument = WaveOscillator.from(instrumentWave);

        var vibratoFreqSource = scalarConstant(6 * zHertz);
        var tempoSource = scalarConstant(2 * zHertz / scaleFrequencies.size());
        var audioSink = AudioSink.of(audioFormat);

        var vibrato = vibratoFreqSource
            .through(vibratoPlayer);
        tempoSource
            .through(notePlayer)
            .through(scalarMultiplication, vibrato)
            .through(instrument)
            .into(audioSink);
    }

    private double[] randomSamples(int count) {
        return new Random(getClass().getSimpleName().hashCode()).doubles(count).toArray();
    }

    static double[] dynamicRange(double min, double max, double[] wave) {
        double mn = DoubleStream.of(wave).min().orElse(-1);
        double mx = DoubleStream.of(wave).max().orElse(+1);
        RealFunction f = Linear.linear(mn, min, mx, max);
        return DoubleStream.of(wave).map(f::apply).toArray();
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 1, true, false);
        var cMajor = new CMajor(audioFormat);
        var oscillator = new MasterOscillator(audioFormat.getFrameRate());
        cMajor.connectTo(oscillator);

        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
