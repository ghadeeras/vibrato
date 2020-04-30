package vibrato.dspunits.filters;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.vectors.RealVector;

import static vibrato.dspunits.DspUnit.ops;

public class Line implements DspSource<RealVector>, RealVector {

    private final RealVector input;
    private final Conductivity conductivity;

    private final double[] output;

    public Line(RealVector input) {
        this.input = input;
        this.output = new double[input.size()];
        this.conductivity = new Conductivity(this::inputToOutput);
    }

    private void inputToOutput() {
        for (int i = 0; i < output.length; i++) {
            output[i] = input.value(i);
        }
    }

    @Override
    public RealVector output() {
        return this;
    }

    @Override
    public double value(int i) {
        conductivity.conduct();
        return output[i];
    }

    @Override
    public int size() {
        return output.length;
    }

    @Override
    public Operation[] operations() {
        return ops(conductivity);
    }

}
