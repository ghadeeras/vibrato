package vibrato.fourier;

import org.junit.Test;
import vibrato.complex.ComplexBuffer;
import vibrato.complex.ComplexNumber;
import vibrato.functions.DiscreteRealFunction;
import vibrato.functions.DiscreteSignal;
import vibrato.functions.Linear;
import vibrato.functions.Pulse;
import vibrato.testtools.Generator;
import vibrato.testtools.TestBase;
import vibrato.vectors.Buffer;
import vibrato.vectors.RealVector;

import java.util.function.Predicate;
import java.util.stream.DoubleStream;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static vibrato.functions.Operator.addition;

public class FFTTest extends TestBase {

    private int sampleCount = 128;
    private ComplexNumber zero = ComplexNumber.createXY(0, 0);
    private ComplexNumber one = ComplexNumber.createXY(1, 0);

    public Predicate<Double> notZero() {
        return not(approximatelyEqualTo(0))::matches;
    }

    private ComplexBuffer transform(DiscreteSignal signal) {
        ComplexBuffer output = new ComplexBuffer(sampleCount);
        fft().transform(vector(signal), output.pointer());
        return output;
    }

    private ComplexBuffer inverseTransform(ComplexBuffer spectrum) {
        ComplexBuffer output = new ComplexBuffer(sampleCount);
        ifft().transform(spectrum.pointer(), output.pointer());
        return output;
    }

    private RealVector vector(DiscreteSignal signal) {
        return new Buffer(signal.samples(sampleCount, 0, 1));
    }

    private DiscreteSignal randomSignal() {
        Generator<Double> sampleGenerator = doublesBetween(-1, 1);
        double[] samples = DoubleStream.generate(sampleGenerator::get).limit(sampleCount).toArray();
        return discreteSignal(samples);
    }

    private DiscreteSignal discreteSignal(double[] samples) {
        return t -> t >= 0 && t < samples.length ? samples[t] : 0;
    }

    private FFT fft() {
        return new FFT(sampleCount);
    }

    private IFFT ifft() {
        return new IFFT(sampleCount);
    }

    @Test
    public void testConstant() {
        ComplexBuffer output = transform(Linear.constant(1).asSignal().discrete());
        ComplexBuffer.Pointer pointer = output.pointer();

        assertThat(pointer.slideTo(0), approximatelyEqualTo(one.scaled(sampleCount)));
        for (int i = 1; i < output.size(); i++) {
            assertThat(pointer.slideTo(i), approximatelyEqualTo(zero));
        }
    }

    @Test
    public void testImpulse() {
        ComplexBuffer output = transform(Pulse.impulse().discrete());
        ComplexBuffer.Pointer pointer = output.pointer();
        for (int i = 0; i < output.size(); i++) {
            assertThat(pointer.slideTo(i), approximatelyEqualTo(one));
        }
    }

    @Test
    public void testDelayedImpulse() {
        forAnyOf(integersBetween(0, sampleCount - 1), sampleCount / 8, delay -> {
            ComplexBuffer output = transform(Pulse.impulse().discrete().delay(delay));
            ComplexBuffer.Pointer pointer = output.pointer();
            for (int i = 0; i < output.size(); i++) {
                double angle = 2 * Math.PI * i / sampleCount;
                assertThat(pointer.slideTo(i), approximatelyEqualTo(one.rotated(-delay * angle)));
            }
        });
    }

    @Test
    public void testLinearity_Addition() {
        DiscreteSignal signal1 = randomSignal();
        DiscreteSignal signal2 = randomSignal();
        DiscreteSignal signal1Plus2 = addition.apply(signal1, signal2);

        ComplexBuffer fft1 = transform(signal1);
        ComplexBuffer fft2 = transform(signal2);
        ComplexBuffer fft1Plus2 = transform(signal1Plus2);

        DiscreteRealFunction expectedXs = addition.apply(fft1.xs(), fft2.xs());
        DiscreteRealFunction expectedYs = addition.apply(fft1.ys(), fft2.ys());
        for (int i = 0; i < fft1Plus2.size(); i++) {
            assertThat(fft1Plus2.xs().apply(i), approximatelyEqualTo(expectedXs.apply(i)));
            assertThat(fft1Plus2.ys().apply(i), approximatelyEqualTo(expectedYs.apply(i)));
        }
    }

    @Test
    public void testLinearity_Scaling() {
        double factor = doubles().filter(notZero()).get();

        DiscreteSignal signal = randomSignal();
        DiscreteSignal scaledSignal = signal.magnify(factor);

        ComplexBuffer fft = transform(signal);
        ComplexBuffer scaledFFT = transform(scaledSignal);

        DiscreteRealFunction expectedXs = fft.xs().stretchOnYAxis(factor);
        DiscreteRealFunction expectedYs = fft.ys().stretchOnYAxis(factor);
        for (int i = 0; i < scaledFFT.size(); i++) {
            assertThat(scaledFFT.xs().apply(i), approximatelyEqualTo(expectedXs.apply(i)));
            assertThat(scaledFFT.ys().apply(i), approximatelyEqualTo(expectedYs.apply(i)));
        }
    }

    @Test
    public void testReversibility() {
        DiscreteSignal signal = randomSignal();

        ComplexBuffer fft = transform(signal);
        ComplexBuffer ifft = inverseTransform(fft);

        for (int i = 0; i < fft.size(); i++) {
            assertThat(ifft.xs().apply(i), approximatelyEqualTo(signal.at(i)));
            assertThat(ifft.ys().apply(i), approximatelyEqualTo(0));
        }
    }

}
