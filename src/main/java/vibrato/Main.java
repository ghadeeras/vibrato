package vibrato;

import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.dspunits.sources.WaveSource;
import vibrato.functions.DiscreteSignal;
import vibrato.functions.Sinc;
import vibrato.system.DspSystem;
import vibrato.vectors.RealValue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Main extends DspSystem {

    private Main(String inputMediaFile) throws IOException, UnsupportedAudioFileException {
        this(AudioSystem.getAudioInputStream(new File(inputMediaFile)));
    }

    private Main(AudioInputStream audioInputStream) {
        super(audioInputStream.getFormat().getFrameRate());

        int waveLength = (int) masterClockSpeed;
        DiscreteSignal sinc = Sinc.sinc()
            .compress(500 * zHertz)
            .delay(masterClockSpeed / 2)
            .discrete();

        AudioSource source = audioSource(audioInputStream.getFormat(), audioInputStream);
        RandomSource random = randomSource(null, false);
        WaveSource wave = periodicWaveSource(sinc, waveLength);

        RealValue modulator = multiplication(random, wave);

        SecondOrderFilter filter = secondOrderBPF(modulator,
            128,
            500 * zHertz,
            zHertz,
            0.5
        );

        RealValue mixer = average(filter, source.valueAt(0), source.valueAt(1));

        AudioSink sink = audioSink(filter, audioInputStream.getFormat());

    }

    public void loop() throws IOException {
        long cycles = Math.round(masterClockSpeed);
        while (System.in.available() == 0) {
            masterOscillator().oscillate(cycles);
        }
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        Main main = new Main(args[0]);
        main.loop();
    }

}
