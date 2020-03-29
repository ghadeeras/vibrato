package vibrato.complex;

import vibrato.vectors.Buffer;
import vibrato.vectors.RealVector;

/**
 * This class allows manipulating an array of complex numbers. Usually a process manipulates a limited number of complex
 * numbers at a time. So, it could create a minimal number of Pointer objects into the complex buffer, and slides these
 * pointer along the length of the buffer. These pointers will represent the complex numbers they point to.
 *
 * Say you have to implement an operation that reverses the order of complex numbers in a buffer. This can be one as
 * follows:
 * <pre>{@code
 *  Runnable reverserOf(ComplexBuffer buffer) {
 *      // Construction-time
 *      ComplexNumber c = new ComplexNumber();
 *      ComplexBuffer.Pointer p1 = buffer.pointer();
 *      ComplexBuffer.Pointer p2 = buffer.pointer();
 *      return () -> {
 *          // Processing-time
 *          for (int i = 0; i < buffer.size() / 2; i++) {
 *              p1.slideTo(i);
 *              p2.slideTo(buffer.size() - 1 - i);
 *              c.set(p1);
 *              p1.set(p2);
 *              p2.set(c);
 *          }
 *      }
 *  }
 * }</pre>
 * Notice that every time the Runnable returned by reverserOf() runs no instantiation of new complex numbers or pointers
 * happen as they were pre-instantiated when constructing the runnable.
 */
public class ComplexBuffer {

    private final int size;

    private final double[] realParts;
    private final double[] imaginaryParts;

    private final Buffer realBuffer;
    private final Buffer imaginaryBuffer;

    public ComplexBuffer(int size) {
        this.size = size;

        realParts = new double[size];
        imaginaryParts = new double[size];

        realBuffer = new Buffer(realParts);
        imaginaryBuffer = new Buffer(imaginaryParts);
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
