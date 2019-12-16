package vibrato.testtools;

import org.hamcrest.Matcher;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class UnaryOperatorTester<T> extends TestBase {

    private final Supplier<Generator<T>> generator;
    private final Function<T, Matcher<T>> equality;

    public UnaryOperatorTester(Supplier<Generator<T>> generatorSupplier, Function<T, Matcher<T>> equality) {
        this.generator = generatorSupplier;
        this.equality = equality;
    }

    public UnaryOperatorTester<T> filter(Predicate<T> predicate) {
        return new UnaryOperatorTester<>(() -> generator.get().filter(predicate), equality);
    }

    public void testReversibility(BinaryOperator<T> op, BinaryOperator<T> reverseOp) {
        forAnyOf(generator.get(), a -> {
            forAnyOf(generator.get(), b -> {
                assertThat(reverseOp.apply(op.apply(a, b), b), equality.apply(a));
            });
        });
    }

    public void testCommutativity(BinaryOperator<T> op) {
        forAnyOf(generator.get(), a -> {
            forAnyOf(generator.get(), b -> {
                T aOpB = op.apply(a, b);
                T bOpA = op.apply(b, a);
                assertThat(aOpB, equality.apply(bOpA));
            });
        });
    }

    public void testAssociativity(BinaryOperator<T> op) {
        forAnyOf(generator.get(), 10, a -> {
            forAnyOf(generator.get(), 10, b -> {
                forAnyOf(generator.get(), 10, c -> {
                    T abOpc = op.apply(op.apply(a, b), c);
                    T aOpBc = op.apply(a, op.apply(b, c));
                    assertThat(abOpc, equality.apply(aOpBc));
                });
            });
        });
    }

    public void testDistributivity(BinaryOperator<T> op1, BinaryOperator<T> op2) {
        forAnyOf(generator.get(), 10, a -> {
            forAnyOf(generator.get(), 10, b -> {
                forAnyOf(generator.get(), 10, c -> {
                    T aOpBc = op1.apply(a, op2.apply(b, c));
                    T abOpAc = op2.apply(op1.apply(a, b), op1.apply(a, c));
                    assertThat(aOpBc, equality.apply(abOpAc));
                });
            });
        });
    }

    public void testIdentity(BinaryOperator<T> op, T identity) {
        forAnyOf(generator.get(), a -> {
            assertThat(op.apply(a, identity), equality.apply(a));
        });
    }

    public void testField(BinaryOperator<T> addition, BinaryOperator<T> subtraction, T additionIdentity, BinaryOperator<T> multiplication, BinaryOperator<T> division, T multiplicationIdentity) {
        testIdentity(addition, additionIdentity);
        testIdentity(multiplication, multiplicationIdentity);
        testCommutativity(addition);
        testCommutativity(multiplication);
        testAssociativity(addition);
        testAssociativity(multiplication);
        testReversibility(addition, subtraction);
        filter(not(equality.apply(additionIdentity))::matches).testReversibility(multiplication, division);
        testDistributivity(multiplication, addition);
    }

}
