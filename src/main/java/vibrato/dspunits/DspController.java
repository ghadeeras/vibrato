package vibrato.dspunits;

import vibrato.vectors.RealVector;

import java.util.function.Function;

public interface DspController<
        C extends RealVector,
        I extends RealVector,
        O extends RealVector
    > extends Function<C, DspFilter<I, O>> {

    default DspController<I, C, O> flip() {
        return  i -> c -> apply(c).apply(i);
    }

}
