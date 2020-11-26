package vibrato.dspunits.filters.iir;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.interpolators.Interpolator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class FirstOrderFilter extends AbstractIIRFilter {

    private final Coefficients coefficients;

    private FirstOrderFilter(RealValue input, Coefficients coefficients) {
        super(input, 1);
        this.coefficients = coefficients;
    }

    private FirstOrderFilter(RealValue input, Coefficients coefficients, RealValue delayFactor, Interpolator interpolator) {
        super(input, 1, delayFactor, interpolator);
        this.coefficients = coefficients;
    }

    @Override
    protected RealValue feedbackValue(RealVector state) {
        return () -> coefficients.feedBack1 * state.value(-1);
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> coefficients.output1 * state.value(-1) + coefficients.output0 * feedback.value();
    }

    public static class Coefficients implements DspFilter<RealValue, RealValue> {

        public final double feedBack1;
        public final double output0;
        public final double output1;

        private Coefficients(double feedBack1, double output0, double output1) {
            this.feedBack1 = feedBack1;
            this.output0 = output0;
            this.output1 = output1;
        }

        public double zero() {
            return -output1 / output0;
        }

        public double pole() {
            return -feedBack1;
        }

        @Override
        public DspSource<RealValue> apply(RealValue input) {
            return new FirstOrderFilter(input, this);
        }

        public DspController<RealValue, RealValue, RealValue> variableDelay(Interpolator interpolator) {
            return delayFactor -> input -> new FirstOrderFilter(input, this, delayFactor, interpolator);
        }

    }

    public static Coefficients poleZero(double gain, double pole) {
        return new Coefficients(pole, 0, -gain);
    }

    public static Coefficients poleZero(double gain, double pole, double zero) {
        return new Coefficients(pole, gain, -gain * zero);
    }

    public static Coefficients lpf(double constantGain, double cutOffFrequency, double cutOffGain) {
        double alpha = Math.tan(Math.PI * cutOffFrequency) * cutOffGain / Math.sqrt(1 - cutOffGain * cutOffGain);
        double pole = (1 - alpha) / (1 + alpha);
        double gain = constantGain * (1 - pole) / 2;
        return poleZero(gain, pole, -1);
    }

    public static Coefficients hpf(double constantGain, double cutOffFrequency, double cutOffGain) {
        double alpha = Math.tan(Math.PI * cutOffFrequency) * Math.sqrt(1 - cutOffGain*cutOffGain) / cutOffGain;
        double pole = (1 - alpha) / (1 + alpha);
        double gain = constantGain * (1 + pole) / 2;
        return poleZero(gain, pole, 1);
    }

}
