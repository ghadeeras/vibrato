package vibrato.dspunits;

import vibrato.vectors.RealValue;

import static vibrato.oscillators.Oscillator.Operation;
import static vibrato.oscillators.Oscillator.State;

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
    protected Operation[] operations() {
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
