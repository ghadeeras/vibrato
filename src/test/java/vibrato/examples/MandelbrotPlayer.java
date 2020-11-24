package vibrato.examples;

import vibrato.dspunits.filters.MandelbrotFilter;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioFormat;
import java.util.stream.DoubleStream;

public class MandelbrotPlayer extends DspApp {

    public MandelbrotPlayer(float clockSpeed) {
        super(clockSpeed);

        var xs = WaveTable.create(DoubleStream.of(
            0.308896040, // 0.307267833, //
            0.329411451, // 0.326806319, //
            0.359044821, // 0.352206351, //
            0.388678192, // 0.372721762, //
            0.388678192, // 0.372721762, //
            0.359044821, // 0.352206351, //
            0.329411451, // 0.326806319, //
            0.308896040  // 0.307267833  //
        ).toArray());

        var ys = WaveTable.create(DoubleStream.of(
            0.0319708403, // 0.0345759718, //
            0.0544400993, // 0.0576965137, //
            0.1016581080, // 0.1052401630, //
            0.2166095340, // 0.2175864590, //
            0.2166095340, // 0.2175864590, //
            0.1016581080, // 0.1052401630, //
            0.0544400993, // 0.0576965137, //
            0.0319708403  // 0.0345759718  //
        ).toArray());

        var tempoSource = scalarConstant(2 * zHertz / xs.size());

        var source = join(
            tempoSource.through(WaveOscillator.create(xs, Interpolator.truncating)),
            tempoSource.through(WaveOscillator.create(ys, Interpolator.truncating))
        );
        var lpf = SecondOrderFilter.lpf(1, 2000 * zHertz, 0.5);
        var audioSink = AudioSink.create(new AudioFormat(clockSpeed, 16, 2, true, false));

        source
            .through(MandelbrotFilter.create())
            .through(replicate(lpf))
            .into(audioSink);
    }

    public static void main(String[] args) {
        float clockSpeed = 9450;
        MasterOscillator oscillator = MasterOscillator.create();
        MandelbrotPlayer system = new MandelbrotPlayer(clockSpeed);
        system.connectTo(oscillator);

        loop(oscillator);
    }

}
