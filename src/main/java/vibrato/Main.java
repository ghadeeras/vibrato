package vibrato;

import vibrato.dspunits.filters.iir.SecondOrderFilter;
import vibrato.dspunits.sinks.AudioSink;
import vibrato.dspunits.sources.AudioSource;
import vibrato.dspunits.sources.RandomSource;
import vibrato.dspunits.sources.WaveSource;
import vibrato.functions.DiscreteSignal;
import vibrato.functions.Sinc;
import vibrato.system.DspSystem;
import vibrato.system.DspSystemBuilder;
import vibrato.vectors.RealValue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Main extends DspSystemBuilder<DspSystem.Master> {

    private Main(DspSystem.Master dspSystem, AudioInputStream audioInputStream) {
        super(dspSystem);

        int waveLength = (int) dspSystem().clockSpeed();
        DiscreteSignal sinc = Sinc.sinc()
            .compress(500 * dspSystem().zHertz())
            .delay(dspSystem().clockSpeed() / 2)
            .discrete();

        AudioSource source = audioSource(audioInputStream.getFormat(), audioInputStream);
        RandomSource random = randomSource(null, false);
        WaveSource wave = periodicWaveSource(sinc, waveLength);

        RealValue modulator = multiplication(random, wave);

        SecondOrderFilter filter = secondOrderBPF(modulator,
            128,
            500 * dspSystem().zHertz(),
            dspSystem().zHertz(),
            0.5
        );

        RealValue mixer = average(filter, source.valueAt(0), source.valueAt(1));

        AudioSink sink = audioSink(filter, audioInputStream.getFormat());

    }

    public void loop() throws IOException {
        long cycles = Math.round(dspSystem().clockSpeed());
        while (System.in.available() == 0) {
            dspSystem().run(cycles);
        }
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(args[0]));
        float clockSpeed = audioInputStream.getFormat().getFrameRate();
        DspSystem.Master dspSystem = new DspSystem.Master(clockSpeed);
        Main main = new Main(dspSystem, audioInputStream);
        main.loop();
    }

}
