package vibrato.dspunits.filters;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.oscillators.State;
import vibrato.vectors.RealVector;

import static vibrato.dspunits.DspUnit.ops;

public class Line implements DspSource<RealVector>, RealVector, State {

    private final RealVector input;
    private final Conductivity conductivity = new Conductivity();

    private final double[] output;
    private boolean conductive;

    public Line(RealVector input) {
        this.input = input;
        this.output = new double[input.size()];
    }

    @Override
    public RealVector output() {
        return this;
    }

    @Override
    public double value(int i) {
        conduct();
        return output[i];
    }

    @Override
    public int size() {
        return output.length;
    }

    private void conduct() {
        if (!conductive) {
            conductive = true;
            for (int i = 0; i < output.length; i++) {
                output[i] = input.value(i);
            }
        }
    }

    @Override
    public Operation[] operations() {
        return ops(conductivity);
    }

    private class Conductivity implements Operation {

        @Override
        public State state() {
            return Line.this;
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
