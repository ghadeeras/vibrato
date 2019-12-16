package vibrato.dspunits.sources;

import vibrato.dspunits.Wire;
import vibrato.vectors.RealValue;

import java.util.Random;

public class RandomSource extends Wire {

    public RandomSource(Long seed, boolean gaussian) {
        super(randomValue(seed, gaussian));
    }

    private static RealValue randomValue(Long seed, boolean gaussian) {
        Random random = seed != null ? new Random(seed) : new Random();
        return gaussian ? random::nextGaussian : random::nextDouble;
    }

}
