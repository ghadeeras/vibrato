package vibrato.functions;

public class Pulse {

    public static Signal pulse(double duration, double delay, double magnitude, double bias) {
        double s0 = bias;
        double s1 = bias + magnitude;
        double edge01 = delay;
        double edge10 = delay + duration;
        return x -> (x >= edge01 && x <= edge10 ? s1 : s0);
    }

    public static Signal pulse() {
        return pulse(1, 0, 1, 0);
    }

    public static Signal step(double delay, double magnitude, double bias) {
        double s0 = bias;
        double s1 = bias + magnitude;
        return x -> (x >= delay ? s1 : s0);
    }

    public static Signal step() {
        return step(0, 1, 0);
    }

    public static Signal impulse(double delay, double magnitude, double bias) {
        double s0 = bias;
        double s1 = bias + magnitude;
        return x -> x == delay ? s1 : s0;
    }

    public static Signal impulse() {
        return impulse(0, 1, 0);
    }

}
