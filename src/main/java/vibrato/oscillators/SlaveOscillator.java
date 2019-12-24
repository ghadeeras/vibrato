package vibrato.oscillators;

public class SlaveOscillator extends Oscillator implements State {

    private final int period;
    private final int phase;
    private final int absolutePeriod;
    private final int absolutePhase;

    private int t = 0;

    SlaveOscillator(Oscillator oscillator, int period, int phase) {
        this.period = period;
        this.phase = phase % period;
        this.absolutePeriod = period * oscillator.absolutePeriod();
        this.absolutePhase = phase * oscillator.absolutePeriod() + oscillator.absolutePhase();
        oscillator.triggers(new Oscillation());
    }

    @Override
    public int absolutePeriod() {
        return absolutePeriod;
    }

    @Override
    public int absolutePhase() {
        return absolutePhase;
    }

    private class Oscillation implements Operation {

        @Override
        public State state() {
            return SlaveOscillator.this;
        }

        @Override
        public void readPhase() {
            if (t == phase) {
                SlaveOscillator.this.readPhase();
            }
        }

        @Override
        public void writePhase() {
            if (t == phase) {
                SlaveOscillator.this.writePhase();
            }
            t = (t + 1) % period;
        }

    }

}
