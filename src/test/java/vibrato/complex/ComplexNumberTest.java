package vibrato.complex;

import org.junit.Test;
import vibrato.testtools.TestBase;
import vibrato.testtools.UnaryOperatorTester;
import vibrato.testtools.VibratoMatchers;

import java.util.function.Predicate;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static vibrato.complex.ComplexNumber.createLA;
import static vibrato.complex.ComplexNumber.createRI;

public class ComplexNumberTest extends TestBase {

    private UnaryOperatorTester<ComplexNumber> operatorTester = new UnaryOperatorTester<>(this::complexNumbers, VibratoMatchers::approximatelyEqualTo);

    private ComplexNumber zero = createRI(0, 0);
    private ComplexNumber one = createRI(1, 0);

    private Predicate<ComplexNumber> notZero = not(approximatelyEqualTo(zero))::matches;

    @Test
    public void test_complex_field() {
        operatorTester.testField(
            ComplexNumber::plus, ComplexNumber::minus, zero,
            ComplexNumber::times, ComplexNumber::dividedBy, one
        );
    }

    @Test
    public void test_conjugation() {
        forAnyOf(complexNumbers(), c -> {
            assertThat(c.times(c.conjugate()), approximatelyEqualTo(createRI(c.lengthSquared(), 0)));
        });
    }

    @Test
    public void test_negation() {
        forAnyOf(complexNumbers(), c -> {
            assertThat(c.negated(), approximatelyEqualTo(zero.minus(c)));
        });
    }

    @Test
    public void test_reciprocation() {
        forAnyOf(complexNumbers().filter(notZero), c -> {
            assertThat(c.reciprocal(), approximatelyEqualTo(one.dividedBy(c)));
        });
    }

    @Test
    public void test_scaling() {
        forAnyOf(complexNumbers(), c -> {
            forAnyOf(doubles(), s -> {
                assertThat(c.scaled(s), approximatelyEqualTo(c.times(createLA(s, 0))));
            });
        });
    }

    @Test
    public void test_multiplication() {
        forAnyOf(complexNumbers().filter(notZero), c1 -> {
            forAnyOf(complexNumbers().filter(notZero), c2 -> {
                ComplexNumber c = c1.times(c2);
                assertThat(c.length(), approximatelyEqualTo(c1.length() * c2.length()));
                assertThat(mod2Pi(c.angle()), approximatelyEqualTo(mod2Pi(c1.angle() + c2.angle())));
            });
        });
    }

    @Test
    public void test_rotation() {
        forAnyOf(complexNumbers().filter(notZero), c -> {
            forAnyOf(doubles().filter(d -> d != 0), angle -> {
                ComplexNumber rotated = c.rotated(angle);
                assertThat(rotated.length(), approximatelyEqualTo(c.length()));
                assertThat(mod2Pi(rotated.angle()), approximatelyEqualTo(mod2Pi(c.angle() + angle)));
            });
        });
    }

    private double mod2Pi(double value) {
        double period = 2 * Math.PI;
        double mod = (value + period) % period;
        return mod >= 0 ? mod : mod + period;
    }

}
