package vibrato.dspunits.filters;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealValue;

import static vibrato.dspunits.DspUnit.ops;

public class Wire implements DspSource<RealValue>, RealValue, State {

    private final RealValue input;
    private final Conductivity conductivity = new Conductivity();

    private double output;
    private boolean conductive;

    public Wire(RealValue input) {
        this.input = input;
        this.output = 0;
    }

    @Override
    public RealValue output() {
        return this;
    }

    @Override
    public double value() {
        conduct();
        return output;
    }

    private void conduct() {
        if (!conductive) {
            conductive = true;
            output = input.value();
        }
    }

    @Override
    public Operation[] operations() {
        return ops(conductivity);
    }

    private class Conductivity implements Operation {

        @Override
        public State state() {
            return Wire.this;
        }

        @Override
        public void readPhase() {
        }

        @Override
        public void writePhase() {
            conductive = false;
        }

    }

}
