package vibrato.functions;

public class Linear {

    public static RealFunction constant(double c) {
        return x -> c;
    }

    public static RealFunction identity() {
        return x -> x;
    }

    public static RealFunction neg() {
        return x -> -x;
    }

    public static RealFunction linear(double slope) {
        return slope == 0 ?
            constant(0) :
            slope == 1 ?
                identity() :
                slope == -1 ?
                    neg() :
                    x -> slope * x;
    }

    public static RealFunction linear(double slope, double y0) {
        return slope == 0 ?
            constant(y0) :
            y0 == 0 ?
                linear(slope) :
                x -> slope * x + y0;
    }

    public static RealFunction linear(double x1, double y1, double x2, double y2) {
        double slope = (y2 - y1) / (x2 - x1);
        double y0 = y1 - x1 * slope;
        return linear(slope, y0);
    }

}
