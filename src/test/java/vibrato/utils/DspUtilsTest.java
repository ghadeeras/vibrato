package vibrato.utils;

import org.junit.Test;
import vibrato.complex.ComplexNumber;
import vibrato.testtools.Generator;
import vibrato.testtools.TestBase;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static vibrato.complex.ComplexNumber.createXY;
import static vibrato.utils.DspUtils.*;

public class DspUtilsTest extends TestBase {

    @Test
    public void testBitCount() {
        assertThat(bitCount(0), equalTo(0));

        IntStream.rangeClosed(1, 31).forEach(bits -> {
            int minInclusive = 1 << (bits - 1);
            int maxInclusive = (minInclusive << 1) - 1;
            forAnyOf(integersBetween(minInclusive, maxInclusive), i -> {
                assertThat(bitCount(i), equalTo(bits));
            });
        });

        forAnyOf(positiveIntegers(), i -> {
            int bits = bitCount(i);
            int minInclusive = 1 << (bits - 1);
            int maxInclusive = (minInclusive << 1) - 1;
            assertThat(i, allOf(
                greaterThanOrEqualTo(minInclusive),
                lessThanOrEqualTo(maxInclusive)
            ));
        });

        forAnyOf(negativeIntegers(), i -> {
            assertThat(bitCount(i), equalTo(32));
        });
    }

    @Test
    public void testFlipBits() {
        forAnyOf(integers(), i -> {
            assertThat(flipBits(flipBits(i, 32), 32), equalTo(i));
        });

        forAnyOf(integers(), i -> {
            int symmetricNumber1 = i | flipBits(i, 32);
            int symmetricNumber2 = i & flipBits(i, 32);
            assertThat(flipBits(symmetricNumber1, 32), equalTo(symmetricNumber1));
            assertThat(flipBits(symmetricNumber2, 32), equalTo(symmetricNumber2));
        });

        forAnyOf(integers(), i -> {
            int flippedI = flipBits(i, 32);
            IntStream.rangeClosed(1, 31).forEach(bits -> {
                assertThat(flippedI << bits, equalTo(flipBits(i >>> bits, 32)));
                assertThat(flippedI >>> bits, equalTo(flipBits(i << bits, 32)));
            });
        });
    }

    @Test
    public void testCommonDivisorAndMultipliers() {
        forAnyOf(positiveIntegers(), a -> {
            forAnyOf(positiveIntegers(), b -> {
                int divisor = greatestCommonDivisor(a, b);

                assertThat(greatestCommonDivisor(a / divisor, b / divisor), equalTo(1));
                assertThat(leastCommonMultiple(a / divisor, b / divisor), equalTo((a / divisor) * (b / divisor)));
            });
        });
    }

    @Test
    public void testGetRoots_TwoComplexRoots() {
        forAnyOf(complexNumbers().filter(cn -> not(approximatelyEqualTo(0)).matches(cn.y())), cn -> {
            ComplexNumber[] roots = getRoots(1, -2 * cn.x(), cn.length() * cn.length());

            assertThat(roots.length, equalTo(2));
            assertThat(roots, hasItemInArray(approximatelyEqualTo(cn)));
            assertThat(roots, hasItemInArray(approximatelyEqualTo(cn.conjugate())));
        });
    }

    @Test
    public void testGetRoots_TwoRealRoots() {
        forAnyOf(doubles(), root1 -> {
            forAnyOf(doubles().filter(not(approximatelyEqualTo(root1))::matches), root2 -> {
                ComplexNumber[] roots = getRoots(1, -(root1 + root2), root1 * root2);

                assertThat(roots.length, equalTo(2));
                assertThat(roots, hasItemInArray(approximatelyEqualTo(createXY(root1, 0))));
                assertThat(roots, hasItemInArray(approximatelyEqualTo(createXY(root2, 0))));
            });
        });
    }

    @Test
    public void testGetRoots_OneTangentRoot() {
        forAnyOf(doubles(), root -> {
            ComplexNumber[] roots = getRoots(1, -2 * root, root * root);

            assertThat(roots.length, equalTo(1));
            assertThat(roots, hasItemInArray(approximatelyEqualTo(createXY(root, 0))));
        });
    }

    @Test
    public void testGetRoots_OneRoot() {
        forAnyOf(doubles(), root -> {
            ComplexNumber[] roots = getRoots(0, 1, -root);

            assertThat(roots.length, equalTo(1));
            assertThat(roots, hasItemInArray(approximatelyEqualTo(createXY(root, 0))));
        });
    }

    @Test
    public void testGetRoots_NoRoots() {
        forAnyOf(doubles(), c -> {
            ComplexNumber[] roots = getRoots(0, 0, c);

            assertThat(roots.length, equalTo(0));
        });
    }

    @Test
    public void testGetRoots() {
        Generator<Double> as = doubles();
        Generator<Double> bs = doubles();
        Generator<Double> cs = doubles();
        Generator<double[]> coefficients = () -> new double[] { as.get(), bs.get(), cs.get() };
        forAnyOf(coefficients, abc -> {
            double d = DoubleStream.of(abc).map(Math::abs).filter(v -> v != 0).max().orElse(1);
            double a = abc[0] / d;
            double b = abc[1] / d;
            double c = abc[2] / d;
            ComplexNumber[] roots = getRoots(a, b, c);

            for (ComplexNumber root : roots) {
                ComplexNumber result = createXY(a, 0)
                    .mul(root).add(createXY(b, 0))
                    .mul(root).add(createXY(c, 0));
                assertThat(result, approximatelyEqualTo(createXY(0, 0)));
            }
        });
    }

}
