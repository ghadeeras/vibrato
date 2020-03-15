package vibrato.oscillators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MasterOscillator extends Oscillator {

    public final Map<State, List<Oscillator>> stateToOscillators = new HashMap<>();

    public MasterOscillator(double clockSpeed) {
        super(clockSpeed);
    }

    @Override
    protected void declareConnection(State state, Oscillator oscillator) {
        List<Oscillator> oscillators = stateToOscillators.computeIfAbsent(state, s -> new LinkedList<>());
        oscillators.stream().filter(oscillator::conflictsWith).findFirst().ifPresent(conflictingOscillator -> {
            throw new RuntimeException("Oscillators conflict: " + oscillator + " and " + conflictingOscillator);
        });
        oscillators.add(oscillator);
    }

    private void cycle() {
        readPhase();
        writePhase();
    }

    public void oscillate(long cycles) {
        for (long i = 0; i < cycles; i++) {
            cycle();
        }
    }

    public void run(long cycles) {
        oscillate(cycles);
    }

    @Override
    public int absolutePeriod() {
        return 1;
    }

    @Override
    public int absolutePhase() {
        return 0;
    }

}
