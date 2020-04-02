package vibrato.dspunits;

import vibrato.oscillators.Operation;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DspSystem implements DspUnit {

    protected final double clockSpeed;
    protected final double zHertz;
    protected final double zSecond;

    private final List<Operation> operations = new ArrayList<>();

    protected DspSystem(double clockSpeed) {
        this.clockSpeed = clockSpeed;
        this.zHertz = 2 * Math.PI / clockSpeed;
        this.zSecond = 1 / zHertz;
    }

    <U extends DspUnit> U connect(U unit) {
        operations.addAll(Arrays.asList(unit.operations()));
        return unit;
    }

    @Override
    public Operation[] operations() {
        return operations.toArray(new Operation[0]);
    }

    protected <O extends RealVector> Source<O> define(DspSource<O> source) {
        return new Source<>(source);
    }

    @SafeVarargs
    protected final Source<RealVector> join(DspSource<RealValue>... sources) {
        return join(Stream.of(sources).map(this::define));
    }

    @SafeVarargs
    protected final Source<RealVector> join(Source<RealValue>... sources) {
        return join(Stream.of(sources));
    }

    private Source<RealVector> join(Stream<Source<RealValue>> sourceStream) {
        return define(DspSource.create(RealVector.join(sourceStream.map(source -> source.source.output()).collect(toList()))));
    }

    public static DspFilter<RealValue, RealVector> fork(int channels) {
        return input -> DspSource.create(input.window(channels));
    }

    protected <I extends RealVector, O extends RealVector> Filter<I, O> define(DspFilter<I, O> filter) {
        return new Filter<>(filter);
    }

    protected Filter<RealVector, RealVector> replicate(DspFilter<RealValue, RealValue> filter) {
        return define(input -> DspSource.create(RealVector.join(IntStream.range(0, input.size())
            .boxed()
            .map(input::valueAt)
            .map(filter)
            .map(this::connect)
            .map(DspSource::output)
            .collect(toList())
        )));
    }

    protected class Source<O extends RealVector> {

        final DspSource<O> source;

        Source(DspSource<O> source) {
            this.source = connect(source);
        }

        public <V extends RealVector> Source<V> then(Filter<O, V> filter) {
            return then(filter.filter);
        }

        public <V extends RealVector> Source<V> then(DspFilter<O, V> filter) {
            return define(filter.apply(this.source.output()));
        }

        @SafeVarargs
        public final void then(DspSink<O>... sinks) {
            for (DspSink<O> sink : sinks) {
                connect(sink.apply(this.source.output()));
            }
        }

        public Source<RealValue> channel(int index) {
            return define(DspSource.create(source.output().valueAt(index)));
        }

    }

    protected class Filter<I extends RealVector, O extends RealVector> {

        final DspFilter<I, O> filter;

        Filter(DspFilter<I, O> filter) {
            this.filter = filter;
        }

        public <V extends RealVector> Filter<I, V> then(Filter<O, V> filter) {
            return then(filter.filter);
        }

        public <V extends RealVector> Filter<I, V> then(DspFilter<O, V> filter) {
            return define(this.filter.andThen(DspSystem.this::connect).andThen(DspSource::output).andThen(filter)::apply);
        }

        public DspSink<I> then(DspSink<O> sink) {
            return filter.andThen(DspSystem.this::connect).andThen(DspSource::output).andThen(sink)::apply;
        }

        public Filter<I, RealValue> channel(int index) {
            DspFilter<O, RealValue> filter = input -> DspSource.create(input.valueAt(index));
            return then(filter);
        }

    }

}
