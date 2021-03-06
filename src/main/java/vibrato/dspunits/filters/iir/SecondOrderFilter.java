package vibrato.dspunits.filters.iir;

import vibrato.complex.ComplexNumber;
import vibrato.dspunits.DspController;
import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.interpolators.Interpolator;
import vibrato.utils.DspUtils;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class SecondOrderFilter extends AbstractIIRFilter {

    private final Coefficients coefficients;

    private SecondOrderFilter(RealValue input, Coefficients coefficients) {
        super(input, 2);
        this.coefficients = coefficients;
    }

    private SecondOrderFilter(RealValue input, Coefficients coefficients, RealValue delayFactor, Interpolator interpolator) {
        super(input, 2, delayFactor, interpolator);
        this.coefficients = coefficients;
    }

    @Override
    protected RealValue feedbackValue(RealVector state) {
        return () ->
            coefficients.feedBack1 * state.value(-1) +
            coefficients.feedBack2 * state.value(-2);
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () ->
            coefficients.output0 * feedback.value() +
            coefficients.output1 * state.value(-1) +
            coefficients.output2 * state.value(-2);
    }

    public static class Coefficients implements DspFilter<RealValue, RealValue> {

        public final double feedBack1;
        public final double feedBack2;
        public final double output0;
        public final double output1;
        public final double output2;

        public Coefficients(double feedBack1, double feedBack2, double output0, double output1, double output2) {
            this.feedBack1 = feedBack1;
            this.feedBack2 = feedBack2;
            this.output0 = output0;
            this.output1 = output1;
            this.output2 = output2;
        }

        public ComplexNumber[] zeros() {
            return DspUtils.getRoots(output0, output1, output2);
        }

        public ComplexNumber[] poles() {
            return DspUtils.getRoots(1, -feedBack1, -feedBack2);
        }

        @Override
        public DspSource<RealValue> apply(RealValue input) {
            return new SecondOrderFilter(input, this);
        }

        public DspController<RealValue, RealValue, RealValue> variableDelay(Interpolator interpolator) {
            return delayFactor -> input -> new SecondOrderFilter(input, this, delayFactor, interpolator);
        }

    }

    public static Coefficients poleZero(double gain, ComplexNumber pole, ComplexNumber zero) {
        double b1 = -2 * zero.real() * gain;
        double b2 = +zero.length() * zero.length() * gain;
        double a1 = +2 * pole.real();
        double a2 = -pole.length() * pole.length();
        return new Coefficients(a1, a2, gain, b1, b2);
    }

    public static Coefficients notchFilter(double constantGain, double frequency, double bandWidth, double cutOffGain) {
        double beta = Math.tan(Math.PI * bandWidth) * Math.sqrt(1 - cutOffGain * cutOffGain) / cutOffGain;
        double b = 1 / (1 + beta);
        double b0 = constantGain * b;
        double b1 = -2 * b0 * Math.cos(2 * Math.PI * frequency);
        double a1 = +2 * b * Math.cos(2 * Math.PI * frequency);
        double a2 = (1 - 2 * b);
        return new Coefficients(a1, a2, b0, b1, b0);
    }

    public static Coefficients bpf(double constantGain, double frequency, double bandWidth, double cutOffGain) {
        double beta = Math.tan(Math.PI * bandWidth) * cutOffGain / Math.sqrt(1 - cutOffGain * cutOffGain);
        double b = 1 / (1 + beta);
        double b0 = constantGain * (1 - b);
        double a1 = +2 * b * Math.cos(2 * Math.PI * frequency);
        double a2 = (1 - 2 * b);
        return new Coefficients(a1, a2, b0, 0, -b0);
    }

    public static Coefficients lpf(double constantGain, double cutOffFrequency, double cutOffGain) {
        double omega1 = Math.tan(Math.PI * cutOffFrequency);
        double omega2 = omega1 * omega1;
        double d = 1 + omega1 / cutOffGain + omega2;
        double gain = constantGain * omega2 / d;
        double a1 = 2 * (1 - omega2) / d;
        double a2 = (omega1 / cutOffGain - omega2 - 1) / d;
        return new Coefficients(a1, a2, gain, 2 * gain, gain);
    }

    public static Coefficients hpf(double constantGain, double cutOffFrequency, double cutOffGain) {
        double omega1 = 1 / Math.tan(Math.PI * cutOffFrequency);
        double omega2 = omega1 * omega1;
        double d = 1 + omega1 / cutOffGain + omega2;
        double gain = constantGain * omega2 / d;
        double a1 = 2 * (omega2 - 1) / d;
        double a2 = (omega1 / cutOffGain - omega2 - 1) / d;
        return new Coefficients(a1, a2, gain, -2 * gain, gain);
    }

}
