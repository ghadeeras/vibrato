package vibrato.functions;

public class Sinusoid {

    public static Signal sin() {
        return Math::sin;
    }

    public static Signal sin(double period, double phase, double amplitude, double bias) {
        double frequency = 2 * Math.PI / period;
        return sin().compress(frequency).delay(phase).magnify(amplitude).bias(bias);
    }

}
