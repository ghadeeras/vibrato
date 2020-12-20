package vibrato.testtools;

import org.hamcrest.Matcher;
import vibrato.complex.Complex;
import vibrato.complex.ComplexNumber;

import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TestBase {

    private final int bigNumber = 1 << 20;

    protected final long seed = getClass().getSimpleName().hashCode();
    protected final Random random = new Random(seed);

    public <T> void forAnyOf(Generator<T> set, Consumer<T> check) {
        int sampleSize = 100;
        forAnyOf(set, sampleSize, check);
    }

    public <T> void forAnyOf(Generator<T> set, int sampleSize, Consumer<T> check) {
        Stream.generate(set)
            .limit(sampleSize)
            .filter(Objects::nonNull)
            .distinct()
            .forEach(check);
    }

    public <T> Generator<T> always(T value) {
        return () -> value;
    }

    public Generator<Integer> positiveIntegers() {
        return integersBetween(1, bigNumber);
    }

    public Generator<Integer> negativeIntegers() {
        return integersBetween(-bigNumber, -1);
    }

    public Generator<Integer> integers() {
        return integersBetween(-bigNumber, +bigNumber);
    }

    public Generator<Integer> integersBetween(int min, int max) {
        return max > min ?
            Generator.create(() -> random.ints(min, max).findFirst().orElse(min), min, max) :
            max != min ? integersBetween(max, min) : () -> min;
    }

    public Generator<Double> doubles() {
        return doublesBetween(-bigNumber, +bigNumber);
    }

    public Generator<Double> doublesBetween(double min, double max) {
        return max > min ?
            Generator.create(() -> random.doubles(min, max).findFirst().orElse(min), min, max) :
            max != min ? doublesBetween(max, min) : () -> min;
    }

    public Generator<ComplexNumber> complexNumbers() {
        return complexNumbersXY(doubles(), doubles());
    }

    public Generator<ComplexNumber> complexNumbersXY(Generator<Double> xs, Generator<Double> ys) {
        return () -> ComplexNumber.createRI(xs.get(), ys.get());
    }

    public Generator<ComplexNumber> complexNumberLA(Generator<Double> lengths, Generator<Double> angles) {
        return () -> ComplexNumber.createLA(lengths.get(), angles.get());
    }

    public Generator<Boolean> booleans() {
        return Generator.create(random::nextBoolean, false, true);
    }

    public static Matcher<Double> approximatelyEqualTo(double expectedValue) {
        return VibratoMatchers.approximatelyEqualTo(expectedValue);
    }

    public static <T extends Complex<?>> Matcher<T> approximatelyEqualTo(T expectedValue) {
        return VibratoMatchers.approximatelyEqualTo(expectedValue);
    }
    
}
