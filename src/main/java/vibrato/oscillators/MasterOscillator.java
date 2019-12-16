package vibrato.oscillators;

import vibrato.system.DspSystem;

import java.util.IdentityHashMap;
import java.util.Map;

public class MasterOscillator extends Oscillator {

    private static final Map<DspSystem, MasterOscillator> existingSystems = new IdentityHashMap<>();

    private boolean debugMode = false;

    public MasterOscillator(DspSystem system) {
        super(system);
        assertMasterUniqueness(system);
    }

    private void assertMasterUniqueness(DspSystem system) {
        if (existingSystems.put(system, this) != null) {
            throw new RuntimeException("One system cannot have more than one master oscillator!");
        }
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

    @Override
    public int absolutePeriod() {
        return 1;
    }

    @Override
    public int absolutePhase() {
        return 0;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

}
