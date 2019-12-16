package vibrato.testtools;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import vibrato.complex.Complex;
import vibrato.complex.ComplexNumber;

public class VibratoMatchers {

    private static final double precision = 1 / (double) (1 << 20);

    public static Matcher<Double> approximatelyEqualTo(double expectedValue) {
        double acceptableError = Math.abs(expectedValue) <= precision ? precision : Math.abs(expectedValue) * precision;
        double min = expectedValue - acceptableError;
        double max = expectedValue + acceptableError;

        return new CustomTypeSafeMatcher<Double>("Equal to " + expectedValue + " +/- " + acceptableError) {

            @Override
            protected boolean matchesSafely(Double actualValue) {
                return actualValue != null && min <= actualValue && actualValue <= max;
            }

        };
    }

    public static <T extends Complex<?>> Matcher<T> approximatelyEqualTo(T expectedValue) {
        ComplexNumber expectedComplexValue = ComplexNumber.createXY(expectedValue.x(), expectedValue.y());
        double acceptableError = expectedComplexValue.length() <= precision ? precision : expectedComplexValue.length() * precision;

        return new CustomTypeSafeMatcher<T>("Equal to " + expectedComplexValue.toXYString() + " +/- " + acceptableError) {

            @Override
            protected boolean matchesSafely(T actualValue) {
                double error = actualValue != null ? expectedComplexValue.minus(actualValue).length() : Double.MAX_VALUE;
                return error <= acceptableError;
            }

        };
    }

}
