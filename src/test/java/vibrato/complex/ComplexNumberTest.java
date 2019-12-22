package vibrato.complex;

import org.junit.Test;
import vibrato.testtools.TestBase;
import vibrato.testtools.UnaryOperatorTester;
import vibrato.testtools.VibratoMatchers;

import java.util.function.Predicate;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static vibrato.complex.ComplexNumber.createLA;
import static vibrato.complex.ComplexNumber.createXY;

public class ComplexNumberTest extends TestBase {

    private UnaryOperatorTester<ComplexNumber> operatorTester = new UnaryOperatorTester<>(this::complexNumbers, VibratoMatchers::approximatelyEqualTo);

    private ComplexNumber zero = createXY(0, 0);
    private ComplexNumber one = createXY(1, 0);

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
            assertThat(c.times(c.conjugate()), approximatelyEqualTo(createXY(c.length() * c.length(), 0)));
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
    public void test_rotation() {
        forAnyOf(complexNumbers().filter(notZero), c1 -> {
            forAnyOf(complexNumbers().filter(notZero), c2 -> {
                ComplexNumber c = c1.times(c2);
                assertThat(c.length(), approximatelyEqualTo(c1.length() * c2.length()));
                assertThat(mod2Pi(c.angle()), approximatelyEqualTo(mod2Pi(c1.angle() + c2.angle())));
            });
        });
    }

    private double mod2Pi(double value) {
        double period = 2 * Math.PI;
        return (value + period) % period;
    }

}
