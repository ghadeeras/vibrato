package vibrato.dspunits.sources;

import vibrato.dspunits.DspUnit;
import vibrato.functions.DiscreteSignal;
import vibrato.vectors.Buffer;
import vibrato.vectors.RealValue;

import java.util.concurrent.atomic.AtomicBoolean;

import static vibrato.oscillators.Oscillator.Operation;
import static vibrato.oscillators.Oscillator.State;

public class WaveSource extends DspUnit implements RealValue, State {

    private final Buffer wave;
    private final boolean periodic;

    private final Generation generation = new Generation();
    private final AtomicBoolean reset = new AtomicBoolean(true);

    private int time = 0;

    public WaveSource(DiscreteSignal signal, int length, boolean periodic) {
        this.wave = new Buffer(signal.samples(length, 0, 1));
        this.periodic = periodic;
    }

    private void next() {
        time = mustReset() ? 0 : time + 1;
        if (time >= wave.size()) {
            time = periodic ? 0 : wave.size();
        }
    }

    private boolean mustReset() {
        return reset.getAndSet(false);
    }

    public void reset() {
        reset.set(true);
    }

    @Override
    protected Operation[] operations() {
        return ops(generation);
    }

    @Override
    public double value() {
        return wave.value(time);
    }

    private class Generation implements Operation {

        @Override
        public State state() {
            return WaveSource.this;
        }

        @Override
        public void readPhase() {
        }

        @Override
        public void writePhase() {
            next();
        }

    }

}
