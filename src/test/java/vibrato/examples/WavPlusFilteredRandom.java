package vibrato.examples;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSink;
import vibrato.dspunits.filters.Mixer;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.oscillators.MasterOscillator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import javax.sound.sampled.AudioInputStream;

public class WavPlusFilteredRandom extends DspApp {

    public WavPlusFilteredRandom(AudioInputStream stream) {
        super(stream.getFormat().getFrameRate());

        var audioSource = define(AudioSource.from(stream, stream.getFormat()));
        var randomSource = define(RandomSource.uniform(null));

        var mixer = Mixer.average();
        var middleAFreqFilter = SecondOrderFilter.bpf(128, 440 * zHertz, zHertz, 0.5);
        var audioSink = AudioSink.of(stream.getFormat());

        var audioChannel = audioSource.then(mixer);
        var randomChannel = randomSource.then(middleAFreqFilter);

        join(audioChannel, randomChannel).then(audioSink);
    }

    public static void main(String[] args) {
        AudioInputStream audioInputStream = openAudioInputStream(args);

        float clockSpeed = audioInputStream.getFormat().getFrameRate();
        MasterOscillator oscillator = new MasterOscillator(clockSpeed);
        WavPlusFilteredRandom system = new WavPlusFilteredRandom(audioInputStream);
        system.connectTo(oscillator);

        loop(oscillator);
    }

}
