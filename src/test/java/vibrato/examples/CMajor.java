package vibrato.examples;

import vibrato.dspunits.sinks.AudioSink;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioFormat;
import java.util.stream.DoubleStream;

public class CMajor extends DspApp {

    public CMajor(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var scaleFrequencies = WaveTable.create(DoubleStream.of(
            261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25,
            523.25, 493.88, 440.00, 392.00, 349.23, 329.63, 293.66, 261.63
        ).map(freq -> freq * zHertz).toArray());
        var instrumentWave = WaveTable.create(randomSamples(-0.5, +0.5, 32))
            .antiAliased()
            .withCachedInterpolation(Interpolator.cubic);

        var vibratoPlayer = WaveOscillator.from(sineLikeWave);
        var notePlayer = WaveOscillator.from(scaleFrequencies, Interpolator.truncating);
        var instrument = WaveOscillator.from(instrumentWave);

        var vibratoFreqSource = scalarConstant(6 * zHertz);
        var tempoSource = scalarConstant(2 * zHertz / scaleFrequencies.size());
        var audioSink = AudioSink.of(audioFormat);

        var vibrato = vibratoFreqSource
            .through(vibratoPlayer);
        tempoSource
            .through(notePlayer)
            .through(amplitudeModulation(0.03), vibrato)
            .through(instrument)
            .into(audioSink);
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 1, true, false);
        var cMajor = new CMajor(audioFormat);
        var oscillator = new MasterOscillator(audioFormat.getFrameRate());
        cMajor.connectTo(oscillator);

        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
