package vibrato.dspunits;

import vibrato.vectors.RealVector;

import java.util.function.Function;

public interface DspSink<V extends RealVector> extends Function<V, DspUnit> {
}
