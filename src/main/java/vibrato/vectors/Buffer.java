package vibrato.vectors;

public class Buffer implements RealVector {

    private final double[] values;
    private final int lastIndex;

    public Buffer(double... values) {
        this.values = values;
        this.lastIndex = values.length - 1;
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public double firstValue() {
        return values[0];
    }

    @Override
    public double lastValue() {
        return values[lastIndex];
    }

    @Override
    public double value(int index) {
        return index >= 0 && index <= lastIndex ? values[index] : 0;
    }

}
