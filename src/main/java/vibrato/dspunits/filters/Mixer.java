package vibrato.dspunits.filters;

import vibrato.dspunits.DspFilter;
import vibrato.functions.DiscreteRealFunction;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class Mixer {

    public static DspFilter<RealVector, RealVector> matrix(double[][] matrix) {
        return input -> {
            DiscreteRealFunction outputFunction = i -> dotProduct(input, matrix[i]);
            return new Line(outputFunction.window(matrix.length));
        };
    }

    public static DspFilter<RealVector, RealValue> weightedSum(double[] weights) {
        return input -> new Wire(() -> dotProduct(input, weights));
    }

    public static DspFilter<RealVector, RealValue> weightedAverage(double[] weights) {
        double sum = sum(weights);
        double factor = sum != 0 ? 1 / sum : 1;
        return weightedSum(scaled(weights, factor));
    }

    public static DspFilter<RealVector, RealValue> sum() {
        return input -> new Wire(() -> sum(input));
    }

    public static DspFilter<RealVector, RealValue> average() {
        return input -> new Wire(() -> sum(input) / input.size());
    }

    private static double dotProduct(RealVector input, double[] vector) {
        double result = 0;
        for (int i = 0; i < vector.length; i++) {
            result += vector[i] * input.value(i);
        }
        return result;
    }

    private static double sum(RealVector input) {
        double result = 0;
        for (int i = 0; i < input.size(); i++) {
            result += input.value(i);
        }
        return result;
    }

    private static double[] scaled(double[] vector, double factor) {
        double[] scaledVector = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            scaledVector[i] = factor * vector[i];
        }
        return scaledVector;
    }

    private static double sum(double[] weights) {
        double sum = 0;
        for (double weight : weights) {
            sum += weight;
        }
        return sum;
    }

}
