package vibrato.dspunits;

import vibrato.vectors.RealVector;

import java.util.function.Function;

public interface DspFilter<I extends RealVector, O extends RealVector> extends Function<I, DspSource<O>> {
}
