package vibrato.music.synthesis.base;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.fir.AbstractFIRFilter;
import vibrato.music.synthesis.generators.WaveGenerator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.functions.Signal;
import vibrato.vectors.RealValue;

public class BasicInstrument extends DspSystem implements DspSource<RealValue> {

    private final Source<RealValue> source;

    private BasicInstrument(WaveTable wave, Signal attackEnvelope, Signal muteEnvelope, RealValue excitation, RealValue frequency) {
        super(1);

        var negToPosDetector = AbstractFIRFilter.create(1, (input, state) ->
            input >= 0 && state.value(-1) < 0 ? 1 : 0
        );

        var excitationSource = fromSource(excitation);
        var attackSpeedSource = excitationSource
            .through(scalarFunction(input -> input > 0 ? +input : 0));
        var attenuationSpeedSource = excitationSource
            .through(scalarFunction(input -> input < 0 ? -input : 0));
        var resetSource = excitationSource
            .through(negToPosDetector);

        var waveSource = fromSource(frequency)
            .through(WaveOscillator.from(wave));
        var attackSource = attackSpeedSource
            .through(WaveGenerator.from(attackEnvelope), resetSource);
        var attenuationSource = attenuationSpeedSource
            .through(WaveGenerator.from(muteEnvelope), resetSource);

        this.source = waveSource
            .through(scalarMultiplication, attackSource)
            .through(scalarMultiplication, attenuationSource);
    }

    @Override
    public RealValue output() {
        return source.output();
    }

    public static DspController<RealValue, RealValue, RealValue> define(WaveTable wave, Signal attackEnvelope, Signal muteEnvelope) {
        return excitation -> frequency -> new BasicInstrument(wave, attackEnvelope, muteEnvelope, excitation, frequency);
    }

}
