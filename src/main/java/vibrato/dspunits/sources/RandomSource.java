package vibrato.dspunits.sources;

import vibrato.dspunits.DspSource;
import vibrato.dspunits.filters.Wire;
import vibrato.vectors.RealValue;

import java.util.Random;

public class RandomSource {

    public static DspSource<RealValue> uniform(Long seed) {
        return instance(seed, false);
    }

    public static DspSource<RealValue> gaussian(Long seed) {
        return instance(seed, true);
    }

    private static DspSource<RealValue> instance(Long seed, boolean gaussian) {
        return new Wire(DspSource.create(randomValue(seed, gaussian)).output());
    }

    private static RealValue randomValue(Long seed, boolean gaussian) {
        Random random = seed != null ? new Random(seed) : new Random();
        return gaussian ? random::nextGaussian : random::nextDouble;
    }

}
