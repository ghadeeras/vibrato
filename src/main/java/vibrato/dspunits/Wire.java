package vibrato.dspunits;

import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealValue;

public class Wire extends DspUnit implements RealValue, State {

    private final RealValue input;
    private final Conductivity conductivity = new Conductivity();

    private double output;
    private boolean conductive;

    public Wire(RealValue input) {
        this.input = input;
    }

    @Override
    public double value() {
        if (!conductive) {
            output = input.value();
            conductive = true;
        }
        return output;
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
