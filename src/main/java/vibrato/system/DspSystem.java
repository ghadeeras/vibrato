package vibrato.system;

import vibrato.dspunits.DspUnit;
import vibrato.oscillators.MasterOscillator;
import vibrato.oscillators.Operation;
import vibrato.oscillators.Oscillator;
import vibrato.oscillators.State;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class DspSystem<O extends Oscillator> {

    protected final O oscillator;
    protected final double clockSpeed;
    protected final double zHertz;
    protected final double zSecond;

    private DspSystem(O oscillator, double clockSpeed) {
        this.oscillator = oscillator;
        this.clockSpeed = clockSpeed;
        this.zHertz = 2 * Math.PI / clockSpeed;
        this.zSecond = 1 / zHertz;
    }

    <U extends DspUnit> U connect(U unit) {
        for (Operation operation : unit.operations()) {
            declareConnection(operation.state(), oscillator);
            oscillator.triggers(operation);
        }
        return unit;
    }

    protected abstract void declareConnection(State state, Oscillator oscillator);

    public Slave subsystem(int period, int phase) {
        return new Slave(this, period, phase);
    }

    public double clockSpeed() {
        return clockSpeed;
    }

    public double zHertz() {
        return zHertz;
    }

    public double zSecond() {
        return zSecond;
    }

    public static class Master extends DspSystem<MasterOscillator> {

        public final Map<State, List<Oscillator>> stateToOscillators = new HashMap<>();

        public Master(double clockSpeed) {
            super(new MasterOscillator(), clockSpeed);
        }

        @Override
        protected void declareConnection(State state, Oscillator oscillator) {
            List<Oscillator> oscillators = stateToOscillators.computeIfAbsent(state, s -> new LinkedList<>());
            oscillators.stream().filter(oscillator::conflictsWith).findFirst().ifPresent(conflictingOscillator -> {
                throw new RuntimeException("Oscillators conflict: " + oscillator + " and " + conflictingOscillator);
            });
            oscillators.add(oscillator);
        }

        public void run(long cycles) {
            oscillator.oscillate(cycles);
        }

    }

    public static class Slave extends DspSystem<Oscillator> {

        private final DspSystem<?> parent;

        private Slave(DspSystem<?> parent, int period, int phase) {
            super(parent.oscillator.oscillator(period, phase), parent.clockSpeed / period);
            this.parent = parent;
        }

        @Override
        protected void declareConnection(State state, Oscillator oscillator) {
            parent.declareConnection(state, oscillator);
        }

    }

}
