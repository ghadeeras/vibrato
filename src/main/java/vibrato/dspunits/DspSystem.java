package vibrato.dspunits;

import vibrato.dspunits.filters.Line;
import vibrato.dspunits.filters.Wire;
import vibrato.dspunits.filters.fir.AbstractFIRFilter;
import vibrato.functions.Linear;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.music.synthesis.generators.WaveOscillator;
import vibrato.music.synthesis.generators.WaveTable;
import vibrato.vectors.Buffer;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DspSystem extends CompositeUnit {

    protected DspSystem(double clockSpeed) {
        super(clockSpeed);
    }

    protected Source<RealValue> scalarConstant(double value) {
        return fromSource(() -> value);
    }

    @SafeVarargs
    protected final Source<RealVector> join(Source<RealValue>... sources) {
        List<RealValue> outputs = Stream.of(sources).map(source -> source.source.output()).collect(toList());
        return from(DspSource.create(RealVector.join(outputs)));
    }

    protected final Filter<RealValue, RealValue> scalarFunction(RealFunction function) {
        return from(input -> new Wire(() -> function.apply(input.value())));
    }

    protected final Filter<RealValue, RealValue> scalarMultiplication(double factor) {
        return scalarFunction(Linear.linear(factor));
    }

    protected final Filter<RealValue, RealValue> scalarDivisionOf(double numerator) {
        return scalarFunction(input -> numerator / input);
    }

    protected final Filter<RealValue, RealValue> scalarDivisionBy(double denominator) {
        return scalarFunction(input -> input / denominator);
    }

    protected final Filter<RealValue, RealValue> diff = from(AbstractFIRFilter.create(1,
        (input, delay) -> input - delay.value(-1)
    ));

    protected final Filter<RealValue, RealValue> posDiff = from(AbstractFIRFilter.create(1,
        (input, delay) -> Math.max(input - delay.value(-1), 0)
    ));

    protected final Filter<RealValue, RealValue> negDiff = from(AbstractFIRFilter.create(1,
        (input, delay) -> Math.min(input - delay.value(-1), 0)
    ));

    protected final Filter<RealValue, RealValue> scalarSquareRoot = scalarFunction(Math::sqrt);
    protected final DspController<RealValue, RealValue, RealValue> scalarAddition = control -> input -> new Wire(() -> control.value() + input.value());
    protected final DspController<RealValue, RealValue, RealValue> scalarMultiplication = control -> input -> new Wire(() -> control.value() * input.value());
    protected final DspController<RealValue, RealValue, RealValue> scalarDivision = control -> input -> new Wire(() -> input.value() / control.value());

    protected final DspController<RealValue, RealValue, RealValue> amplitudeModulation(double index) {
        return modulatingControl -> carrierInput -> new Wire(() -> (index * modulatingControl.value() + 1) * carrierInput.value());
    }

    protected final Filter<RealVector, RealValue> vectorLengthSquared = from(input -> new Wire(input::lengthSquared));
    protected final Filter<RealVector, RealValue> vectorLength =  from(input -> new Wire(input::length));
    protected final DspController<RealVector, RealVector, RealVector> vectorDivision = control -> input -> new Line(input.size(), i -> input.value(i) / control.value(i));

    protected final Filter<RealVector, RealValue> dotProduct(double... components) {
        var vector = new Buffer(components);
        return from(input -> new Wire(() -> vector.dotProduct(input, components.length)));
    }

    private static final int sineResolution = 8;

    protected static final WaveTable sineLikeWave = sineLikeWave(sineResolution);

    protected static WaveTable sineLikeWave(int resolution) {
        return WaveTable.create(
            DoubleStream
                .iterate(0, d -> d + 1)
                .map(d -> Math.sin(d * 2 * Math.PI / resolution))
                .limit(resolution)
                .toArray()
        ).withCachedInterpolation(Interpolator.cubic);
    }

    protected Source<RealValue> scalarImpulsesAt(double frequency) {
        return scalarsAt(2 * frequency, 1, 0).through(posDiff);
    }

    protected Source<RealValue> scalarsAt(double frequency, double... samples) {
        var samplesTable = WaveTable.create(samples);
        return scalarConstant(frequency / samples.length)
            .through(WaveOscillator.create(samplesTable, Interpolator.truncating));
    }

}
