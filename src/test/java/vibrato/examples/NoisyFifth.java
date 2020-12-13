package vibrato.examples;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.filters.Mixer;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.RandomSource;
import vibrato.oscillators.MainOscillator;
import vibrato.vectors.RealValue;

import javax.sound.sampled.AudioFormat;

public class NoisyFifth extends DspApp {

    public NoisyFifth(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate());
        var randomSource = from(RandomSource.uniform(sameSeed()));
        var panning = Mixer.matrix(new double[][]{
            {0.75, 0.25},
            {0.25, 0.75}
        });
        var audioSink = AudioSink.create(audioFormat);
        join(
            randomSource.through(bpf(293.66 * zHertz)),
            randomSource.through(bpf(440.00 * zHertz))
        ).through(panning).into(audioSink);
    }

    private DspFilter<RealValue, RealValue> bpf(double frequency) {
        return SecondOrderFilter.bpf(128, frequency, frequency / 440, 0.5);
    }

    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 2, true, false);
        var system = new NoisyFifth(audioFormat);
        var oscillator = MainOscillator.create();
        system.connectTo(oscillator);
        oscillator.oscillateUntil(DspApp::pressedEnter);
    }

}
