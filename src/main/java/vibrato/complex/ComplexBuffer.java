package vibrato.complex;

import vibrato.vectors.Buffer;
import vibrato.vectors.RealVector;

public class ComplexBuffer {

    private final int size;

    private final double[] xs;
    private final double[] ys;

    private final Buffer xBuffer;
    private final Buffer yBuffer;

    public ComplexBuffer(int size) {
        this.size = size;

        xs = new double[size];
        ys = new double[size];

        xBuffer = new Buffer(xs);
        yBuffer = new Buffer(ys);
    }

    public ComplexBuffer(Complex[] complexNumbers) {
        this(complexNumbers.length);
        for (int i = complexNumbers.length - 1; i >= 0; i--) {
            set(i, complexNumbers[i]);
        }
    }

    public ComplexBuffer(ComplexBuffer complexBuffer) {
        this(complexBuffer.xs.length);
        System.arraycopy(complexBuffer.xs, 0, xs, 0, xs.length);
        System.arraycopy(complexBuffer.ys, 0, ys, 0, ys.length);
    }

    public int size() {
        return size;
    }

    public RealVector xs() {
        return xBuffer;
    }

    public RealVector ys() {
        return yBuffer;
    }

    private void set(int index, Complex complexNumber) {
        set(index, complexNumber.x(), complexNumber.y());
    }

    private void set(int index, double x, double y) {
        xs[index] = x;
        ys[index] = y;
    }

    public Pointer pointer() {
        return pointer(0);
    }

    public Pointer pointer(int index) {
        return new Pointer(this, index);
    }

    public static class Pointer implements Complex<Pointer> {

        private final ComplexBuffer buffer;

        private int index;

        public Pointer(ComplexBuffer buffer, int index) {
            this.buffer = buffer;
            this.index = index;
        }

        public ComplexBuffer buffer() {
            return buffer;
        }

        @Override
        public double x() {
            return buffer.xs[index];
        }

        @Override
        public double y() {
            return buffer.ys[index];
        }

        @Override
        public Pointer setXY(double x, double y) {
            buffer.set(index, x, y);
            return this;
        }

        public Pointer slideTo(int index) {
            this.index = index;
            return this;
        }

    }

}
