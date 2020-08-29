package vibrato.examples;

import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.dspunits.filters.fir.VariableDelay;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.functions.Linear;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.MasterOscillator;
import vibrato.music.synthesis.generators.WaveTable;

import javax.sound.sampled.AudioFormat;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Doppler extends DspApp {

    protected Doppler(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var sound = WaveTable.create(randomSamples(-0.5, +0.5).limit(16).toArray()).withCachedInterpolation(Interpolator.cubic);
        var audioSource = scalarConstant(220 * zHertz).through(WaveOscillator.from(sound));

        var xs = WaveTable.create(8, 0, -8, 0).withCachedInterpolation(Interpolator.cubic);
        var ys = WaveTable.create(0, 1, 0, -1).withCachedInterpolation(Interpolator.cubic);
        var audioSourceRPS = scalarConstant(zHertz / 15);
        var audioSourcePos = join(
            audioSourceRPS.through(WaveOscillator.from(xs)),
            audioSourceRPS.through(WaveOscillator.from(ys))
        );

        var audioSourceDistanceSquared = audioSourcePos.through(vectorLengthSquared);
        var audioSourceDistance = audioSourceDistanceSquared.through(scalarSquareRoot);
        var audioSourceDirection = audioSourcePos.through(vectorDivision, audioSourceDistance);

        var delay = VariableDelay.ofMax(100 * zMeters, Interpolator.linear);
        var perceivedAudio = audioSource
            .through(delay, audioSourceDistance.through(scalarMultiplication(10 * zMeters)))
            .through(scalarDivision, audioSourceDistanceSquared);

        var audioSink = AudioSink.of(audioFormat);

        var earAngle = Math.PI / 6;
        var cos = Math.cos(earAngle);
        var sin = Math.sin(earAngle);
        var adapter = scalarFunction(Linear.linear(0.4, 0.6));
        var leftChannelWeight = audioSourceDirection.through(dotProduct(-cos, sin)).through(adapter);
        var rightChannelWeight = audioSourceDirection.through(dotProduct(+cos, sin)).through(adapter);

        join(
            perceivedAudio.through(scalarMultiplication, leftChannelWeight),
            perceivedAudio.through(scalarMultiplication, rightChannelWeight)
        ).into(audioSink);
    }

    private DoubleStream randomSamples(double min, double max) {
        return new Random(getClass().getSimpleName().hashCode()).doubles(min, max);
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 2, true, false);

        var clockSpeed = audioFormat.getFrameRate();
        var oscillator = new MasterOscillator(clockSpeed);
        var system = new Doppler(audioFormat);
        system.connectTo(oscillator);

        loop(oscillator);
    }

}
