package vibrato.testtools;

import vibrato.dspunits.*;
import vibrato.dspunits.filters.BufferingWindow;
import vibrato.dspunits.filters.Conductivity;
import vibrato.oscillators.MainOscillator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

public class DspUnitTestBase extends TestBase {

    public double[] randomInput(Integer signalSize) {
        return DoubleStream.generate(doubles()::get).limit(signalSize).toArray();
    }

    public double[] apply(DspFilter<RealValue, RealValue> filter, double[] input) {
        var bufferingWindow = BufferingWindow.create(input.length);
        var output = new AtomicReference<RealVector>();
        var system = dspSystem(input, filter, bufferingWindow, output);
        var oscillator = MainOscillator.create();
        system.connectTo(oscillator);
        oscillator.oscillate(input.length);
        return output.get().asSignal().samples(input.length, 0, 1);
    }

    private DspSystem dspSystem(double[] input, DspFilter<RealValue, RealValue> filter, DspFilter<RealValue, RealVector> bufferingWindow, AtomicReference<RealVector> output) {
        return new DspSystem(1) {
            {
                from(source(input))
                    .through(filter)
                    .through(bufferingWindow)
                    .into(sink(output));
            }

        };
    }

    private DspSource<RealValue> source(double[] input) {
        var index = new AtomicInteger(-1);
        var conductivity = new Conductivity(index::incrementAndGet);
        return DspSource.create(() -> {
            conductivity.conduct();
            return input[index.get()];
        }, conductivity);
    }

    private DspSink<RealVector> sink(AtomicReference<RealVector> output) {
        return vector -> {
            output.set(vector);
            return DspUnit.create();
        };
    }

}
