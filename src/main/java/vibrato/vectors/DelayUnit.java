package vibrato.vectors;

public class DelayUnit extends AbstractDelayLine implements RealValue {

    private double value;

    public DelayUnit() {
        this(0D);
    }

    public DelayUnit(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    protected void insert(double value) {
        this.value = value;
    }

    @Override
    protected void rotate(int count) {
    }

}
