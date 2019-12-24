package vibrato.vectors;

public class CircularBuffer extends AbstractDelayLine {

    private final double[] values;

    private int firstIndex;
    private int lastIndex;

    public CircularBuffer(int size) {
        this(new double[size]);
    }

    public CircularBuffer(double[] values) {
        this.values = values;
        this.firstIndex = 0;
        this.lastIndex = this.values.length - 1;
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public double firstValue() {
        return values[firstIndex];
    }

    @Override
    public double lastValue() {
        return values[lastIndex];
    }

    @Override
    public double value(int index) {
        return values[modSize(firstIndex + index)];
    }

    @Override
    protected void insert(double value) {
        rotate();
        values[lastIndex] = value;
    }

    @Override
    protected void rotate(int count) {
        lastIndex = firstIndex;
        firstIndex = modSize(firstIndex + count);
    }

    private int modSize(int index) {
        int i = index % values.length;
        return i >= 0 ? i : i + values.length;
    }

}
