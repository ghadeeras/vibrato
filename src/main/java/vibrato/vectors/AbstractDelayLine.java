package vibrato.vectors;

import static vibrato.oscillators.Oscillator.Operation;
import static vibrato.oscillators.Oscillator.State;

public abstract class AbstractDelayLine implements RealVector, State {

    protected abstract void insert(double value);

    protected abstract void rotate(int count);

    final void rotate() {
        rotate(1);
    }

    public Operation rotation(int count) {
        return new Rotation(count);
    }

    public Operation readingFrom(RealValue input) {
        return new ReadingFrom(input);
    }

    private class Rotation implements Operation {

        private final int count;

        private Rotation(int count) {
            this.count = count;
        }

        @Override
        public State state() {
            return AbstractDelayLine.this;
        }

        @Override
        public void readPhase() {
        }

        @Override
        public void writePhase() {
            rotate(count);
        }

    }

    private class ReadingFrom implements Operation {

        private final RealValue input;

        private double value;

        private ReadingFrom(RealValue input) {
            this.input = input;
        }

        @Override
        public State state() {
            return AbstractDelayLine.this;
        }

        @Override
        public void readPhase() {
            value = input.value();
        }

        @Override
        public void writePhase() {
            insert(value);
        }

    }

}
