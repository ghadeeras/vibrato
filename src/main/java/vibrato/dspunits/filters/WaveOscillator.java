package vibrato.dspunits.filters;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealValue;
import vibrato.vectors.WaveTable;

import static vibrato.dspunits.DspUnit.ops;

public class WaveOscillator implements DspSource<RealValue>, State {

    private final RealValue input;

    private final WaveTable waveTable;
    private final Interpolator interpolator;
    private final double baseWaveSize;
    private final double baseFrequency;

    private final RealValue outputValue;

    private final Rotation rotation = new Rotation();
    private double samplingIncrement = 0;
    private double phase = 0;
    private double output = 0;

    private WaveOscillator(RealValue input, WaveTable waveTable, Interpolator interpolator) {
        this.input = input;

        this.waveTable = waveTable;
        this.interpolator = interpolator;
        this.baseWaveSize = this.waveTable.size();
        this.baseFrequency = 2 * Math.PI / baseWaveSize;

        this.outputValue = () -> output;

        rotation.writePhase();
    }

    @Override
    public RealValue output() {
        return outputValue;
    }

    @Override
    public Operation[] operations() {
        return ops(rotation);
    }

    public static DspFilter<RealValue, RealValue> from(WaveTable waveTable) {
        return from(waveTable, Interpolator.cubic);
    }

    public static DspFilter<RealValue, RealValue> from(WaveTable waveTable, Interpolator interpolator) {
        return input -> new WaveOscillator(input, waveTable, interpolator);
    }

    private class Rotation implements Operation {

        @Override
        public State state() {
            return WaveOscillator.this;
        }

        @Override
        public void readPhase() {
            samplingIncrement = input.value() / baseFrequency;
            phase = (phase + samplingIncrement) % baseWaveSize;
        }

        @Override
        public void writePhase() {
            output = waveTable.sample(phase, samplingIncrement, interpolator);
        }

    }

}
