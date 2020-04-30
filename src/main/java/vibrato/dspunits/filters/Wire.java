package vibrato.dspunits.filters;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.vectors.RealValue;

import static vibrato.dspunits.DspUnit.ops;

public class Wire implements DspSource<RealValue>, RealValue {

    private final RealValue input;
    private final Conductivity conductivity;

    private double output;

    public Wire(RealValue input) {
        this.input = input;
        this.output = 0;
        this.conductivity = new Conductivity(this::inputToOutput);
    }

    private void inputToOutput() {
        output = input.value();
    }

    @Override
    public RealValue output() {
        return this;
    }

    @Override
    public double value() {
        conductivity.conduct();
        return output;
    }

    @Override
    public Operation[] operations() {
        return ops(conductivity);
    }

}
