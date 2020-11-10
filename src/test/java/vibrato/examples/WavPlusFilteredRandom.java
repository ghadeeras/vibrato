package vibrato.examples;

import vibrato.dspunits.filters.Mixer;
import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.oscillators.MasterOscillator;

import javax.sound.sampled.AudioInputStream;

public class WavPlusFilteredRandom extends DspApp {

    public WavPlusFilteredRandom(AudioInputStream stream) {
        super(stream.getFormat().getFrameRate());

        var audioSource = from(AudioSource.from(stream, stream.getFormat()));
        var randomSource = from(RandomSource.uniform(sameSeed()));

        var mixer = Mixer.average();
        var middleAFreqFilter = SecondOrderFilter.bpf(128, 440 * zHertz, zHertz, 0.5);
        var audioSink = AudioSink.of(stream.getFormat());

        var audioChannel = audioSource.through(mixer);
        var randomChannel = randomSource.through(middleAFreqFilter);

        join(audioChannel, randomChannel).into(audioSink);
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
