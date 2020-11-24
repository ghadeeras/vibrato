package vibrato.examples;

import vibrato.dspunits.sinks.AudioSink;
import vibrato.effects.OmniSourceMover;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioFormat;

public class Doppler extends DspApp {

    protected Doppler(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var sound = WaveTable.create(randomSamples(-0.5, +0.5, 16)).withCachedInterpolation(Interpolator.cubic);
        var audioSource = scalarConstant(220 * zHertz).through(WaveOscillator.create(sound));

        var xs = WaveTable.create(80 * zMeters, 0, -80 * zMeters, 0).withCachedInterpolation(Interpolator.cubic);
        var ys = WaveTable.create(0, 10 * zMeters, 0, -10 * zMeters).withCachedInterpolation(Interpolator.cubic);
        var audioSourceRPS = scalarConstant(zHertz / 15);
        var audioSourcePos = join(
            audioSourceRPS.through(WaveOscillator.create(xs)),
            audioSourceRPS.through(WaveOscillator.create(ys))
        );

        var mover = OmniSourceMover.create(10 * zMeters, 100 * zMeters);

        var audioSink = AudioSink.create(audioFormat);

        audioSource.through(mover, audioSourcePos).into(audioSink);
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 2, true, false);

        var oscillator = MasterOscillator.create();
        var system = new Doppler(audioFormat);
        system.connectTo(oscillator);

        loop(oscillator);
    }

}
