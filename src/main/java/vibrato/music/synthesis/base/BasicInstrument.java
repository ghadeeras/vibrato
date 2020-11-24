package vibrato.music.synthesis.base;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.functions.Signal;
import vibrato.vectors.RealValue;

public class BasicInstrument extends DspSystem implements DspSource<RealValue> {

    private final Source<RealValue> source;

    private BasicInstrument(WaveTable wave, Signal attackEnvelope, Signal muteEnvelope, RealValue excitation, RealValue frequency) {
        super(1);

        var envelopeSource = fromSource(excitation)
            .through(EnvelopeGenerator.create(attackEnvelope, muteEnvelope));

        var waveSource = fromSource(frequency)
            .through(WaveOscillator.create(wave));

        this.source = waveSource
            .through(scalarMultiplication, envelopeSource);
    }

    @Override
    public RealValue output() {
        return source.output();
    }

    public static DspController<RealValue, RealValue, RealValue> create(WaveTable wave, Signal attackEnvelope, Signal muteEnvelope) {
        return excitation -> frequency -> new BasicInstrument(wave, attackEnvelope, muteEnvelope, excitation, frequency);
    }

}
