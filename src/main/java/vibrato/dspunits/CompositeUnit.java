package vibrato.dspunits;

import vibrato.oscillators.Operation;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CompositeUnit implements DspUnit {

    protected final double clockSpeed;
    protected final double zHertz;
    protected final double zSecond;

    private final List<Operation> operations = new ArrayList<>();

    protected CompositeUnit(double clockSpeed) {
        this.clockSpeed = clockSpeed;
        this.zHertz = 2 * Math.PI / clockSpeed;
        this.zSecond = 1 / zHertz;
    }

    private <U extends DspUnit> U connect(U unit) {
        operations.addAll(Arrays.asList(unit.operations()));
        return unit;
    }

    @Override
    public Operation[] operations() {
        return operations.toArray(new Operation[0]);
    }

    protected <O extends RealVector> Source<O> from(DspSource<O> source) {
        return new Source<>(source);
    }

    protected <O extends RealVector> Source<O> fromSource(O output) {
        return from(DspSource.create(output));
    }

    protected <I extends RealVector, O extends RealVector> Filter<I, O> from(DspFilter<I, O> filter) {
        return new Filter<>(filter);
    }

    protected Filter<RealVector, RealVector> replicate(DspFilter<RealValue, RealValue> filter) {
        return from(input -> DspSource.create(RealVector.join(IntStream.range(0, input.size())
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

        public <V extends RealVector> Source<V> through(Filter<? super O, V> filter) {
            return through(filter.filter);
        }

        public <V extends RealVector> Source<V> through(DspFilter<? super O, V> filter) {
            return from(filter.apply(this.source.output()));
        }

        public <V extends RealVector, C extends  RealVector> Source<V> through(DspController<C, ? super O, V> controller, Source<C> controlSignal) {
            return through(controller.apply(controlSignal.source.output()));
        }

        @SafeVarargs
        public final void into(DspSink<? super O>... sinks) {
            for (DspSink<? super O> sink : sinks) {
                connect(sink.apply(this.source.output()));
            }
        }

        public Source<RealValue> channel(int index) {
            return from(DspSource.create(source.output().valueAt(index)));
        }

    }

    protected class Filter<I extends RealVector, O extends RealVector> {

        final DspFilter<I, O> filter;

        Filter(DspFilter<I, O> filter) {
            this.filter = filter;
        }

        public <V extends RealVector> Filter<I, V> through(Filter<? super O, V> filter) {
            return through(filter.filter);
        }

        public <V extends RealVector> Filter<I, V> through(DspFilter<? super O, V> filter) {
            return from(this.filter.andThen(CompositeUnit.this::connect).andThen(DspSource::output).andThen(filter)::apply);
        }

        public <V extends RealVector, C extends  RealVector> Filter<I, V> through(DspController<C, ? super O, V> controller, Source<C> controlSignal) {
            return through(controller.apply(controlSignal.source.output()));
        }

        public DspSink<I> into(DspSink<? super O> sink) {
            return filter.andThen(CompositeUnit.this::connect).andThen(DspSource::output).andThen(sink)::apply;
        }

        public Filter<I, RealValue> channel(int index) {
            DspFilter<O, RealValue> filter = input -> DspSource.create(input.valueAt(index));
            return through(filter);
        }

    }

}
