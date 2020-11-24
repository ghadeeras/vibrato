package vibrato.oscillators;

import java.util.ArrayList;
import java.util.List;

import static vibrato.utils.DspUtils.greatestCommonDivisor;

public abstract class Oscillator {

    private final List<Operation> operations = new ArrayList<>();

    protected Oscillator() {
    }

    protected abstract void declareConnection(State state, Oscillator oscillator);

    public void triggers(Operation operation) {
        declareConnection(operation.state(), this);
        this.operations.add(operation);
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

}
