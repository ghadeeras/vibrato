package vibrato.utils;

import vibrato.complex.ComplexNumber;

public class DspUtils {

    public static int bitCount(int i) {
        int result = 0;
        while (i != 0) {
            i >>>= 1;
            result++;
        }
        return result;
    }

    public static int flipBits(int i, int bitCount) {
        int result = 0;
        while (bitCount > 0) {
            result <<= 1;
            result |= (i & 1);
            i >>>= 1;
            bitCount--;
        }
        return result;
    }

    public static int greatestCommonDivisor(int i1, int i2) {
        return i2 == 0 ? i1 : greatestCommonDivisor(i2, i1 % i2);
    }

    public static int leastCommonMultiple(int i1, int i2) {
        return i1 * (i2 / greatestCommonDivisor(i1, i2));
    }

    public static ComplexNumber[] getRoots(double a, double b, double c) {
        ComplexNumber[] result = null;
        if (a != 0) {
            double delta = b * b - 4 * a * c;
            double a2 = 2 * a;
            double b2a = -b / a2;
            if (delta < 0) {
                result = new ComplexNumber[2];
                double delta2a = Math.sqrt(-delta) / a2;
                result[0] = ComplexNumber.createXY(b2a, +delta2a);
                result[1] = ComplexNumber.createXY(b2a, -delta2a);
            } else if (delta > 0) {
                result = new ComplexNumber[2];
                double delta2a = Math.sqrt(delta) / a2;
                result[0] = ComplexNumber.createXY(b2a + delta2a, 0);
                result[1] = ComplexNumber.createXY(b2a - delta2a, 0);
            } else {
                result = new ComplexNumber[1];
                result[0] = ComplexNumber.createXY(b2a, 0);
            }
        } else if (b != 0) {
            result = new ComplexNumber[1];
            result[0] = ComplexNumber.createXY(-c/b, 0);
        } else if (c != 0) {
            result = new ComplexNumber[0];
        }
        return result;
    }

}
