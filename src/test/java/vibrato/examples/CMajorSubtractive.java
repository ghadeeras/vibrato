package vibrato.examples;

import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.RandomSource;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MainOscillator;

import javax.sound.sampled.AudioFormat;
import java.util.stream.DoubleStream;

import static vibrato.dspunits.filters.iir.SecondOrderFilter.hpf;

public class CMajorSubtractive extends DspApp {

    public CMajorSubtractive(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var scaleFrequencies = WaveTable.create(DoubleStream.of(
            261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25,
            523.25, 493.88, 440.00, 392.00, 349.23, 329.63, 293.66, 261.63
        ).map(freq -> freq * zHertz).toArray());

        var vibrato = scalarConstant(6 * zHertz)
            .through(WaveOscillator.create(sineLikeWave));

        var frequencyController = hpf(25, 0.48, 0.01)
            .variableDelay(Interpolator.cubic);
        var instrument = from(RandomSource.uniform(null))
            .throughInputOf(frequencyController);

        var audioSink = AudioSink.create(audioFormat);

        scalarConstant(2 * zHertz / scaleFrequencies.size())
            .through(WaveOscillator.create(scaleFrequencies, Interpolator.truncating))
            .through(amplitudeModulation(0.03), vibrato)
            .through(scalarDivisionOf(1))
            .through(instrument)
            .into(audioSink);
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 1, true, false);
        var cMajor = new CMajorSubtractive(audioFormat);
        var oscillator = MainOscillator.create();
        cMajor.connectTo(oscillator);

        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
