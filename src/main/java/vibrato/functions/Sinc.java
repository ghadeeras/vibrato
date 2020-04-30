package vibrato.functions;

public class Sinc {

    private static final double epsilon = Math.sqrt(Math.nextUp(0f));

    public static Signal sinc() {
        return x -> -epsilon < x && x < epsilon ? 1: calculate(x);
    }

    private static double calculate(double x) {
        double a = Math.PI * x;
        return Math.sin(a) / a;
    }

}