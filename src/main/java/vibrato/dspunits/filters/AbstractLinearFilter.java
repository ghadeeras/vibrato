package vibrato.dspunits.filters;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.oscillators.Operation;
import vibrato.vectors.*;

import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;
import static vibrato.dspunits.DspUnit.ops;

public abstract class AbstractLinearFilter implements DspSource<RealValue>, RealValue {

    private final Wire inputPlusFeedback;
    private final Wire output;
    private final Operation stateUpdate;

    protected AbstractLinearFilter(RealValue input, int order) {
        this(input, order == 1 ? new DelayUnit() : new CircularBuffer(order), identity());
    }

    protected AbstractLinearFilter(RealValue input, int order, RealValue delayFactor, Interpolator interpolator) {
        this(input, new CircularBuffer(order * 1024), state -> adapt(state, order, delayFactor, interpolator));
    }

    private static RealVector adapt(RealVector state, int order, RealValue delayFactor, Interpolator interpolator) {
        RealFunction interpolatedState = state.interpolated(interpolator);
        return RealVector.window(-order, 1, i -> readWithDelayFactor(delayFactor, interpolatedState, i));
    }

    private static double readWithDelayFactor(RealValue delayFactor, RealFunction interpolatedState, int i) {
        double delay = delayFactor.value();
        return interpolatedState.apply(i * delay);
    }

    protected AbstractLinearFilter(RealValue input, AbstractDelayLine state, UnaryOperator<RealVector> stateAdaptation) {
        RealVector stateAdaptor = stateAdaptation.apply(state);
        this.inputPlusFeedback = new Wire(inputPlusFeedbackValue(input, stateAdaptor));
        this.output = new Wire(outputValue(inputPlusFeedback, stateAdaptor));
        this.stateUpdate = state.readingFrom(inputPlusFeedback);
    }

    protected abstract RealValue inputPlusFeedbackValue(RealValue input, RealVector state);

    protected abstract RealValue outputValue(RealValue feedback, RealVector state);

    @Override
    public double value() {
        return output.value();
    }

    @Override
    public Operation[] operations() {
        return ops(
            ops(inputPlusFeedback),
            ops(output),
            ops(stateUpdate)
        );
    }

    @Override
    public RealValue output() {
        return output;
    }

    public static DspFilter<RealValue, RealValue> create(int order, FeedbackFunction feedbackFunction, OutputFunction outputFunction) {
        return input -> new AbstractLinearFilter(input, order) {

            @Override
            protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
                return () -> feedbackFunction.inputPlusFeedbackValue(input.value(), state);
            }

            @Override
            protected RealValue outputValue(RealValue feedback, RealVector state) {
                return () -> outputFunction.outputValue(feedback.value(), state);
            }

        };
    }

    public interface FeedbackFunction {

        double inputPlusFeedbackValue(double input, RealVector state);

    }

    public interface OutputFunction {

        double outputValue(double feedback, RealVector state);

    }

}
