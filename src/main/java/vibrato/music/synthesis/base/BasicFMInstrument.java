package vibrato.music.synthesis.base;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.functions.Signal;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.vectors.RealValue;

public class BasicFMInstrument extends DspSystem implements DspSource<RealValue> {

    private final Source<RealValue> source;

    private BasicFMInstrument(
        WaveTable wave,
        WaveTable modulatingWave,
        double modulationRatio,
        double modulationIndex,
        Signal attackEnvelope,
        Signal muteEnvelope,
        RealValue excitation,
        RealValue frequency
    ) {
        super(1);
        var basicInstrument = BasicInstrument.create(wave, attackEnvelope, muteEnvelope);

        var excitationSource = fromSource(excitation);
        var frequencySource = fromSource(frequency);

        var modulatingControl = frequencySource
            .through(scalarMultiplication(modulationRatio))
            .through(WaveOscillator.create(modulatingWave));

        this.source = frequencySource
            .through(amplitudeModulation(modulationIndex), modulatingControl)
            .through(basicInstrument, excitationSource);
    }

    @Override
    public RealValue output() {
        return source.output();
    }

    public static DspController<RealValue, RealValue, RealValue> create(
        WaveTable wave,
        WaveTable modulatingWave,
        double modulationRatio,
        double modulationIndex,
        Signal attackEnvelope,
        Signal muteEnvelope
    ) {
        return excitation -> frequency -> new BasicFMInstrument(
            wave,
            modulatingWave,
            modulationRatio,
            modulationIndex,
            attackEnvelope,
            muteEnvelope,
            excitation,
            frequency
        );
    }

}
