package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.filters.Line;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.Operation;
import vibrato.vectors.CircularBuffer;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import static vibrato.dspunits.DspUnit.ops;

public class VariableDelays implements DspSource<RealVector> {

    private final CircularBuffer delayLine;
    private final Operation stateUpdate;
    private final Line output;


    private VariableDelays(RealValue input, int maxDelay, RealVector delays, Interpolator interpolator) {
        this.delayLine = new CircularBuffer(maxDelay);
        this.output = new Line(input.size(), i -> interpolator.value(delayLine, -delays.value(i)));
        this.stateUpdate = delayLine.readingFrom(input);
    }

    public static DspController<RealVector, RealValue, RealVector> ofMax(double maxDelay) {
        return ofMax(maxDelay, Interpolator.linear);
    }

    public static DspController<RealVector, RealValue, RealVector> ofMax(double maxDelay, Interpolator interpolator) {
        return delay -> input -> new VariableDelays(input, (int) Math.ceil(maxDelay), delay, interpolator);
    }

    @Override
    public RealVector output() {
        return output;
    }

    @Override
    public Operation[] operations() {
        return ops(ops(stateUpdate), ops(output));
    }

}
