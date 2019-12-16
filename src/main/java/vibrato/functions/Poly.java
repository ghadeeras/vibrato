package vibrato.functions;

public class Poly {

    public static RealFunction poly(double... coefficients) {
        return x -> {
            double result = coefficients[0];
            for (int i = 1; i < coefficients.length; i++) {
                result *= x;
                result += coefficients[i];
            }
            return result;
        };
    }

}
