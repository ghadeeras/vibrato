package vibrato.examples;

import vibrato.dspunits.sinks.AudioSink;
import vibrato.functions.Curve;
import vibrato.functions.Pulse;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.base.BasicInstrument;
import vibrato.music.synthesis.generators.WaveGenerator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioFormat;
import java.util.stream.DoubleStream;

public class CMajorEnveloped extends DspApp {

    public CMajorEnveloped(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());

        var scaleFrequencies = WaveTable.create(DoubleStream.of(
            261.64, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.24,
            523.26, 493.88, 440.00, 392.00, 349.23, 329.63, 293.66, 261.62
        ).map(freq -> freq * zHertz).toArray());
        var instrumentWave = WaveTable.create(randomSamples(-0.5, +0.5, 16))
            .antiAliased()
            .withCachedInterpolation(Interpolator.cubic);
        var attackEnvelope = Curve
            .from(0 * zSecond, 0, 0)
            .to(0.03 * zSecond, 0.95, 0)
            .to(0.06 * zSecond, 0.45, 0)
            .to(0.09 * zSecond, 0.75, 0)
            .slopedAs(Curve.envelope)
            .create(Curve.smooth)
            .asSignal();
        var muteEnvelope = Curve
            .from(0.00 * zSecond, 1)
            .to(0.07 * zSecond, 0.25)
            .to(0.20 * zSecond, 0)
            .slopedAs(Curve.envelope)
            .create(Curve.smooth)
            .asSignal();

        var vibratoPlayer = WaveOscillator.create(sineLikeWave);
        var notePlayer = WaveOscillator.create(scaleFrequencies, Interpolator.truncating);
        var instrument = BasicInstrument.create(instrumentWave, attackEnvelope, muteEnvelope);

        var vibratoSource = scalarConstant(6 * zHertz)
            .through(vibratoPlayer);
        var noteFrequencySource = scalarConstant(2 * zHertz / scaleFrequencies.size())
            .through(notePlayer);
        var excitationResetSource = noteFrequencySource
            .through(diff);
        var excitationSource = scalarConstant(1)
            .through(WaveGenerator.create(Pulse.pulse(0.3 * zSecond, 0, 2, -1)), excitationResetSource);
        noteFrequencySource
            .through(amplitudeModulation(0.03), vibratoSource)
            .through(instrument, excitationSource)
            .into(AudioSink.create(audioFormat));
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 1, true, false);
        var cMajor = new CMajorEnveloped(audioFormat);
        var oscillator = MasterOscillator.create();
        cMajor.connectTo(oscillator);

        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
