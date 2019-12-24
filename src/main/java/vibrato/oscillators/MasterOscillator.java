package vibrato.oscillators;

public class MasterOscillator extends Oscillator {

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

}
