package vibrato.functions;

public class Sinc {

    public static Signal sinc() {
        return x -> x != 0 ? calculate(x) : 1;
    }

    private static double calculate(double x) {
        double a = Math.PI * x;
        return Math.sin(a) / a;
    }

}