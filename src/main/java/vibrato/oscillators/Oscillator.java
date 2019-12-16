package vibrato.oscillators;

import vibrato.system.DspSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static vibrato.utils.DspUtils.greatestCommonDivisor;

public abstract class Oscillator {

    private final List<Operation> operations = new ArrayList<>();
    private final DspSystem system;

    Oscillator(DspSystem system) {
        this.system = system;
    }

    public void triggers(Operation... operations) {
        for (Operation operation : operations) {
            this.system.declareConnection(operation.state(), this);
            this.operations.add(operation);
        }
    }

    protected void readPhase() {
        for (Operation operation : operations) {
            operation.readPhase();
        }
    }

    protected void writePhase() {
        for (Operation operation : operations) {
            operation.writePhase();
        }
    }

    protected Stream<State> states() {
        return operations.stream()
            .map(Operation::state)
            .flatMap(state -> state instanceof Oscillator ? states((Oscillator) state) : Stream.of(state));
    }

    private Stream<State> states(Oscillator oscillator) {
        return oscillator.states();
    }

    public DspSystem system() {
        return system;
    }

    public abstract int absolutePeriod();

    public abstract int absolutePhase();

    public Oscillator oscillator(int period) {
        return oscillator(period, 0);
    }

    public Oscillator oscillator(int period, int phase) {
        return new SlaveOscillator(this, period, phase);
    }

    public boolean conflictsWith(Oscillator that) {
        int phase = this.absolutePhase() - that.absolutePhase();
        int minShift = greatestCommonDivisor(this.absolutePeriod(), that.absolutePeriod());
        return phase % minShift == 0;
    }

    public String toString() {
        String op = absolutePhase() > 0 ? " - " : " + ";
        String s1 = absolutePhase() != 0 ? "(n" + op + absolutePhase() + ")" : "n";
        String s2 = absolutePeriod() != 1 ? " / " + absolutePeriod() : "";
        return "Oscillator @ " + s1 + s2;
    }

    public interface Operation {

        State state();

        void readPhase();

        void writePhase();

    }

    public interface State {
    }

}
