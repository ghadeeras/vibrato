package vibrato.functions;

import java.util.function.IntUnaryOperator;

public class Saturation {

    public static IntUnaryOperator clamp(int min, int max) {
        return x -> x <= min ? min : (x >= max ? max : x);
    }

    public static RealFunction clamp(double min, double max) {
        return x -> x <= min ? min : (x >= max ? max : x);
    }

}
