package vibrato.music.synthesis.base;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.fir.AbstractFIRFilter;
import vibrato.functions.Signal;
import vibrato.music.synthesis.generators.WaveGenerator;
import vibrato.vectors.RealValue;

public class EnvelopeGenerator extends DspSystem implements DspSource<RealValue> {

    private final Source<RealValue> source;

    private EnvelopeGenerator(Signal attackEnvelope, Signal muteEnvelope, RealValue excitation) {
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

        var attackSource = attackSpeedSource
            .through(WaveGenerator.create(attackEnvelope), resetSource);
        var attenuationSource = attenuationSpeedSource
            .through(WaveGenerator.create(muteEnvelope), resetSource);

        this.source = attackSource
            .through(scalarMultiplication, attenuationSource);
    }

    @Override
    public RealValue output() {
        return source.output();
    }

    public static DspFilter<RealValue, RealValue> create(Signal attackEnvelope, Signal muteEnvelope) {
        return excitation -> new EnvelopeGenerator(attackEnvelope, muteEnvelope, excitation);
    }

}
