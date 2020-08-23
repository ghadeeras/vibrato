package vibrato.dspunits.music.synthesis.generators;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.functions.RealFunction;
import vibrato.functions.Signal;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealValue;

import static vibrato.dspunits.DspUnit.ops;

public class WaveGenerator implements DspSource<RealValue>, State {

    private final RealValue reset;
    private final RealValue input;
    private final Signal wave;

    private double t;
    private double value;

    private final RealValue output = () -> value;
    private final Operation generation = new Generation();

    private WaveGenerator(RealValue reset, RealValue input, Signal wave) {
        this.reset = reset;
        this.input = input;
        this.wave = wave;
        this.t = 0;
        this.value = wave.at(t);
    }

    @Override
    public RealValue output() {
        return output;
    }

    @Override
    public Operation[] operations() {
        return ops(generation);
    }

    private class Generation implements Operation {

        @Override
        public State state() {
            return WaveGenerator.this;
        }

        @Override
        public void readPhase() {
            double deltaT = input.value();
            if (reset.value() != 0) {
                t = 0;
            } else {
                t += deltaT;
            }
        }

        @Override
        public void writePhase() {
            value = wave.at(t);
        }

    }

    public static DspController<RealValue, RealValue, RealValue> from(RealFunction function) {
        return from(function.asSignal());
    }

    public static DspController<RealValue, RealValue, RealValue> from(Signal signal) {
        return reset -> input -> new WaveGenerator(reset, input, signal);
    }

}
