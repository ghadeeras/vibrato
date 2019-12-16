package vibrato.vectors;

public interface RealValue extends RealVector {

    @Override
    default int size() {
        return 1;
    }

    @Override
    default double firstValue() {
        return value();
    }

    @Override
    default double lastValue() {
        return value();
    }

    @Override
    default double value(int index) {
        return value();
    }

    double value();

}
