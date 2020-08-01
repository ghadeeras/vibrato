package vibrato.functions;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class Curve {

    public static final SlopeFunction envelope = new EnvelopeFunction();

    public static final CurveFunction linear = new LinearFunction();
    public static final CurveFunction smooth = new SmoothFunction();

    public static Builder from(double x, double y) {
        return from(x, y, 0);
    }

    public static Builder from(double x, double y, double slope) {
        return new Builder(new Point(x, y, slope));
    }

    public static class Builder {

        private final List<Point> points = new ArrayList<>();

        private Builder(Point point) {
            points.add(point);
        }

        public Builder to(double x, double y) {
            return to(x, y, 0);
        }

        public Builder to(double x, double y, double slope) {
            points.add(new Point(x, y, slope));
            points.sort(comparing(point -> point.x));
            return this;
        }

        public Builder slopedAs(SlopeFunction slopeFunction) {
            for (int i = 0; i < points.size(); i++) {
                points.set(i, points.get(i).withSlope(slopeFunction.slope(i, points)));
            }
            return this;
        }

        public RealFunction curve(CurveFunction curveFunction) {
            return x -> evaluateFor(x, pointArray(), subCurves(curveFunction));
        }

        public RealFunction slidingCurve(CurveFunction curveFunction) {
            return new Sliding(pointArray(), subCurves(curveFunction));
        }

        private Point[] pointArray() {
            return points.toArray(Point[]::new);
        }

        private RealFunction[] subCurves(CurveFunction curveFunction) {
            var curves = new RealFunction[points.size() + 1];
            for (int i = 0; i <= points.size(); i++) {
                curves[i] = curveFunction.curve(i, points);
            }
            return curves;
        }

    }

    private static double evaluateFor(double x, Point[] sortedPoints, RealFunction[] curves) {
        int i = 0;
        while (i < sortedPoints.length && x > sortedPoints[i].x) {
            i++;
        }
        return curves[i].apply(x);
    }

    private static class Point {

        public final double x;
        public final double y;
        public final double slope;

        private Point(double x, double y, double slope) {
            this.x = x;
            this.y = y;
            this.slope = slope;
        }

        private Point withSlope(double slope) {
            return new Point(x, y, slope);
        }

    }

    public interface SlopeFunction {

        double slope(int index, List<Point> points);

    }

    public interface CurveFunction {

        RealFunction curve(int index, List<Point> points);

    }

    private static class EnvelopeFunction implements SlopeFunction {

        @Override
        public double slope(int index, List<Point> points) {
            if (points.size() <= 1 || index >= points.size() - 1) {
                return 0;
            } else if (index <= 0) {
                var slope12 = points.size() >= 3 ? slopeBetween(points.get(1), points.get(2)) : 0;
                return (3 * slopeBetween(points.get(0), points.get(1)) - slope12) / 2;
            } else {
                var p = points.get(index);
                return (slopeBetween(points.get(index - 1), p) + slopeBetween(p, points.get(index + 1))) / 2;
            }
        }

        private double slopeBetween(Point p1, Point p2) {
            return (p2.y - p1.y) / (p2.x - p1.x);
        }

    }

    private static class SmoothFunction implements CurveFunction {

        @Override
        public RealFunction curve(int index, List<Point> points) {
            if (index <= 0) {
                return lineThrough(points.get(0));
            } else if (index >= points.size()) {
                return lineThrough(points.get(points.size() - 1));
            } else {
                return curveBetween(points.get(index - 1), points.get(index));
            }
        }

        private RealFunction lineThrough(Point point) {
            return Linear.linear(point.slope, point.y - point.slope * point.x);
        }

        private RealFunction curveBetween(Point point1, Point point2) {
            var z = Linear.linear(point1.x, 0, point2.x, 1);
            var deltaX = point2.x - point1.x;
            var deltaY = point2.y - point1.y;
            var s1 = point1.slope * deltaX;
            var s2 = point2.slope * deltaX;
            return z.then(Poly.poly(
                s1 + s2 - 2 * deltaY,
                3 * deltaY - 2 * s1 - s2,
                s1,
                point1.y
            ));
        }

    }

    private static class LinearFunction implements CurveFunction {

        @Override
        public RealFunction curve(int index, List<Point> points) {
            if (index <= 0) {
                return Linear.constant(points.get(0).y);
            } else if (index >= points.size()) {
                return Linear.constant(points.get(points.size() - 1).y);
            } else {
                return lineBetween(points.get(index - 1), points.get(index));
            }
        }

        public RealFunction lineBetween(Point p1, Point p2) {
            return Linear.linear(p1.x, p1.y, p2.x, p2.y);
        }

    }

    private static class Sliding implements RealFunction {

        private final Point[] points;
        private final RealFunction[] curves;

        private int i = 0;

        private Sliding(Point[] points, RealFunction[] curves) {
            this.points = points;
            this.curves = curves;
        }

        @Override
        public double apply(double x) {
            while (i > 0 && x <= points[i - 1].x) {
                i--;
            }
            while (i < points.length && x > points[i].x) {
                i++;
            }
            return curves[i].apply(x);
        }

    }

}
