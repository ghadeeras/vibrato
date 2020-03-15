package vibrato.oscillators;

public class SlaveOscillator extends Oscillator implements State {

    private final Oscillator parent;
    private final int period;
    private final int phase;
    private final int absolutePeriod;
    private final int absolutePhase;

    private int t = 0;

    public SlaveOscillator(Oscillator parent, int period, int phase) {
        super(parent.clockSpeed() / period);
        this.parent = parent;
        this.period = period;
        this.phase = phase % period;
        this.absolutePeriod = period * parent.absolutePeriod();
        this.absolutePhase = phase * parent.absolutePeriod() + parent.absolutePhase();
        parent.triggers(new Oscillation());
    }

    @Override
    protected void declareConnection(State state, Oscillator oscillator) {
        parent.declareConnection(state, oscillator);
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
