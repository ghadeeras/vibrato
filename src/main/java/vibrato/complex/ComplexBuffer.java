package vibrato.complex;

import vibrato.vectors.Buffer;
import vibrato.vectors.RealVector;

public class ComplexBuffer {

    private final int size;

    private final double[] realParts;
    private final double[] imaginaryParts;

    private final Buffer realBuffer;
    private final Buffer imaginaryBuffer;

    private final RealVector lengths;
    private final RealVector angles;

    public ComplexBuffer(int size) {
        this.size = size;

        realParts = new double[size];
        imaginaryParts = new double[size];

        realBuffer = new Buffer(realParts);
        imaginaryBuffer = new Buffer(imaginaryParts);

        lengths = RealVector.window(size, i -> length(realParts[i], imaginaryParts[i]));
        angles = RealVector.window(size, i -> Math.atan2(imaginaryParts[i], realParts[i]));
    }

    private static double length(double real, double imaginary) {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public int size() {
        return size;
    }

    public RealVector realParts() {
        return realBuffer;
    }

    public RealVector imaginaryParts() {
        return imaginaryBuffer;
    }

    public RealVector lengthParts() {
        return lengths;
    }

    public RealVector angleParts() {
        return angles;
    }

    private void set(int index, double real, double imaginary) {
        realParts[index] = real;
        imaginaryParts[index] = imaginary;
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
        public double real() {
            return buffer.realParts[index];
        }

        @Override
        public double imaginary() {
            return buffer.imaginaryParts[index];
        }

        @Override
        public Pointer setRI(double real, double imaginary) {
            buffer.set(index, real, imaginary);
            return this;
        }

        public Pointer slideTo(int index) {
            this.index = index;
            return this;
        }

        @Override
        public String toString() {
            return toRIString();
        }

    }

}
