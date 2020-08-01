package vibrato.functions;

import java.util.stream.DoubleStream;

public class Poly {

    public static RealFunction poly(double... coefficients) {
        var cs = DoubleStream.of(coefficients).dropWhile(c -> c == 0).toArray();
        switch (cs.length) {
            case 0:
                return Linear.constant(0);
            case 1:
                return Linear.constant(cs[0]);
            case 2:
                return Linear.linear(cs[0], cs[1]);
            default:
                return x -> evaluateFor(x, cs);
        }
    }

    private static double evaluateFor(double x, double[] cs) {
        var result = cs[0];
        for (var i = 1; i < cs.length; i++) {
            result *= x;
            result += cs[i];
        }
        return result;
    }

}
